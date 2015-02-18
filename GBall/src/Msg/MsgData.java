package Msg;

import java.io.Serializable;

import GBall.Vector2D;

public class MsgData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Vector2D m_P1position;
	public Vector2D m_P2position;
	public Vector2D m_P3position;
	public Vector2D m_P4position;
	public Vector2D m_BallPosition;
	
	public Vector2D m_P1rotation;
	public Vector2D m_P2rotation;
	public Vector2D m_P3rotation;
	public Vector2D m_P4rotation;

	
    public MsgData(){
    	m_P1position = new Vector2D();
    	m_P2position = new Vector2D();
    	m_P3position = new Vector2D();
    	m_P4position = new Vector2D();
    	m_BallPosition = new Vector2D();
    	
    	m_P1rotation = new Vector2D();
    	m_P2rotation = new Vector2D();
    	m_P3rotation = new Vector2D();
    	m_P4rotation = new Vector2D();
    }

    public MsgData(Vector2D P1position, Vector2D P2position, Vector2D P3position, Vector2D P4position, Vector2D P1rotation, Vector2D P2rotation, Vector2D P3rotation, Vector2D P4rotation, Vector2D Ballposition){
    	m_P1position = P1position;
    	m_P2position = P2position;
    	m_P3position = P3position;
    	m_P4position = P3position;
    	m_BallPosition = Ballposition;
    	
    	m_P1rotation = P1rotation;
    	m_P2rotation = P2rotation;
    	m_P3rotation = P3rotation;
    	m_P4rotation = P4rotation;
    }

}
