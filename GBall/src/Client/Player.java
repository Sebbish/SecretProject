package Client;

import java.awt.Color;
import java.awt.event.KeyEvent;

import GBall.Const;
import GBall.KeyConfig;
import GBall.Vector2D;

public class Player {
	
	Vector2D position;
	Vector2D rotation;
	Color color;
	int ID;
	
	enum direction {
		LEFT,
		RIGHT,
		NONE
		
		direction(int i) {}
	};
	
	KeyConfig keyConfig;
	
	public Player() {
		
	}
	
	public void SetID(int id) {
		ID = id;
	}
	
	public int GetID() {
		return ID;
	}
	
	public void keyPressed(KeyEvent e) {
	try {
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
		System.exit(0);
	    }
	    else if(e.getKeyCode() == keyConfig.rightKey()) {
		rotation = 1;
	    }
	    else if(e.getKeyCode() == keyConfig.leftKey()) {
		rotation = -1;
	    }
	    else if(e.getKeyCode() == keyConfig.accelerateKey()) {
		setAcceleration(Const.SHIP_MAX_ACCELERATION);
	    }
	    else if(e.getKeyCode() == keyConfig.brakeKey()) {
		braking = true;
	    }
	} catch(Exception x) {System.err.println(x);}
    }

    public void keyReleased(KeyEvent e) {
    	
        try {
	    if(e.getKeyCode() == keyConfig.rightKey() && rotation == 1) {
		rotation = 0;
	    }
	    else if(e.getKeyCode() == keyConfig.leftKey() && rotation == -1) {
		rotation = 0;
	    }
	    else if(e.getKeyCode() == keyConfig.accelerateKey()) {
		setAcceleration(0);
	    }
	    else if(e.getKeyCode() == keyConfig.brakeKey()) {
		braking = false;
	    }
	} catch(Exception x) {System.out.println(x);}
    }
    public void keyTyped(KeyEvent e) {} 

    

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
}