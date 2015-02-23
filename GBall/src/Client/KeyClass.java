package Client;

import java.awt.event.KeyEvent;

import GBall.Const;
import GBall.KeyConfig;

public class KeyClass {
	private KeyConfig keyConfig;
	private int acceleration = 0;
	private int turning = 0;
	private int ID;
	
	public KeyClass(KeyConfig k, int id) {
		keyConfig = k;
		ID = id;
	}
	
	public int[] getOutput() {
		int[] i = {ID, acceleration, turning};
		return i;
	}
	
	public void keyPressed(KeyEvent e) {
    	try {
    	    if(e.getKeyCode() == keyConfig.rightKey()) {
    		turning = 1;
    	    }
    	    else if(e.getKeyCode() == keyConfig.leftKey()) {
    		turning = -1;
    	    }
    	    else if(e.getKeyCode() == keyConfig.accelerateKey()) {
    		acceleration = 1;
    	    }
    	    else if(e.getKeyCode() == keyConfig.brakeKey()) {
    		acceleration = -1;
    	    }
    	} catch(Exception x) {System.err.println(x);}
        }

        public void keyReleased(KeyEvent e) {
        	
            try {
    	    if(e.getKeyCode() == keyConfig.rightKey()) {
    		turning = 0;
    	    }
    	    else if(e.getKeyCode() == keyConfig.leftKey()) {
    		turning = 0;
    	    }
    	    else if(e.getKeyCode() == keyConfig.accelerateKey()) {
    		acceleration = 0;
    	    }
    	    else if(e.getKeyCode() == keyConfig.brakeKey()) {
    		acceleration = 0;
    	    }
    	} catch(Exception x) {System.out.println(x);}
        }
        public void keyTyped(KeyEvent e) {} 
}
