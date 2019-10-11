import java.awt.*;
import java.util.ArrayList;

public class FootballGameManager
{
    private Game theGame;
    private double playerRadius;
    private ArrayList<FootballPlayer> leftTeam;
    private ArrayList<FootballPlayer> rightTeam;

    private int endZoneWidth;

    // Stores the current line of scrimmage
    private double lineOfScrimmage;

    public FootballGameManager(Game inputGame, double inputRadius)
    {
        this.theGame = inputGame;
        this.playerRadius = inputRadius;
        this.leftTeam = createTeam(5, Color.RED);
        this.rightTeam = createTeam(5, Color.BLUE);
        this.lineOfScrimmage = this.theGame.getWidth() / 2;
        this.endZoneWidth = 60;
    }

    public ArrayList<FootballPlayer> createTeam(int numPlayers, Color color)
    {
        double randomX, randomY;
        // The player can spawn anywhere on the field such that it is not overlapping with the border
        double spawnableWidth = this.theGame.getWidth() - 4*this.playerRadius;
        double spawnableHeight = this.theGame.getHeight() - 4*this.playerRadius;
        ArrayList<FootballPlayer> players = new ArrayList<FootballPlayer>();
        for(int i = 0; i < numPlayers; i++)
        {
            randomX = Math.random()*spawnableWidth + 2*this.playerRadius;
            randomY = Math.random()*spawnableHeight + 2*this.playerRadius;
            players.add(new FootballPlayer(this, randomX, randomY, ID.Player, this.playerRadius, color, 2));
        }

        return players;
    }

    public void tick()
    {
        for(FootballPlayer curPlayer : leftTeam)
        {
            curPlayer.tick();
        }
        for(FootballPlayer curPlayer : rightTeam)
        {
            curPlayer.tick();
        }
    }

    public double getWidth()
	{
    	return this.theGame.getWidth();
	}

	public double getHeight()
	{
    	return this.theGame.getHeight();
	}

    public void render(Graphics2D g2d)
    {
        // Draw the goal lines
        int height = this.theGame.getHeight();
        int width = this.theGame.getWidth();
        g2d.setColor(Color.WHITE);
        g2d.drawLine(this.endZoneWidth, 0, this.endZoneWidth, height);
        g2d.drawLine(width - this.endZoneWidth, 0, width - this.endZoneWidth, this.theGame.getHeight());

        for(FootballPlayer curPlayer : leftTeam)
        {
            curPlayer.render(g2d);
        }
        for(FootballPlayer curPlayer : rightTeam)
        {
            curPlayer.render(g2d);
        }
    }
}
