import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class FootballGameManager
{
    private Game theGame;
    private double playerRadius;
    private ArrayList<FootballPlayer> leftTeam;
    private ArrayList<FootballPlayer> rightTeam;
    private int leftScore;
    private int rightScore;

    // Storing all the players is helpful for collision stuff
    private ArrayList<FootballPlayer> allPlayers;
    private GameStatus status;
    private boolean playersReady;   // if the players are all lined up
    private boolean playInProgress; // if the play is actually happening
    private int waitTime; // Once the players are in position, how long
                          // we have waited before starting the play

    private int endZoneWidth;

    // Stores the current line of scrimmage
    private double lineOfScrimmage;
    private Ball ball;
    private boolean ballInAir;
    private FootballPlayer ballCarrier;
    private Team ballPossessingTeam;
    private int down; // 1st, 2nd, 3rd, 4th

    // How long since the user last clicked
    private int timeSinceLastClick;

    public FootballGameManager(Game inputGame, double inputRadius)
    {
        this.theGame = inputGame;
        this.status = GameStatus.LeftKickoff;
        this.playersReady = false;
        this.playInProgress = false;
        this.playerRadius = inputRadius;

        this.allPlayers = new ArrayList<FootballPlayer>();
        this.leftTeam = createTeam(5, Team.left, Color.RED);
        this.rightTeam = createTeam(5, Team.right, Color.BLUE);
        this.leftScore = 0;
        this.rightScore = 0;

        this.lineOfScrimmage = this.getWidth() / 2;
        this.endZoneWidth = this.getWidth() / 12;
        this.ball = new Ball(this, 0,0, ID.Ball);
        this.ballInAir = false;
        this.waitTime = 0;

    }

    // Returns true if we can spawn a new player at the given position
    // without overlapping with an existing player
    public boolean isValidSpawnPosition(double potentialX, double potentialY)
    {
        for(FootballPlayer fp : this.allPlayers)
        {
            if(fp.distanceToPoint(potentialX, potentialY) <= 2*this.playerRadius)
            {
                return false;
            }
        }
        return true;
    }


    public ArrayList<FootballPlayer> createTeam(int numPlayers, Team whichTeam, Color color)
    {
        double randomX, randomY;
        // The player can spawn anywhere on the field such that it is not overlapping with the border
        double spawnableWidth = this.theGame.getWidth() - 4*this.playerRadius;
        double spawnableHeight = this.theGame.getHeight() - 4*this.playerRadius;
        ArrayList<FootballPlayer> players = new ArrayList<FootballPlayer>();
        for(int i = 0; i < numPlayers; i++)
        {
            // Keep trying random coordinates until we get somewhere that works
            randomX = Math.random()*spawnableWidth + 2*this.playerRadius;
            randomY = Math.random()*spawnableHeight + 2*this.playerRadius;
            while(!this.isValidSpawnPosition(randomX, randomY))
            {
                randomX = Math.random()*spawnableWidth + 2*this.playerRadius;
                randomY = Math.random()*spawnableHeight + 2*this.playerRadius;
            }

            // Add the new player to the team
            players.add(new FootballPlayer(this, i, whichTeam,
                    randomX, randomY, ID.Player, this.playerRadius, color, 2));
            // Add the new player to the list of all players
            this.allPlayers.add(players.get(players.size() - 1));
            //players.add(new RandomBouncer(this, randomX, randomY, ID.Player, this.playerRadius, color, 2));
        }

        return players;
    }

    public void tick()
    {
        // For mouse clicks
        if(this.timeSinceLastClick < 100)
        {
            this.timeSinceLastClick++;
        }
        for(FootballPlayer curPlayer : leftTeam)
        {
            curPlayer.tick();
        }
        for(FootballPlayer curPlayer : rightTeam)
        {
            curPlayer.tick();
        }
        this.checkCollisions();

        // Game management
        if(!this.playInProgress && !this.playersReady)
        {
            this.checkReadiness();
        }
        else if(!this.playInProgress)
        {
            if(this.waitTime < 30)
            {
                this.waitTime++;
            }
            else
            {
                this.actuallyStartPlay();
            }
        }
        // Check if the play is over
        else
        {
            this.checkEndZones();
            this.checkSidelines();
        }
        if(this.ballInAir)
        {
            this.ball.tick();
            this.checkPickUpBall();
        }
    }


    public void render(Graphics2D g2d)
    {
        this.drawClickRecharge(g2d);

        // Draw the goal lines
        int height = this.theGame.getHeight();
        int width = this.theGame.getWidth();
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(5));
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

        if(this.ballInAir)
        {
            this.ball.render(g2d);
        }

        // Draw the line of scrimmage if a play if happening
        if(this.status == GameStatus.LeftPlay)
        {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(5));
            Line2D.Double LoS = new Line2D.Double(this.lineOfScrimmage, 0, this.lineOfScrimmage, height);
            g2d.draw(LoS);
        }
        else if(this.status == GameStatus.RightPlay)
        {
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(5));
            Line2D.Double LoS = new Line2D.Double(this.lineOfScrimmage, 0, this.lineOfScrimmage, height);
            g2d.draw(LoS);
        }



        // Print the status and score
        if(!this.playInProgress && !this.playersReady)
        {
            String statusString;
            switch(this.status)
            {
                case LeftKickoff :
                    statusString = "Left Kickoff";
                    break;

                case RightKickoff :
                    statusString = "Right Kickoff";
                    break;
                case LeftPlay :
                    statusString = this.downString() + " for Left";
                    break;
                case RightPlay :
                    statusString = this.downString() + " for Right";
                    break;
                default:
                    statusString = "Football Game";
            }
            g2d.setFont(new Font("Courier", Font.PLAIN, 24));
            g2d.setColor(Color.WHITE);
            int pixelLength = g2d.getFontMetrics().stringWidth(statusString); // the number of pixels the string is long
            g2d.drawString(statusString, this.getWidth()/2 - pixelLength/2, this.getHeight()/2);
            g2d.setColor(Color.RED);
            g2d.drawString(Integer.toString(this.leftScore), this.getWidth()/4, this.getHeight()/2);
            g2d.setColor(Color.BLUE);
            g2d.drawString(Integer.toString(this.rightScore), 3*this.getWidth()/4, this.getHeight()/2);
        }


    }

    // Draw a line around the perimeter showing how close we are to being recharged
    public void drawClickRecharge(Graphics2D g2d)
    {
        // For concise code
        int i = this.timeSinceLastClick;

        // The color is dark green when not charged, and light green when charged
        Color c = new Color(i, 2*i + 55, i);

        g2d.setColor(c);
        g2d.setStroke(new BasicStroke(10));

        double semiperimeter = this.getWidth() + this.getHeight();
        double horizontalPercent = this.getWidth() / (2.0*this.getWidth() + this.getHeight())*100;

        double halfWidth = this.getWidth() / 2.0;

        // Make the bottom lines
        Line2D.Double leftBottom = new Line2D.Double(halfWidth, this.getHeight(),
            halfWidth - i/horizontalPercent*halfWidth, this.getHeight());
        Line2D.Double rightBottom = new Line2D.Double(halfWidth, this.getHeight(),
                halfWidth + i/horizontalPercent*halfWidth, this.getHeight());

        g2d.draw(leftBottom);
        g2d.draw(rightBottom);

        // The side lines
        if(i > horizontalPercent)
        {
            Line2D.Double left = new Line2D.Double(0, this.getHeight(),
                    0, this.getHeight() - (i-horizontalPercent)/(100-2*horizontalPercent)*this.getHeight());
            Line2D.Double right = new Line2D.Double(this.getWidth(), this.getHeight(),
                    this.getWidth(), this.getHeight() - (i-horizontalPercent)/(100-2*horizontalPercent)*this.getHeight());
            g2d.draw(left);
            g2d.draw(right);
        }

        // The top lines
        if(i > 100 - horizontalPercent)
        {
            Line2D.Double leftTop = new Line2D.Double(0, 0,
                    (i - (100 - horizontalPercent))/(horizontalPercent)*halfWidth, 0);
            Line2D.Double rightTop = new Line2D.Double(this.getWidth(), 0,
                    this.getWidth() - (i - (100 - horizontalPercent))/(horizontalPercent)*halfWidth, 0);
            g2d.draw(leftTop);
            g2d.draw(rightTop);
        }
    }




    public boolean areColliding(FootballPlayer p1, FootballPlayer p2)
    {
        return p1.isCollided(p2);
    }

    public void checkCollisions()
    {
        for(int i = 0; i < allPlayers.size(); i++)
        {
            for(int j = i + 1; j < allPlayers.size(); j++)
            {
                FootballPlayer p1 = allPlayers.get(i);
                FootballPlayer p2 = allPlayers.get(j);
                if(areColliding(p1, p2))
                {
                    //collisionReaction(p1, p2);
                    //collideRB((RandomBouncer)p1, (RandomBouncer)p2);
                    collide(p1, p2);
                }
            }
        }
    }

    // Ryan's original version
    /*public void collisionReaction(FootballPlayer p1, FootballPlayer p2)
	{
		double p1Angle = Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getY()) + Math.PI/2;
		p1.setTarget(p1.getX() + Math.cos(p1Angle) * 20, p1.getY() + Math.sin(p1Angle) * 20);
		double p2Angle = Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getY());
		p2.setTarget(p2.getX() + Math.cos(p2Angle) * 20, p2.getY() + Math.sin(p2Angle) * 20);
	}*/

	// Suppose that an object is bouncing off a wall, and it is coming in at an angle of
    // incoming angle.  This reflects it across the imaginary line of the fixedAngle
	public double reflectAngle(double incomingAngle, double fixedAngle)
    {
        return fixedAngle + (fixedAngle - incomingAngle);
    }

	public void collide(FootballPlayer fp1, FootballPlayer fp2)
    {
        fp1.reactToCollision(fp2.getX(), fp2.getY());
        fp2.reactToCollision(fp1.getX(), fp1.getY());

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
        /*double targetXP1;
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
        fp2.setTarget(targetXP2, targetYP2);*/
    }

    // Another attempt, which calls the function in RandomBouncer
    public void collideRB(RandomBouncer rb1, RandomBouncer rb2)
    {
        rb1.reactToCollision(rb2.getX(), rb2.getY());
        rb2.reactToCollision(rb1.getX(), rb1.getY());
    }

    public void pushEveryone(int x, int y)
    {
        for(FootballPlayer fp : this.allPlayers)
        {
            fp.bounceAway(x, y);
        }
    }

    // This checks if any players can pickup the ball. If one can, it does
    public void checkPickUpBall()
    {
        for(FootballPlayer fp : this.allPlayers)
        {
            if(fp.distanceToPoint(this.ball.getX(), this.ball.getY()) < 2*this.playerRadius)
            {
                fp.pickUpBall();
                this.ballInAir = false;
                this.ballCarrier = fp;
                this.ballPossessingTeam = fp.getWhichTeam();
                return;
            }
        }
    }

    // Returns the nearest opponent to the given player
    public FootballPlayer findNearestOpponent(FootballPlayer fp)
    {
        double shortestDistance = 2*this.getWidth();
        int indexOfClosest = 0;
        ArrayList<FootballPlayer> otherTeam;
        if(fp.getWhichTeam() == Team.left)
        {
            otherTeam = this.rightTeam;
        }
        else
        {
            otherTeam = this.leftTeam;
        }
        for(int i = 0; i < otherTeam.size(); i++)
        {
            double curDistance = fp.distanceToPoint(otherTeam.get(i).getX(), otherTeam.get(i).getY());
            if(curDistance < shortestDistance)
            {
                shortestDistance = curDistance;
                indexOfClosest = i;
            }
        }
        return otherTeam.get(indexOfClosest);
    }

    // Getters
    public int getWidth()
    {
        return this.theGame.getWidth();
    }
    public int getHeight()
    {
        return this.theGame.getHeight();
    }
    public GameStatus getStatus()
    {
        return this.status;
    }
    public boolean getInProgress()
    {
        return this.playInProgress;
    }
    public double getPlayerRadius()
    {
        return this.playerRadius;
    }
    public Ball getBall()
    {
        return this.ball;
    }
    public boolean isBallInAir()
    {
        return this.ballInAir;
    }
    public FootballPlayer getBallCarrier()
    {
        return this.ballCarrier;
    }
    public Team getBallPossessingTeam()
    {
        return this.ballPossessingTeam;
    }
    public double getLineOfScrimmage()
    {
        return this.lineOfScrimmage;
    }
    public int getTimeSinceLastClick()
    {
        return this.timeSinceLastClick;
    }
    public String downString()
    {
        if(this.down == 1)
        {
            return "1st Down";
        }
        else if(this.down == 2)
        {
            return "2nd Down";
        }
        else if(this.down == 3)
        {
            return "3rd Down";
        }
        else
        {
            return "4th Down";
        }
    }

    public void setBallInAir(boolean b)
    {
        this.ballInAir = b;
    }
    public void setBallCarrier(FootballPlayer fp)
    {
        this.ballCarrier = fp;
    }
    public void setTimeSinceLastClick(int newTime)
    {
        this.timeSinceLastClick = newTime;
    }

    //  ====================================
    //
    //            Game management
    //
    //  ====================================

    // set the playersReady boolean by checking if all players are ready or not
    public void checkReadiness()
    {
        boolean allReady = true;
        // Check if every player is ready for the play
        for(FootballPlayer fp : leftTeam)
        {
            if(!fp.isInPosition())
            {
                allReady = false;
            }
        }
        for(FootballPlayer fp : rightTeam)
        {
            if(!fp.isInPosition())
            {
                allReady = false;
            }
        }
        this.playersReady = allReady;
        if(allReady)
        {
            this.startPlay();
        }
    }

    public void startPlay()
    {
        this.waitTime = 0;
        if(this.status == GameStatus.LeftKickoff || this.status == GameStatus.LeftPlay)
        {
            this.leftTeam.get(2).pickUpBall();
        }
        else if(this.status == GameStatus.RightKickoff || this.status == GameStatus.RightPlay)
        {
            this.rightTeam.get(2).pickUpBall();
        }
    }

    public void actuallyStartPlay()
    {
        if(this.status == GameStatus.LeftKickoff)
        {
            this.leftTeam.get(2).kickOff();
        }
        else if(this.status == GameStatus.RightKickoff)
        {
            this.rightTeam.get(2).kickOff();
        }
        this.playInProgress = true;
    }

    public void endPlay()
    {
        this.ballCarrier.dropBall();
        this.playersReady = false;
        this.playInProgress = false;
        for(FootballPlayer fp : this.allPlayers)
        {
            fp.setInPosition(false);
            fp.setPositionForPlay();
        }
    }

    // If a player has the ball in the endzone, do either a touchdown or safety
    public void checkEndZones()
    {
        // If the ball is in the air, do not check
        if(this.ballInAir)
        {
            return;
        }
        double x = this.ballCarrier.getX();
        if(x <= this.getWidth()/12.0 - 2*this.playerRadius)
        {
            if(this.ballCarrier.getWhichTeam() == Team.right)
            {
                this.rightScore += 7;
                this.status = GameStatus.RightKickoff;
            }
            else
            {
                this.rightScore += 2;
                this.status = GameStatus.LeftKickoff;
            }
            this.endPlay();
        }
        else if(x >= this.getWidth()*11.0/12 + 2*this.playerRadius)
        {
            if(this.ballCarrier.getWhichTeam() == Team.left)
            {
                this.leftScore += 7;
                this.status = GameStatus.LeftKickoff;
            }
            else
            {
                this.leftScore += 2;
                this.status = GameStatus.RightKickoff;
            }

            this.endPlay();
        }
    }

    // Simply return the opposite of the Team you input
    public Team otherTeam(Team team)
    {
        if(team == Team.left)
        {
            return Team.right;
        }
        else
        {
            return Team.left;
        }
    }

    // A simple helper function to set this GameManager's status to be either
    // left play or right play
    public void setPlay(Team team)
    {
        if(team == Team.left)
        {
            this.status = GameStatus.LeftPlay;
        }
        else
        {
            this.status = GameStatus.RightPlay;
        }
    }

    // If the ball carrier is out of bounds, end the play
    public void checkSidelines()
    {
        // If the ball is in the air, do not check
        if(this.ballInAir)
        {
            return;
        }
        double y = this.ballCarrier.getY();

        // If they are out
        if(y < this.playerRadius + 2 || y > this.getHeight() - this.playerRadius - 2)
        {
            this.lineOfScrimmage = this.ballCarrier.getX();

            // If we had a kickoff, start 1st down
            if(this.status == GameStatus.LeftKickoff || this.status == GameStatus.RightKickoff)
            {
                this.down = 1;
                this.setPlay(this.ballPossessingTeam);
            }
            // If it's fourth down, the other team gets the ball
            else if(this.down == 4)
            {
                 this.ballPossessingTeam = this.otherTeam(this.ballPossessingTeam);
                 this.down = 1;
                 this.setPlay(this.ballPossessingTeam);
            }
            // If it wasn't 4th down
            else
            {
                // If no interception happened
                if((this.status == GameStatus.LeftPlay && this.ballPossessingTeam == Team.left) ||
                        (this.status == GameStatus.RightPlay && this.ballPossessingTeam == Team.right))
                {
                    this.down += 1;
                }
                // There was an interception
                else
                {
                    this.ballPossessingTeam = this.otherTeam(this.ballPossessingTeam);
                    this.down = 1;
                    this.setPlay(this.ballPossessingTeam);
                }
            }
            this.endPlay();
        }
    }
}

