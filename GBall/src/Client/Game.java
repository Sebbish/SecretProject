package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

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
    ArrayList<Player> m_players;

    public void process(DatagramSocket socket, InetAddress address, int port, ArrayList<Player> players) throws IOException {
    	m_port = port;
    	m_address = address;
    	m_socket = socket;
    	m_players = players;
    	m_socket.setSoTimeout(200);
    	handshake();
    }
    
    boolean handshake() throws IOException {
    	byte[] buf = ByteBuffer.allocate(4).putInt(m_players.size()).array();
    	byte[] acc = ByteBuffer.allocate(4).putInt(0).array();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, m_address, m_port);
		DatagramPacket a = new DatagramPacket(acc, acc.length, m_address, m_port);
    	for (int i = 0; i < m_players.size(); i++) {
    		m_players.get(i).SetID(sendPacket(packet));
    		if (m_players.get(i).GetID() == 0) 
    			return false;
    		if (i > 0)
	    		if (m_players.get(i).GetID() == m_players.get(i-1).GetID())
	    			i--;
    		m_socket.send(a);
    	}
    	return true;
    }
    
    int sendPacket(DatagramPacket packet) throws IOException {
    	int retries = 0;
    	while (retries < 5) {
			m_socket.send(packet);
	    	try {
				m_socket.receive(packet);
				return ByteBuffer.wrap(packet.getData()).getInt();
			} catch (IOException e) { //Timeout
				retries++;
			}	
    	}
    	return 0;
    }
}
