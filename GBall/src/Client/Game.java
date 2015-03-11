package Client;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import GBall.Const;
import GBall.EntityManager;
import GBall.KeyConfig;
import Msg.MsgData;
import Msg.Vector2D;

public class Game {
	private static class GameSingletonHolder {
		public static final Game instance = new Game();
	}

	public static Game getInstance() {
		return GameSingletonHolder.instance;
	}

	DatagramSocket m_socket = null;
	InetAddress m_address = null;
	int m_port;
	int m_localPlayers;
	ArrayList<Integer> m_idList = new ArrayList<Integer>();
	ArrayList<Player> m_entities = new ArrayList<Player>();
	ArrayList<KeyClass> m_keys = new ArrayList<KeyClass>();

	private double m_actualFps = 0.0;

	private double m_lastTime = System.currentTimeMillis();

	private final GameWindow m_gameWindow = new GameWindow();

	public void process(DatagramSocket socket, InetAddress address, int port, int players) throws IOException, ClassNotFoundException {
		m_port = port;
		m_address = address;
		m_socket = socket;
		m_localPlayers = players;
		m_socket.setSoTimeout(2000);
		if(!handshake()){
			System.exit(-1);
		}
		initPlayers();
		setScreen();
		m_socket.setSoTimeout(0);
		getUpdate();
		m_socket.setSoTimeout(2);
		
		double startOfFrameTimer;

		while (true) {
			startOfFrameTimer = System.nanoTime();
			getUpdate();
			m_gameWindow.repaint();
			if (newFrame()) {
				sendOutput();
			}
			m_actualFps = 1000000000.0 / (System.nanoTime() - startOfFrameTimer);
		}
	}
	
	void setScreen(){
		m_gameWindow.getGraphics().setColor(Color.BLACK);
		m_gameWindow.getGraphics().fillRect(0, 0, m_gameWindow.getWidth(), m_gameWindow.getHeight());
		m_gameWindow.paint(m_gameWindow.getGraphics());
	}

	void getUpdate() throws IOException, ClassNotFoundException {
		//byte[] buf = new byte[256];
		byte[] buf = new byte[4096];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		try {
			//System.out.println("Getting update:");
			m_socket.receive(packet);
			//System.out.println("received " + packet.getLength() + " bytes: " + buf[packet.getOffset()+0] + "," + buf[packet.getOffset()+1] + "," + buf[packet.getOffset()+2] + "," + buf[packet.getOffset()+3]);
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("Timed out");
			return;
		}
		//ByteArrayInputStream bs = new ByteArrayInputStream(packet.getData());
		ByteArrayInputStream bs = new ByteArrayInputStream(buf);
		ObjectInputStream ois = new ObjectInputStream(bs);
		MsgData data = (MsgData) ois.readObject();
		for (int i = 0; i < Entities.getInstance().getEntities().size(); i++) {
			Entities.getInstance().getEntities().get(i).setPosition(data.getPosition(i + 1));
			Entities.getInstance().getEntities().get(i).setRotation(data.getRotation(i + 1));
		}
		ScoreKeeperClient.getInstance().changeScores((int) data.getScore().getX(), (int) data.getScore().getY());
		ois.close();
	}

	void sendOutput() {
		for (int i = 0; i < m_keys.size(); i++) {
			int[] out = m_keys.get(i).getOutput();
			System.out.println("Output: ID: " + (out[0]) + ", Acc: " + out[1] + ", Dir: " + out[2]);
			ByteBuffer b = ByteBuffer.allocate(12);
			b.putInt(0, out[0]);
			b.putInt(4, out[1]);
			b.putInt(8, out[2]);
			byte[] buf = b.array();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, m_address, m_port);
			try {
				m_socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	boolean handshake() throws IOException {
		System.out.println("handshake");
		byte[] buf = ByteBuffer.allocate(4).putInt(m_localPlayers).array();
		byte[] acc = ByteBuffer.allocate(4).putInt(0).array();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, m_address, m_port);
		DatagramPacket a = new DatagramPacket(acc, acc.length, m_address, m_port);
		m_socket.send(packet);
		for (int i = 0; i < m_localPlayers; i++) {
			m_idList.add(receiveID(packet));

			if (m_idList.get(i) == 0) {
				System.out.println("received a zero");
				return false;
			}
			System.out.println("Received ID: " + m_idList.get(i));
			if (i > 0)
				if (m_idList.get(i) == m_idList.get(i - 1)) {
					System.out.println("received the same ID");
					m_idList.remove(i);
					i--;
				}
			m_socket.send(a);

		}
		System.out.println(m_idList.size());
		return true;
	}

	int receiveID(DatagramPacket packet) throws IOException {
		int retries = 0;
		while (retries < 5) {
			try {
				m_socket.receive(packet);
				return ByteBuffer.wrap(packet.getData()).getInt();
			} catch (IOException e) { //Timeout
				retries++;
				m_socket.send(packet);
			}
		}
		System.out.println("Timeout");
		return 0;
	}

	private boolean newFrame() {
		double currentTime = System.currentTimeMillis();
		double delta = currentTime - m_lastTime;
		boolean rv = (delta > (1000 / 60));
		if (rv) {
			m_lastTime += (1000 / 60);
			if (delta > 10 * (1000 / 60)) {
				m_lastTime = currentTime;
			}
		}
		return rv;
	}

	private void initPlayers() {
		Color c;
		// Team 1
		if (m_idList.contains(1))
			c = Color.ORANGE;
		else
			c = Color.RED;
		m_entities.add(new Player(new Vector2D(200, 100), new Vector2D(1.0, 0.0), c));

		if (m_idList.contains(2))
			c = Color.PINK;
		else
			c = Color.RED;
		m_entities.add(new Player(new Vector2D(200, 758 - 100), new Vector2D(1.0, 0.0), c));

		// Team 2
		if (m_idList.contains(3))
			c = Color.BLUE;
		else
			c = Color.GREEN;
		m_entities.add(new Player(new Vector2D(1024 - 200, 100), new Vector2D(-1.0, 0.0), c));

		if (m_idList.contains(4))
			c = Color.GRAY;
		else
			c = Color.GREEN;
		m_entities.add(new Player(new Vector2D(1024 - 200, 758 - 100), new Vector2D(-1.0, 0.0), c));

		// Ball
		m_entities.add(new Player(new Vector2D(512, 379), new Vector2D(0.0, 0.0), Color.WHITE));

		Entities.getInstance().setEntities(m_entities);
		for (int i = 0; i < m_localPlayers; i++) {
			KeyConfig k = null;
			if (i == 0)
				k = new KeyConfig(KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_W);
			if (i == 1)
				k = new KeyConfig(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_UP);
			if (i == 2)
				k = new KeyConfig(KeyEvent.VK_J, KeyEvent.VK_L, KeyEvent.VK_K, KeyEvent.VK_I);
			if (i == 3)
				k = new KeyConfig(KeyEvent.VK_F, KeyEvent.VK_H, KeyEvent.VK_G, KeyEvent.VK_T);
			m_keys.add(new KeyClass(k, m_idList.get(i)));
			m_gameWindow.addKeyListener(m_keys.get(i));
		}
	}

	public double getActualFps() {

		return m_actualFps;
	}
}
