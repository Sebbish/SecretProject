package Client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import GBall.Const;
import GBall.KeyConfig;

public class KeyClass implements KeyListener{
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
	
	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("A key was pressed");
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
	
	@Override
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
	@Override
    public void keyTyped(KeyEvent e) {} 
}
