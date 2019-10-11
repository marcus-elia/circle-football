import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.Random;

public class FootballPlayer extends GameObject
{
	private double radius; //The radius of the player
	private static Random rand;	//RNG for movement
	private boolean collided;
	private Color color; //The color of the player

    private FootballGameManager manager;

    private double speed;
    private double angle;

    private double targetX;
    private double targetY;

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
    }

    public boolean isCollided(FootballPlayer otherPlayer)
	{
		this.collided = radius > FootballPlayer.distance(this.getX(), this.getY(), otherPlayer.getX(), otherPlayer.getY());
		return radius > FootballPlayer.distance(this.getX(), this.getY(), otherPlayer.getX(), otherPlayer.getY());
	}

    public void tick()
    {
		if (FootballPlayer.distance(this.getX(), this.getY(), targetX, targetY) > this.speed)
		{
			this.setX(this.getX() + this.getDX());
			this.setY(this.getY() + this.getDY());
		} else
		{
			this.setX(targetX);
			this.setY(targetY);
			this.newRandomTarget();


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

	public void newRandomTarget()
	{
		this.targetX = Math.random()*(this.manager.getWidth() - 4*this.radius) + 2*this.radius;
		this.targetY = Math.random()*(this.manager.getHeight() - 4*this.radius) + 2*this.radius;
	}

}
