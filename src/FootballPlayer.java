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

    public FootballPlayer(double inputX, double inputY, ID id, double radius)
    {
        super(inputX, inputY, id);
        this.radius = radius;
        this.rand = new Random();
    }

    public boolean isCollided(FootballPlayer otherPlayer)
	{
		this.collided = radius > (Math.sqrt(Math.pow(otherPlayer.getX() - this.getX(), 2) + Math.pow(otherPlayer.getY() - this.getY(), 2)));
		return radius > (Math.sqrt(Math.pow(otherPlayer.getX() - this.getX(), 2) + Math.pow(otherPlayer.getY() - this.getY(), 2)));
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
        g2d.setColor(Color.CYAN);
        Shape circle = new Ellipse2D.Double(this.x, this.y, radius * 2, radius * 2);
        g2d.fill(circle);
    }
}
