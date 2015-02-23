package GBall;

import java.awt.Color;
import java.awt.event.*;
import java.net.InetAddress;

import Msg.Vector2D;

public class Ship extends GameEntity {

    private Color m_color;
    private int rotation = 0; // Set to 1 when rotating clockwise, -1 when rotating counterclockwise
    private boolean braking = false;
    /////////////////////
    private boolean m_isUsedByPlayer = false;
    private InetAddress m_address;
    private int m_port;
    
    @Override
    public boolean isUsedByPlayer(){
    	return m_isUsedByPlayer;
    }
    @Override
    public boolean connect(InetAddress address, int port){
    	if(!m_isUsedByPlayer){
    		m_address = address;
    		m_port = port;
    		m_isUsedByPlayer = true;
    		return true;
    	}
    	return false;
    }
    @Override
    public boolean compareAddressAndPort(InetAddress address, int port){
    	if(address == m_address && port == m_port)
    		return true;
    	return false;
    }
    @Override
    public void setInput(int acc, int dir){ 
    	if(dir == -1 || dir == 1)
    		rotation = dir;
    	if(acc == 1)
    		setAcceleration(Const.SHIP_MAX_ACCELERATION);
    	else if(acc == -1)
    		braking = true;
    }
    @Override
    public InetAddress getAddress(){
    	return m_address;
    }
    @Override
    public int getPort(){
    	return m_port;
    }
    /////////////////////
    public Ship(final Vector2D position, final Vector2D speed, final Vector2D direction, final Color col) {
	super(position, speed, direction, Const.SHIP_MAX_ACCELERATION, Const.SHIP_MAX_SPEED, Const.SHIP_FRICTION);
	m_color = col;
    }
    
   /* @Override
    public void keyPressed(KeyEvent e) {
	try {
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
		System.exit(0);
	    }
	    else if(e.getKeyCode() == m_keyConfig.rightKey()) {
		rotation = 1;
	    }
	    else if(e.getKeyCode() == m_keyConfig.leftKey()) {
		rotation = -1;
	    }
	    else if(e.getKeyCode() == m_keyConfig.accelerateKey()) {
		setAcceleration(Const.SHIP_MAX_ACCELERATION);
	    }
	    else if(e.getKeyCode() == m_keyConfig.brakeKey()) {
		braking = true;
	    }
	} catch(Exception x) {System.err.println(x);}
    }

    public void keyReleased(KeyEvent e) {
    	
        try {
	    if(e.getKeyCode() == m_keyConfig.rightKey() && rotation == 1) {
		rotation = 0;
	    }
	    else if(e.getKeyCode() == m_keyConfig.leftKey() && rotation == -1) {
		rotation = 0;
	    }
	    else if(e.getKeyCode() == m_keyConfig.accelerateKey()) {
		setAcceleration(0);
	    }
	    else if(e.getKeyCode() == m_keyConfig.brakeKey()) {
		braking = false;
	    }
	} catch(Exception x) {System.out.println(x);}
    }
    @Override
    public void keyTyped(KeyEvent e) {} 
*/
    @Override
    public void move() {
	if(rotation != 0) {
	    rotate(rotation * Const.SHIP_ROTATION);
	    scaleSpeed(Const.SHIP_TURN_BRAKE_SCALE);
	}
	if(braking) {
	    scaleSpeed(Const.SHIP_BRAKE_SCALE);
	    setAcceleration(0);
	}
	super.move();
    }

    @Override
    public void render(java.awt.Graphics g) {
	g.setColor(m_color);
	g.drawOval((int) getPosition().getX() - Const.SHIP_RADIUS,
		   (int) getPosition().getY() - Const.SHIP_RADIUS,
		   Const.SHIP_RADIUS * 2,
		   Const.SHIP_RADIUS * 2
		  );

	g.drawLine((int) getPosition().getX(),
		   (int) getPosition().getY(),
		   (int) (getPosition().getX() + getDirection().getX() * Const.SHIP_RADIUS),
		   (int) (getPosition().getY() + getDirection().getY() * Const.SHIP_RADIUS)
		   );
    }

    @Override
    public boolean givesPoints() {
	return false;
    }

    @Override
    public double getRadius() {
	return Const.SHIP_RADIUS;
    }

}