package GBall;

import java.awt.Color;
import java.awt.Graphics;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.ListIterator;

import Msg.MsgData;

public class EntityManager {
    private static LinkedList<GameEntity> m_entities = new LinkedList<GameEntity>();
    
    DatagramSocket m_socket;
    public static final int SERVERPORT = 4444;
    
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
    }
    
    public void addShip(final Vector2D position, final Vector2D speed, final Vector2D direction, final Color color, final KeyConfig kc) {
    	m_entities.add(new Ship(position, speed, direction, color));
    }

    public void addBall(final Vector2D position, final Vector2D speed) {
    	m_entities.add(new Ball(position, speed));	
    }
    /////////////////////
    public void allConnected(){
    	int id = 0;
    	byte[] b = new byte[16];
    	DatagramPacket packet = new DatagramPacket(b,b.length);
    	
    	try {
			m_socket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
    	int msg = ByteBuffer.wrap(b, 0, packet.getLength()).getInt();
    	/*for(int i = id; i < id + msg; i++){
    		if(m_entities.get(i).isUsedByPlayer() && i < 4)
    			m_entities.get(i).connect(packet.getAddress(), packet.getPort());
    		byte[] by = new byte[4];
    		if(id < 4)
    			by = ByteBuffer.allocate(4).putInt(id).array();
    		else
    			by = ByteBuffer.allocate(4).putInt(0).array();
    		DatagramPacket send = new DatagramPacket(by,by.length);
    		try {
    			m_socket.receive(packet);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			//e.printStackTrace();
    		}
    		int msg1 = ByteBuffer.wrap(b, 0, packet.getLength()).getInt();
    		if(msg1 == 0)
    			id++;
    		else
    			i--;
    	}
    	id  += msg;*/
    }
   
    public void updateinput(){
    	byte[] b = new byte[2];
    	DatagramPacket packet = new DatagramPacket(b,b.length);
    	
    	try {
			m_socket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
    	int id = packet.getData()[0];
    	int msg = packet.getData()[1];
    	if(id >= 0 && id <= 4){
    		if(m_entities.get(id).isUsedByPlayer())
    			m_entities.get(id).setInput(msg);
    		else
    			m_entities.get(id).connect(packet.getAddress(), packet.getPort());
    	}
    }
    
    public void broadcastPosition(){
    	MsgData msg = new MsgData(
    			m_entities.get(0).getPosition(),
    			m_entities.get(1).getPosition(),
    			m_entities.get(2).getPosition(),
    			m_entities.get(3).getPosition(),
    			m_entities.get(0).getDirection(),
    			m_entities.get(1).getDirection(),
    			m_entities.get(2).getDirection(),
    			m_entities.get(3).getDirection(),
    			m_entities.get(4).getPosition());
    	
    	try {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
  		ObjectOutputStream oos = new ObjectOutputStream(baos);
  	    oos.writeObject(new MsgData());
  	    oos.flush();
  	    byte[] buf = new byte[1024];
  	    buf = baos.toByteArray();
  	    for(int i = 0; i < 4; i++){
  	    	if(m_entities.get(i).getAddress() != null){
  	    		DatagramPacket pack = new DatagramPacket(buf, buf.length, m_entities.get(i).getAddress(), m_entities.get(i).getPort());
  	    		m_socket.send(pack);
  	    	}
  	    }
		oos = new ObjectOutputStream(baos);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
    ///////////////////////////////

    public void updatePositions() {
		for(ListIterator<GameEntity> itr = m_entities.listIterator(0); itr.hasNext();) {
		    itr.next().move();
		}
    }

    public void renderAll(Graphics g) {
		for(ListIterator<GameEntity> itr = m_entities.listIterator(0); itr.hasNext();) {
		    itr.next().render(g);
		}
    }

    public void checkBorderCollisions(int screenWidth, int screenHeight) {
		double newX = 0.0, newY = 0.0, radius = 0;
		boolean reset = false;
		for(ListIterator<GameEntity> itr = m_entities.listIterator(0); itr.hasNext();) {
		    GameEntity e = itr.next();
		    newX = e.getPosition().getX();
		    newY = e.getPosition().getY();
		    radius = e.getRadius();
		    
		    if(newX + radius > (screenWidth - Const.WINDOW_BORDER_WIDTH)) {
			newX = screenWidth - radius - Const.WINDOW_BORDER_WIDTH;
			e.deflectX();
			if(e.givesPoints()) {
			    ScoreKeeper.getInstance().changeScores(1, 0);
			    reset = true;
			}
	    }
		else if((newX - e.getRadius()) < Const.WINDOW_BORDER_WIDTH ) {
			newX = radius + Const.WINDOW_BORDER_WIDTH;
			e.deflectX();
			if(e.givesPoints()) {
			    ScoreKeeper.getInstance().changeScores(0, 1);
			    reset = true;
			}
		}
		    
		if(newY + radius > (screenHeight - Const.WINDOW_BOTTOM_HEIGHT)) {
			newY = screenHeight - radius - Const.WINDOW_BOTTOM_HEIGHT;
			e.deflectY();
		}
		else if(newY - radius < Const.WINDOW_TOP_HEIGHT) {
			newY = radius + Const.WINDOW_TOP_HEIGHT;
			e.deflectY();
		}
		
		e.setPosition(newX, newY);
	}
	
	if(reset) {
	    resetPositions();
	}
    }

    public void checkShipCollisions() {
	Vector2D v; // Vector from center of one ship to the other

	for(ListIterator<GameEntity> itr = m_entities.listIterator(0); itr.hasNext();) {	
	    GameEntity s1 = itr.next();
	    if(itr.hasNext()) { 
		for(ListIterator<GameEntity> itr2 = m_entities.listIterator(itr.nextIndex()); itr2.hasNext();) {
		    GameEntity s2 = itr2.next();
		    v = s1.getPosition().minusOperator(s2.getPosition());
		    double dist = v.length();
	
		    if(v.length() < (s1.getRadius() + s2.getRadius())) { // Simple collision detection; just assume that ships will overlap during collision
			// Displace ships to avoid drawing overlap
			// Simplification: just displace both ships an equal amount
			v.setLength((s1.getRadius() + s2.getRadius() - dist) / 2);
			s1.displace(v);
			v.invert();
			s2.displace(v);
			
			// Update movement vectors (assume perfect, rigid collision with no momentum loss and equal masses)
			v.makeUnitVector();		// Normalize v
			// Compute momentum along v
			double comp1 = s1.getSpeed().dotProduct(v);
			double comp2 = s2.getSpeed().dotProduct(v);
			double m = comp1 - comp2;	// 2(comp1-comp2) / mass1 + mass2 = 2(comp1-comp2) / 2 = comp1 - comp2
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
	for(ListIterator<GameEntity> itr = m_entities.listIterator(0); itr.hasNext();) {
	    itr.next().resetPosition();
	}
    }

	public static LinkedList<GameEntity> getState() {
		
		return m_entities;
	}
}