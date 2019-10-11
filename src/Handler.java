/*
This code is from https://www.youtube.com/watch?v=1gir2R7G9ws
It is a tutorial by RealTutsGML
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;

public class Handler
{
    LinkedList<GameObject> objects = new LinkedList<GameObject>();

    public void tick()
    {
        for(int i = 0; i < objects.size(); i++)
        {
            GameObject tempObject = objects.get(i);
            tempObject.tick();
        }
    }

    public void render(Graphics2D g)
    {
        for(int i = 0; i < objects.size(); i++)
        {
            GameObject tempObject = objects.get(i);
            tempObject.render(g);
        }
    }

    public void addObject(GameObject object)
    {
        this.objects.add(object);
    }

    public void removeObject(GameObject object)
    {
        this.objects.remove(object);
    }
}