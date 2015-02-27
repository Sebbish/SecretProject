package Msg;

import java.io.Serializable;

public class MsgData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Vector2D m_P1position;
	private Vector2D m_P2position;
	private Vector2D m_P3position;
	private Vector2D m_P4position;
	private Vector2D m_BallPosition;

	private Vector2D m_P1rotation;
	private Vector2D m_P2rotation;
	private Vector2D m_P3rotation;
	private Vector2D m_P4rotation;

	private Vector2D m_score;

	public MsgData() {
		m_P1position = new Vector2D();
		m_P2position = new Vector2D();
		m_P3position = new Vector2D();
		m_P4position = new Vector2D();
		m_BallPosition = new Vector2D();

		m_P1rotation = new Vector2D();
		m_P2rotation = new Vector2D();
		m_P3rotation = new Vector2D();
		m_P4rotation = new Vector2D();
		m_score = new Vector2D();
	}

	public MsgData(Vector2D P1position, Vector2D P2position, Vector2D P3position, Vector2D P4position, Vector2D P1rotation, Vector2D P2rotation, Vector2D P3rotation, Vector2D P4rotation, Vector2D Ballposition, Vector2D score) {
		m_P1position = P1position;
		m_P2position = P2position;
		m_P3position = P3position;
		m_P4position = P4position;
		m_BallPosition = Ballposition;

		m_P1rotation = P1rotation;
		m_P2rotation = P2rotation;
		m_P3rotation = P3rotation;
		m_P4rotation = P4rotation;

		m_score = score;
	}

	public Vector2D getPosition(int id) {
		if (id == 1)
			return m_P1position;
		if (id == 2)
			return m_P2position;
		if (id == 3)
			return m_P3position;
		if (id == 4)
			return m_P4position;
		if (id == 5)
			return m_BallPosition;
		return null;
	}

	public Vector2D getRotation(int id) {
		if (id == 1)
			return m_P1rotation;
		if (id == 2)
			return m_P2rotation;
		if (id == 3)
			return m_P3rotation;
		if (id == 4)
			return m_P4rotation;
		return null;
	}

	public Vector2D getScore() {
		return m_score;
	}

}
