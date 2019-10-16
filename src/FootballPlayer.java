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

    public FootballPlayer(FootballGameManager inputManager,
                          double inputX, double inputY, ID id, double radius, Color color, double speed)
    {
        super(inputX, inputY, id);
        this.manager = inputManager;
        this.radius = radius;
        this.rand = new Random();
        this.color = color;
        this.speed = speed;
        this.newRandomTarget();
        this.setAngle();

        this.isBouncing = false;
        this.curBounceTime = 0;
        this.recoveryTime = 50;

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
			}
		}

		if (FootballPlayer.distance(this.getX(), this.getY(), targetX, targetY) > this.speed)
		{
			this.setX(this.getX() + this.getDX());
			this.setY(this.getY() + this.getDY());
		} else
		{
			this.setX(targetX);
			this.setY(targetY);
			this.newRandomTarget();
			this.setAngle();
		}
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

	public void setTarget(double x, double y)
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
	}

	public double getAngle()
	{
		return this.angle;
	}

	public void startBouncing()
	{
		this.isBouncing = true;
		this.curBounceTime = 0;
	}

	public boolean isCollided(FootballPlayer otherPlayer)
	{
		return FootballPlayer.distance(this.x, this.y, otherPlayer.getX(), otherPlayer.getY()) < 2*this.radius;
	}

}
