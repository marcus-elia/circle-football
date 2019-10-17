import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Ball extends GameObject
{
    private FootballGameManager manager;
    public Ball(FootballGameManager inputManager, double x, double y, ID id)
    {
        super(x, y, id);
        this.manager = inputManager;
    }

    public void tick()
    {

    }

    public void render(Graphics2D g2d)
    {
        double radius = this.manager.getPlayerRadius();
        g2d.setColor(Color.YELLOW);
        Shape circle = new Ellipse2D.Double(this.x - radius, this.y - radius, radius * 2, radius * 2);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(circle);
    }
}
