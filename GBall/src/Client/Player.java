package Client;

import java.awt.Color;

import GBall.Vector2D;

public class Player {
	
	Vector2D position;
	Vector2D rotation;
	Color color;
	int ID;
	
	public Player() {
		
	}
	
	public void SetID(int id) {
		ID = id;
	}
	
	public int GetID() {
		return ID;
	}
}
