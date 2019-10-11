/*
This code is from https://www.youtube.com/watch?v=1gir2R7G9ws
It is a tutorial by RealTutsGML
 */



import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Game extends Canvas implements Runnable
{
    public static final int WIDTH = 768, HEIGHT = WIDTH/2;
    private Thread thread;
    private boolean running = false;

    private Handler handler;

    private FootballPlayer otherPlayer;
    private FootballPlayer samplePlayer;

    public Game()
    {

        handler = new Handler();
        new Window(WIDTH, HEIGHT, "Circle Football", this);
        //this.addKeyListener(new KeyInput(handler));

        FootballPlayer samplePlayer = new FootballPlayer(WIDTH/2, HEIGHT/2, ID.Player, 10);
        handler.addObject(samplePlayer);

        otherPlayer = new FootballPlayer( WIDTH/2 -100, HEIGHT/2, ID.Player, 10);
        handler.addObject(otherPlayer);

    }

    public synchronized void start()
    {
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    public synchronized void stop()
    {
        try
        {
            thread.join();
            running = false;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running)
        {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1)
            {
                tick();
                delta--;
            }
            if(running)
                render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000)
            {
                timer += 1000;
                System.out.println("FPS: "+ frames);
                frames = 0;
                System.out.println(otherPlayer.isCollided(samplePlayer));
            }

        }
        stop();
    }

    private void tick()
    {
        handler.tick();
    }

    private void render()
    {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null)
        {
            this.createBufferStrategy(3);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        handler.render(g);

        g.dispose();
        bs.show();
    }

    public static void main(String[] args)
    {
        new Game();

    }

}