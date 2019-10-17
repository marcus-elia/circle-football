import java.awt.*;

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

    }
}
