package Client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import GBall.World;

public class Main {
	
	private DatagramSocket socket;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String hostName = args[0];
		int port = Integer.parseInt(args[1]);
		int localPlayers = Integer.parseInt(args[2]);
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < localPlayers && i < 4; i++) {
			players.add(new Player());
		}
		InetAddress address = null;
		try {
			address = InetAddress.getByName(hostName);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DatagramSocket socket = null;
		socket.connect(address, port);
		Game.getInstance().process(socket, address, port, players);
	}
}