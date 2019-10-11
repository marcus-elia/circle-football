import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

public class FootballPlayer extends GameObject
{

    public FootballPlayer(double inputX, double inputY, ID id)
    {
        super(inputX, inputY, id);
    }

    public void tick()
    {
        this.x += 1;
    }

    public void render(Graphics2D g2d)
    {
        g2d.setColor(Color.CYAN);
        Shape circle = new Ellipse2D.Double(this.x, this.y, 20, 20);
        g2d.fill(circle);
    }
}
