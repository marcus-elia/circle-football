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
            //players.add(new FootballPlayer(this, randomX, randomY, ID.Player, this.playerRadius, color, 2));
            players.add(new RandomBouncer(this, randomX, randomY, ID.Player, this.playerRadius, color, 2));
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
                    //collisionReaction(p1, p2);
                    collideRB((RandomBouncer)p1, (RandomBouncer)p2);
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
                    //collisionReaction(p1, p2);
                    collideRB((RandomBouncer)p1, (RandomBouncer)p2);
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
					//collisionReaction(p1, p2);
                    collideRB((RandomBouncer)p1, (RandomBouncer)p2);
                }
            }
        }
    }

    // Ryan's original version
    public void collisionReaction(FootballPlayer p1, FootballPlayer p2)
	{
		double p1Angle = Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getY()) + Math.PI/2;
		p1.setTarget(p1.getX() + Math.cos(p1Angle) * 20, p1.getY() + Math.sin(p1Angle) * 20);
		double p2Angle = Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getY());
		p2.setTarget(p2.getX() + Math.cos(p2Angle) * 20, p2.getY() + Math.sin(p2Angle) * 20);
	}

	// Suppose that an object is bouncing off a wall, and it is coming in at an angle of
    // incoming angle.  This reflects it across the imaginary line of the fixedAngle
	public double reflectAngle(double incomingAngle, double fixedAngle)
    {
        return fixedAngle + (fixedAngle - incomingAngle);
    }

	public void collide(FootballPlayer fp1, FootballPlayer fp2)
    {
        /* double angleBetween = Math.atan2(fp2.getY() - fp1.getY(), fp2.getX() - fp1.getX());
        fp1.setAngle(reflectAngle(fp1.getAngle(), angleBetween));
        angleBetween += Math.PI;
        fp2.setAngle(reflectAngle(fp2.getAngle(), angleBetween));
        */

        //If the signs of dx or dy are different between the two players then they should be swapped
        //eg -dx and +dx means +dx and -dx, but -dx and -dx means -dx and -dx
		//Should look fine as long the players collide head-on
		//I saw a few players get stuck together. I'm guessing that's because this method kept getting called and
		//changing their target coordinates
		//I still don't know why some players don't react to collisions. This method does get called when that happens.
        //Doesn't work if the signs of both dx and dy are the same, ie the players are headed in the same general direction
        double targetXP1;
        double targetXP2;
        double targetYP1;
        double targetYP2;
        //Determine x
        if ((fp1.getDX() > 0 && fp2.getDX() < 0) || (fp1.getDX() < 0 && fp2.getDX() > 0))
        {
            targetXP1 = fp1.getX() + fp1.getDX()*-30;
            targetXP2 = fp2.getX() + fp2.getDX()*-30;
        }
        else
        {
            targetXP1 = fp1.getX() + fp1.getDX()*30;
            targetXP2 = fp2.getX() + fp2.getDX()*30;
        }
        //Determine y
        if ((fp1.getDY() > 0 && fp2.getDY() < 0) || (fp1.getDY() < 0 && fp2.getDY() > 0))
        {
            targetYP1 = fp1.getY() + fp1.getDY()*-30;
            targetYP2 = fp2.getY() + fp2.getDY()*-30;
        }
        else
        {
            targetYP1 = fp1.getY() + fp1.getDY()*30;
            targetYP2 = fp2.getY() + fp2.getDY()*30;
        }
        fp1.setTarget(targetXP1, targetYP1);
        fp2.setTarget(targetXP2, targetYP2);
    }

    // Another attempt, which calls the function in RandomBouncer
    public void collideRB(RandomBouncer rb1, RandomBouncer rb2)
    {
        rb1.reactToCollision(rb2.getX(), rb2.getY());
        rb2.reactToCollision(rb1.getX(), rb1.getY());
    }
}

