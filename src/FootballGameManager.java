import java.awt.*;
import java.util.ArrayList;

public class FootballGameManager
{
    private Game theGame;
    private double playerRadius;
    private ArrayList<FootballPlayer> leftTeam;
    private ArrayList<FootballPlayer> rightTeam;

    public FootballGameManager(Game inputGame, double inputRadius)
    {
        this.theGame = inputGame;
        this.playerRadius = inputRadius;
        this.leftTeam = createTeam(5, Color.RED);
        this.rightTeam = createTeam(5, Color.CYAN);
    }

    public ArrayList<FootballPlayer> createTeam(int numPlayers, Color color)
    {
        double randomX, randomY;
        // The player can spawn anywhere on the field such that it is not overlapping with the border
        double spawnableWidth = this.theGame.getWidth() - 2*this.playerRadius;
        double spawnableHeight = this.theGame.getHeight() - 2*this.playerRadius;
        ArrayList<FootballPlayer> players = new ArrayList<FootballPlayer>();
        for(int i = 0; i < numPlayers; i++)
        {
            randomX = Math.random()*spawnableWidth + this.playerRadius;
            randomY = Math.random()*spawnableHeight + this.playerRadius;
            players.add(new FootballPlayer(randomX, randomY, ID.Player, this.playerRadius, color));
        }

        return players;
    }

    public void tick()
    {
        for(FootballPlayer curPlayer : leftTeam)
        {
            curPlayer.tick();
        }
        for(FootballPlayer curPlayer : rightTeam)
        {
            curPlayer.tick();
        }
    }

    public void render(Graphics2D g2d)
    {
        for(FootballPlayer curPlayer : leftTeam)
        {
            curPlayer.render(g2d);
        }
        for(FootballPlayer curPlayer : rightTeam)
        {
            curPlayer.render(g2d);
        }
    }
}
