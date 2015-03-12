package Client;

import java.awt.Color;
import java.awt.event.KeyEvent;

import GBall.Const;
import Msg.Vector2D;

public class Player {
	
	Vector2D position;
	Vector2D rotation;
	Color color;
	
	final int radius = 22;
	
	public Player(Vector2D pos, Vector2D rot, Color c) {
		position = pos;
		rotation = rot;
		color = c;
	}

    public void render(java.awt.Graphics g) {
	g.setColor(color);
	g.drawOval((int) getPosition().getX() - radius,
		   (int) getPosition().getY() - radius,
		   radius * 2,
		   radius * 2
		  );

	if (color != Color.white) {
		g.drawLine((int) getPosition().getX(),
			   (int) getPosition().getY(),
			   (int) (getPosition().getX() + getDirection().getX() * radius),
			   (int) (getPosition().getY() + getDirection().getY() * radius)
			   );
	}
    }
    
    Vector2D getPosition() {
    	return position;
    }
    
    Vector2D getDirection() {
    	return rotation;
    }
    
    public void setPosition(Vector2D v) {
    	position = v;
    }
    
    public void setRotation(Vector2D r) {
    	rotation = r;
    }
}
