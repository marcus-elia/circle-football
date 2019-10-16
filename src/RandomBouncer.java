import java.awt.*;

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
