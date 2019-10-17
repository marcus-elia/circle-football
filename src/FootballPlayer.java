import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.Random;

public class FootballPlayer extends GameObject
{
	protected double radius; //The radius of the player
	private static Random rand;	//RNG for movement
	protected Color color; //The color of the player

    protected FootballGameManager manager;

    protected double speed;
    protected double angle;

    private double targetX;
    private double targetY;


    // If the player is currently bouncing (after a collision)
	private boolean isBouncing;
    // The number of ticks that the player has currently spent bouncing
    private int curBounceTime;
    // The number of ticks the player bounces for before returning to
	// going toward the target
    private int recoveryTime;

    // determines which spot the player will line up in
	private int teamIndex;
    // If the player is in the correct position before the play starts
    private boolean isInPosition;
    private Team whichTeam;

    public FootballPlayer(FootballGameManager inputManager, int teamIndex, Team whichTeam,
                          double inputX, double inputY, ID id, double radius, Color color, double speed)
    {
        super(inputX, inputY, id);
        this.manager = inputManager;
        this.radius = radius;
		this.teamIndex = teamIndex;
		this.whichTeam = whichTeam;
		this.color = color;
		this.speed = speed;

		this.isInPosition = false;
        this.rand = new Random();


        this.isBouncing = false;
        this.curBounceTime = 0;
        this.recoveryTime = 50;


		this.setPositionForPlay();
    }



    public void tick()
    {
    	// If the player is still reacting to a collision
		if(this.isBouncing)
		{
			this.curBounceTime++;
			if(this.curBounceTime == this.recoveryTime)
			{
				this.isBouncing = false;
				this.isInPosition = false;
				this.setAngle();
			}
			this.x += this.dx;
			this.y += this.dy;
		}
		// Otherwise, move toward the target
		else
		{
		    // If this is pre-play, go line up
		    if(!this.manager.getInProgress())
            {
                if(!this.isInPosition)
                {
                    if (FootballPlayer.distance(this.x, this.y, targetX, targetY) > this.speed)
                    {
                        this.x += this.dx;
                        this.y += this.dy;
                    }
                    else
                    {
                        this.x = this.targetX;
                        this.y = this.targetY;
                        this.isInPosition = true;
                    }
                }
            }

		}


		this.bounceOffWalls();
    }

    public void render(Graphics2D g2d)
    {
        g2d.setColor(this.color);
        Shape circle = new Ellipse2D.Double(this.x - radius, this.y - radius, radius * 2, radius * 2);
        g2d.fill(circle);
    }

    public static double distance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	public void setAngle()
	{
		this.angle = Math.atan2(this.targetY - this.y, this.targetX - this.x);
		this.setDxDy();
	}

	public void setAngle(double angle)
	{
    	this.angle = angle;
		this.setDxDy();
	}

	public void setDxDy()
	{
		this.dx = this.speed * Math.cos(this.angle);
		this.dy = this.speed * Math.sin(this.angle);
	}

	public void setSpeed(double speed)
	{
		if (speed >= 0) {
			this.speed = speed;
		} else {
			this.speed = 0;
		}
		setDxDy();
	}

	public void newRandomTarget()
	{
		this.targetX = Math.random()*(this.manager.getWidth() - 4*this.radius) + 2*this.radius;
		this.targetY = Math.random()*(this.manager.getHeight() - 4*this.radius) + 2*this.radius;
		this.setAngle();
	}

	/*public void setTarget(double x, double y)
	{
		if (x > manager.getWidth() - 2*radius)
		{
			targetX = manager.getWidth() - 2*radius;
		}
		else if (x < 0 + 2*radius)
		{
			targetX = 0 + 2*radius;
		}
		else
		{
			this.targetX = x;
		}
		if (y > manager.getHeight() - 2*radius)
		{
			targetY = manager.getHeight() - 2*radius;
		}
		else if (y < 0 + 2*radius)
		{
			targetY = 0 + 2*radius;
		}
		else
		{
			this.targetY = y;
		}
		this.setAngle();
	}*/

	public void setPositionForPlay()
	{
		GameStatus status = this.manager.getStatus();
		if(status == GameStatus.LeftKickoff)
		{
			if(this.whichTeam == Team.left)
			{
				this.targetX = this.manager.getWidth() / 12.0;
				this.targetY = this.manager.getHeight() * (this.teamIndex + 1.0) / 6;
			}
			else
			{
				this.targetX = this.manager.getWidth() - this.manager.getWidth() / 12.0;
				this.targetY = this.manager.getHeight() * (this.teamIndex + 1.0) / 6;
			}
		}
		this.setAngle();
	}

	public double getAngle()
	{
		return this.angle;
	}

	// =====================================
	//
	//    Collision Reaction and Bouncing
	//
	// =====================================

	public void startBouncing()
	{
		this.isBouncing = true;
		this.curBounceTime = 0;
		this.isInPosition = false;
	}

	// Returns the difference between angle1 and angle2 between -Pi and Pi.
	// For example, trueAngleDifference(Pi/2, 0) = -Pi/2
	//              trueAngleDifference(0, Pi/2) = Pi/2
	//              trueAngleDifference(0, 3Pi/2) = -Pi/2
	//              trueAngleDifference(3Pi/2, 0) = Pi/2
	public double trueAngleDifference(double angle1, double angle2)
	{
		double difference = angle2 - angle1;
		if(difference > Math.PI)
		{
			return difference - 2*Math.PI;
		}
		if(difference < -Math.PI)
		{
			return difference + 2*Math.PI;
		}
		return difference;
	}

	public void reactToCollision(double otherX, double otherY)
	{
		double forceAngle = Math.atan2(this.y - otherY, this.x - otherX);
		double inverseAngle = this.angle + Math.PI;
		double difference = this.trueAngleDifference(forceAngle, inverseAngle);
		this.setAngle(forceAngle - difference);
        this.startBouncing();
	}
	public boolean isCollided(FootballPlayer otherPlayer)
	{
		return FootballPlayer.distance(this.x, this.y, otherPlayer.getX(), otherPlayer.getY()) < 2*this.radius;
	}

	// Checks if this is touching a wall, and changes the velocity components accordingly
	public void bounceOffWalls()
	{
		if(this.x < this.radius) // hitting the left
		{
			this.x = this.radius;
			this.dx = -this.dx;
			this.startBouncing();
		}
		else if(this.x > this.manager.getWidth() - this.radius) // hitting the right
		{
			this.x = this.manager.getWidth() - this.radius;
			this.dx = -this.dx;
            this.startBouncing();
		}
		if(this.y < this.radius) // hitting the top
		{
			this.y = this.radius;
			this.dy = -this.dy;
            this.startBouncing();
		}
		else if(this.y > this.manager.getHeight() - this.radius) // hitting the bottom
		{
			this.y = this.manager.getHeight() - this.radius;
			this.dy = -this.dy;
            this.startBouncing();
		}
		this.angle = Math.atan2(this.dy, this.dx);
	}

	// A getter
	public boolean isInPosition()
	{
		return this.isInPosition;
	}

}
