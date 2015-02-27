package Client;

import java.awt.Graphics;
import Msg.Vector2D;

public class ScoreKeeperClient {
    private static class ScoreKeeperSingletonHolder { 
        public static final ScoreKeeperClient instance = new ScoreKeeperClient();
    }

    public static ScoreKeeperClient getInstance() {
        return ScoreKeeperSingletonHolder.instance;
    }

    private int m_team1Score;
    private int m_team2Score;

    public void changeDeltaScores(int deltaTeam1, int deltaTeam2) {
		m_team1Score += deltaTeam1;
		m_team2Score += deltaTeam2;
    }
    
    public void changeScores(int Team1, int Team2) {
		m_team1Score = Team1;
		m_team2Score = Team2;
    }

    private ScoreKeeperClient() {
		m_team1Score = 0;
		m_team2Score = 0;
    }
    
    public Vector2D getScoreAsVector(){
    	return new Vector2D(m_team1Score,m_team2Score);
    }

    public void render(java.awt.Graphics g) {
		g.setFont(Const.SCORE_FONT);
		g.setColor(Const.TEAM1_COLOR);
		g.drawString(new Integer(m_team1Score).toString(), 
			     (int) Const.TEAM1_SCORE_TEXT_POSITION.getX(), 
			     (int) Const.TEAM1_SCORE_TEXT_POSITION.getY());
	
		g.setColor(Const.TEAM2_COLOR);
		g.drawString(new Integer(m_team2Score).toString(), 
			     (int) Const.TEAM2_SCORE_TEXT_POSITION.getX(), 
			     (int) Const.TEAM2_SCORE_TEXT_POSITION.getY());
    }
}