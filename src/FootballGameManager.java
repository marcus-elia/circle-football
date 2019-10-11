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
        this.checkCollisions();
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

    public boolean areColliding(FootballPlayer p1, FootballPlayer p2)
    {
        return p1.isCollided(p2);
    }

    public void checkCollisions()
    {
        // Left colliding left
        for(int i = 0; i < leftTeam.size(); i++)
        {
            for(int j = i + 1; j < leftTeam.size(); j++)
            {
                FootballPlayer p1 = leftTeam.get(i);
                FootballPlayer p2 = leftTeam.get(j);
                if(areColliding(p1, p2))
                {
					collisionReaction(p1, p2);
                }
            }
        }

        // Left colliding right
        for(int i = 0; i < leftTeam.size(); i++)
        {
            for(int j = 0; j < rightTeam.size(); j++)
            {
                FootballPlayer p1 = leftTeam.get(i);
                FootballPlayer p2 = rightTeam.get(j);
                if(areColliding(p1, p2))
                {
					collisionReaction(p1, p2);
                }
            }
        }

        // Right colliding right
        for(int i = 0; i < rightTeam.size(); i++)
        {
            for(int j = i + 1; j < rightTeam.size(); j++)
            {
                FootballPlayer p1 = rightTeam.get(i);
                FootballPlayer p2 = rightTeam.get(j);
                if(areColliding(p1, p2))
                {
					collisionReaction(p1, p2);
                }
            }
        }
    }

    public void collisionReaction(FootballPlayer p1, FootballPlayer p2)
	{
		double p1Angle = Math.cos(Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getY()) + (Math.PI/2));
		p1.setTarget(Math.cos(p1Angle) * 40, Math.sin(p1Angle) * 40);
		double p2Angle = Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getY());
		p2.setTarget(Math.cos(p2Angle) * 40, Math.sin(p2Angle) * 40);
	}
}
