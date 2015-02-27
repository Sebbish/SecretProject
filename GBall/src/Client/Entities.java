package Client;

import java.awt.Graphics;
import java.util.ArrayList;

public class Entities {
	private static class EntitiesSingletonHolder {
		public static final Entities instance = new Entities();
	}
	
	public static Entities getInstance() {
		return EntitiesSingletonHolder.instance;
	}
	
	private ArrayList<Player> players = new ArrayList<Player>();
	
	public void setEntities(ArrayList<Player> p) {
		players = p;
	}
	
	public void renderAll(Graphics g) {
		for (int i = 0; i < players.size(); i++) {
			players.get(i).render(g);
		}
	}
}