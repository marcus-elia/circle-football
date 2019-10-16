import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

// This class is just a football player that starts moving on a random
// direction, and then just bounces around. It does not do things with
// targeting coordinates.
public class RandomBouncer extends FootballPlayer
{

    RandomBouncer(FootballGameManager inputManager,
                  double inputX, double inputY, ID id, double radius, Color color, double speed)
    {
        super(inputManager, inputX, inputY, id, radius, color, speed);
        this.setAngle(Math.random() * 2*Math.PI);
    }

    // This just bounces if it hits a wall.
    public void tick()
    {
        if(this.x < this.radius) // hitting the left
        {
            this.x = this.radius;
            this.dx = -this.dx;
        }
        else if(this.x > this.manager.getWidth() - this.radius) // hitting the right
        {
            this.x = this.manager.getWidth() - this.radius;
            this.dx = -this.dx;
        }
        if(this.y < this.radius) // hitting the top
        {
            this.y = this.radius;
            this.dy = -this.dy;
        }
        else if(this.y > this.manager.getHeight() - this.radius) // hitting the bottom
        {
            this.y = this.manager.getHeight() - this.radius;
            this.dy = -this.dy;
        }
        this.angle = Math.atan2(this.dy, this.dx);
        this.x += this.dx;
        this.y += this.dy;
    }

    public void render(Graphics2D g2d)
    {
        g2d.setColor(this.color);
        Shape circle = new Ellipse2D.Double(this.x - radius, this.y - radius, radius * 2, radius * 2);
        g2d.fill(circle);

        // For debugging purposes, draw a white line showing which way the player is facing
        g2d.setColor(Color.WHITE);
        g2d.draw(new Line2D.Double(this.x, this.y, this.x + 10*Math.cos(this.angle), this.y + 10*Math.sin(this.angle)));
    }

    public void reactToCollision(double otherX, double otherY)
    {
        double forceAngle = Math.atan2(this.y - otherY, this.x - otherX);
        double inverseAngle = this.angle + Math.PI;
        double difference = this.trueAngleDifference(forceAngle, inverseAngle);
        this.setAngle(forceAngle - difference);
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
}
