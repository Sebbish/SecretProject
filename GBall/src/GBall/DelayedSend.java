package GBall;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class DelayedSend extends Thread {
	ArrayList<Message> m = new ArrayList<Message>();

	public DelayedSend() {
	}

	public void go(DatagramPacket p) {
		m.add(new Message(p, System.currentTimeMillis()));
	}

	public void run() {
		while (true) {
		
			if(m.size() > 0){
					System.out.println(m.get(0).sendTime);
				if(m.get(0).sendTime < System.currentTimeMillis()){
					try {
						EntityManager.getInstance().m_socket.send(m.get(0).pack);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					m.remove(0);
					
				}
			}
			
		}
	}
}

class Message {
	public long sendTime;
	public DatagramPacket pack;
	public Message(DatagramPacket p, long t) {
		pack = p;
		sendTime = t;
	}
}