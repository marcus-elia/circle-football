import java.awt.*;

// This class is just a football player that starts moving on a random
// direction, and then just bounces around. It does not do things with
// targeting coordinates.
public class RandomBouncer extends FootballPlayer
{
    private double radius;
    private FootballGameManager manager;

    RandomBouncer(FootballGameManager inputManager,
                  double inputX, double inputY, ID id, double radius, Color color, double speed)
    {
        super(inputManager, inputX, inputY, id, radius, color, speed);
        this.setAngle(Math.random() * 2*Math.PI);
        this.radius = radius;
        this.manager = inputManager;
    }

    public void tick()
    {
        if(this.x < this.radius || this.x > this.manager.getWidth() - this.radius)
        {
            this.dx = -this.dx;
        }
        if(this.y < this.radius || this.y > this.manager.getHeight() - this.radius)
        {
            this.dy = -this.dy;
        }

        this.x += this.dx;
        this.y += this.dy;
    }
}
