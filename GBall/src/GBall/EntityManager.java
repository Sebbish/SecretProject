package GBall;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import Msg.MsgData;
import Msg.Vector2D;

public class EntityManager {
	//////////////
	class connection{
		public final InetAddress m_address;
		public final int m_port;
		public connection(InetAddress address, int port){
			m_address = address;
			m_port = port;
		}
	};
	private static ArrayList<connection> m_connection = new ArrayList<connection>();
	///////////
	private static LinkedList<GameEntity> m_entities = new LinkedList<GameEntity>();

	DatagramSocket m_socket;
	public static final int SERVERPORT = 25000;
	public static final int MAXPLAYERS = 4;

	private static class SingletonHolder {
		public static final EntityManager instance = new EntityManager();
	}

	public static EntityManager getInstance() {
		return SingletonHolder.instance;
	}

	private EntityManager() {
		try {
			m_socket = new DatagramSocket(SERVERPORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*try {
			m_socket.setSoTimeout(200);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	public void addShip(final Vector2D position, final Vector2D speed, final Vector2D direction, final Color color, final KeyConfig kc) {
		m_entities.add(new Ship(position, speed, direction, color));
	}

	public void addBall(final Vector2D position, final Vector2D speed) {
		m_entities.add(new Ball(position, speed));
	}

	/////////////////////
	public void allConnected() throws IOException {
		int id = 0;
		while(id < 4){
		byte[] b = new byte[16];
		DatagramPacket packet = new DatagramPacket(b, b.length);
		try {
			m_socket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		if(m_connection.size() == 0){
			m_connection.add(new connection(packet.getAddress(),packet.getPort()));
		}else{
			for(int i = 0; i < m_connection.size();i++){
				if(!(m_connection.get(i).m_address == packet.getAddress() && m_connection.get(i).m_port == packet.getPort())){
					m_connection.add(new connection(packet.getAddress(),packet.getPort()));
					break;
				}
			}
		}
		
		
		int msg = ByteBuffer.wrap(b, 0, packet.getLength()).getInt();
		msg = id + msg;
		if (msg - 1 < MAXPLAYERS) {
			for (int i = id; i < msg; i++) {
				m_entities.get(i).connect(packet.getAddress(), packet.getPort());
				byte[] out = new byte[4];
				byte[] in = new byte[128];
				out = ByteBuffer.allocate(4).putInt(id+1).array();
				DatagramPacket send = new DatagramPacket(out, out.length, packet.getAddress(), packet.getPort());
				DatagramPacket get = new DatagramPacket(in, in.length);
				int msg1 = -1;
				int failTimer = 0;
				while (msg1 != 0 && failTimer < 5) {
					m_socket.send(send);
					m_socket.receive(get);
					msg1 = ByteBuffer.wrap(get.getData(), 0, packet.getLength()).getInt();
					if (msg1 == 0 && id < 4)
						id++;
					else
						failTimer++;
				}
			}
		} else {
			byte[] out = new byte[4];
			out = ByteBuffer.allocate(4).putInt(0).array();
			DatagramPacket send = new DatagramPacket(out, out.length, packet.getAddress(), packet.getPort());
			m_socket.send(send);
		}
		}
		m_socket.setSoTimeout(1);
		/*
		 * for (int i = id; i < id + msg; i++) { if
		 * (m_entities.get(i).isUsedByPlayer() && i < 4)
		 * m_entities.get(i).connect(packet.getAddress(), packet.getPort());
		 * byte[] by = new byte[4]; if (id < 4) by =
		 * ByteBuffer.allocate(4).putInt(id).array(); else by =
		 * ByteBuffer.allocate(4).putInt(0).array(); DatagramPacket send = new
		 * DatagramPacket(by, by.length); try { m_socket.receive(packet); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * //e.printStackTrace(); } int msg1 = ByteBuffer.wrap(b, 0,
		 * packet.getLength()).getInt(); if (msg1 == 0) id++; else i--; } id +=
		 * msg;
		 * 
		 * }
		 */
	}

	public void updateinput() {
		byte[] b = new byte[12];
		DatagramPacket packet = new DatagramPacket(b, b.length);

		try {
			m_socket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		int id = ByteBuffer.wrap(packet.getData(), 0, 4).getInt();
		int acc = ByteBuffer.wrap(packet.getData(), 4, 4).getInt();
		int dir = ByteBuffer.wrap(packet.getData(), 8, 4).getInt();
		if(id == 4)
			System.out.println("ID: " + id + ", ACC: " + acc + ", DIR: " + dir);
		if (id >= 1 && id <= 4) {
			if (m_entities.get(id-1).isUsedByPlayer() && m_entities.get(id-1).getAddress().getHostAddress().equals(packet.getAddress().getHostAddress()))
				m_entities.get(id-1).setInput(acc, dir);
		}
	}

	public void broadcastPosition() {
		MsgData msg = new MsgData(m_entities.get(0).getPosition(), m_entities.get(1).getPosition(), m_entities.get(2).getPosition(), m_entities.get(3).getPosition(), m_entities.get(0).getDirection(), m_entities.get(1).getDirection(), m_entities.get(2).getDirection(), m_entities.get(3).getDirection(), m_entities.get(4).getPosition(),ScoreKeeper.getInstance().getScoreAsVector());

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(msg);
			byte[] buf = baos.toByteArray();
			for (int i = 0; i < m_connection.size(); i++) {
					DatagramPacket pack = new DatagramPacket(buf, buf.length, m_connection.get(i).m_address, m_connection.get(i).m_port);
					//System.out.println("sent " + buf.length + " bytes: " + buf[0] + "," + buf[1] + "," + buf[2] + "," + buf[3]);
					m_socket.send(pack);
			}
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	///////////////////////////////

	public void updatePositions() {
		for (ListIterator<GameEntity> itr = m_entities.listIterator(0); itr.hasNext();) {
			itr.next().move();
		}
	}

	public void renderAll(Graphics g) {
		for (ListIterator<GameEntity> itr = m_entities.listIterator(0); itr.hasNext();) {
			itr.next().render(g);
		}
	}

	public void checkBorderCollisions(int screenWidth, int screenHeight) {
		double newX = 0.0, newY = 0.0, radius = 0;
		boolean reset = false;
		for (ListIterator<GameEntity> itr = m_entities.listIterator(0); itr.hasNext();) {
			GameEntity e = itr.next();
			newX = e.getPosition().getX();
			newY = e.getPosition().getY();
			radius = e.getRadius();

			if (newX + radius > (screenWidth - Const.WINDOW_BORDER_WIDTH)) {
				newX = screenWidth - radius - Const.WINDOW_BORDER_WIDTH;
				e.deflectX();
				if (e.givesPoints()) {
					ScoreKeeper.getInstance().changeScores(1, 0);
					reset = true;
				}
			} else if ((newX - e.getRadius()) < Const.WINDOW_BORDER_WIDTH) {
				newX = radius + Const.WINDOW_BORDER_WIDTH;
				e.deflectX();
				if (e.givesPoints()) {
					ScoreKeeper.getInstance().changeScores(0, 1);
					reset = true;
				}
			}

			if (newY + radius > (screenHeight - Const.WINDOW_BOTTOM_HEIGHT)) {
				newY = screenHeight - radius - Const.WINDOW_BOTTOM_HEIGHT;
				e.deflectY();
			} else if (newY - radius < Const.WINDOW_TOP_HEIGHT) {
				newY = radius + Const.WINDOW_TOP_HEIGHT;
				e.deflectY();
			}

			e.setPosition(newX, newY);
		}

		if (reset) {
			resetPositions();
		}
	}

	public void checkShipCollisions() {
		Vector2D v; // Vector from center of one ship to the other

		for (ListIterator<GameEntity> itr = m_entities.listIterator(0); itr.hasNext();) {
			GameEntity s1 = itr.next();
			if (itr.hasNext()) {
				for (ListIterator<GameEntity> itr2 = m_entities.listIterator(itr.nextIndex()); itr2.hasNext();) {
					GameEntity s2 = itr2.next();
					v = s1.getPosition().minusOperator(s2.getPosition());
					double dist = v.length();

					if (v.length() < (s1.getRadius() + s2.getRadius())) { // Simple collision detection; just assume that ships will overlap during collision
						// Displace ships to avoid drawing overlap
						// Simplification: just displace both ships an equal amount
						v.setLength((s1.getRadius() + s2.getRadius() - dist) / 2);
						s1.displace(v);
						v.invert();
						s2.displace(v);

						// Update movement vectors (assume perfect, rigid collision with no momentum loss and equal masses)
						v.makeUnitVector(); // Normalize v
						// Compute momentum along v
						double comp1 = s1.getSpeed().dotProduct(v);
						double comp2 = s2.getSpeed().dotProduct(v);
						double m = comp1 - comp2; // 2(comp1-comp2) / mass1 + mass2 = 2(comp1-comp2) / 2 = comp1 - comp2
						v.setLength(m);
						s2.changeSpeed(v);
						v.invert();
						s1.changeSpeed(v);
					}
				}
			}
		}
	}

	private void resetPositions() {
		for (ListIterator<GameEntity> itr = m_entities.listIterator(0); itr.hasNext();) {
			itr.next().resetPosition();
		}
	}

	public static LinkedList<GameEntity> getState() {

		return m_entities;
	}
}