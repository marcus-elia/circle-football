import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Ball extends GameObject
{
    private FootballGameManager manager;
    private double radius;
    private double angle;
    private double speed;
    public Ball(FootballGameManager inputManager, double x, double y, ID id)
    {
        super(x, y, id);
        this.manager = inputManager;
        this.radius = 10;
        this.speed = 3;
    }

    public void tick()
    {
        this.bounceOffWalls();
        this.x += this.dx;
        this.y += this.dy;
    }

    public void render(Graphics2D g2d)
    {
        double radius = this.manager.getPlayerRadius();
        g2d.setColor(Color.YELLOW);
        Shape circle = new Ellipse2D.Double(this.x - radius, this.y - radius, radius * 2, radius * 2);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(circle);
    }

    public void bounceOffWalls()
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
    }

    public void setDxDy()
    {
        this.dx = this.speed * Math.cos(this.angle);
        this.dy = this.speed * Math.sin(this.angle);
    }
    public void setAngle(double angle)
    {
        this.angle = angle;
        this.setDxDy();
    }
}
