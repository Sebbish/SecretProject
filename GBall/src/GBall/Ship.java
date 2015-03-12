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
    
    //används av en spelare
    @Override
    public boolean isUsedByPlayer(){
    	return m_isUsedByPlayer;
    }
    //information om vem som styr denna spelare sparas
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
    //jämför address och port
    @Override
    public boolean compareAddressAndPort(InetAddress address, int port){
    	if(address.equals(m_address) && port == m_port)
    		return true;
    	return false;
    }
    //ställer in input som skickats från klient
    @Override
    public void setInput(int acc, int dir){ 
    	if(dir >= -1 || dir <= 1)
    		rotation = dir;
    	if(acc == 1)
    		setAcceleration(Const.SHIP_MAX_ACCELERATION);
    	else if(acc == -1)
    		braking = true;
    	else
    		setAcceleration(0);
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