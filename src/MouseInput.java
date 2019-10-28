import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseInput implements MouseListener
{

    private FootballGameManager manager;

    public MouseInput(FootballGameManager manager)
    {
        this.manager = manager;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        int mx = e.getX();
        int my = e.getY();
        if(manager.getTimeSinceLastClick() == 100)
        {
            manager.pushEveryone(mx, my);
        }
        manager.setTimeSinceLastClick(0);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
