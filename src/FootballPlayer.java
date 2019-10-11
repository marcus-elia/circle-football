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
    }

    public boolean isCollided(FootballPlayer otherPlayer)
	{
		this.collided = radius > FootballPlayer.distance(this.getX(), this.getY(), otherPlayer.getX(), otherPlayer.getY());
		return radius > FootballPlayer.distance(this.getX(), this.getY(), otherPlayer.getX(), otherPlayer.getY());
	}

    public void tick()
    {
    	if (!this.collided) {
			if (this.rand.nextBoolean())//true x or false y
			{
				if (this.rand.nextBoolean()) //true positive or false negative
				{
					this.setX(this.getX() + 1);
				} else {
					this.setX(this.getX() - 1);
				}
			} else {
				if (this.rand.nextBoolean()) //true positive or false negative
				{
					this.setY(this.getY() + 1); //Positive y is down
				} else {
					this.setY(this.getY() - 1);
				}
			}
		}
    }

    public void render(Graphics2D g2d)
    {
        g2d.setColor(this.color);
        Shape circle = new Ellipse2D.Double(this.x, this.y, radius * 2, radius * 2);
        g2d.fill(circle);
    }

    public static double distance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
}
