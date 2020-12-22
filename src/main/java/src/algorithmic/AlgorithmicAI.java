package src.algorithmic;

import src.game.Direction;
import src.game.Game;
import src.game.GameMove;
import src.game.Player;

import java.util.*;

class DistanceComparator implements Comparator<Player>
{
    private Player main;
    public DistanceComparator(Player main)
    {
        this.main = main;
    }
    @Override
    public int compare(Player o1, Player o2)
    {
        double d1 = main.getDistanceTo(o1);
        double d2 = main.getDistanceTo(o2);
        return Double.compare(d1, d2);
    }
}

public abstract class AlgorithmicAI
{
    protected Random random;
    protected AreaFinder finder;
    protected boolean shared;

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
        this.finder.setGame(game);
    }

    protected Game game;
    protected int playerId;

    public AlgorithmicAI(Game game, int playerId)
    {
        this.game = game;
        this.playerId = playerId;
        random = new Random();
        finder = new AreaFinder(game);
    }

    public void setFinder(AreaFinder finder)
    {
        this.finder = finder;
        shared = true;
    }

    public GameMove[] getOptions()
    {
        return new GameMove[]{
                GameMove.change_nothing,
                GameMove.turn_right,
                GameMove.turn_left,
                GameMove.speed_up,
                GameMove.slow_down
        };
    }

    public int lookAhead(int px, int py, Direction d)
    {
        int[] dxy = Game.direction2Delta(d);
        int x = px + dxy[0];
        int y = py + dxy[1];
        int counter = 1;
        while (game.positionExists(x, y) && game.getCells()[y][x] == 0)
        {
            x += dxy[0];
            y += dxy[1];
            counter++;
        }
        return counter;
    }

    public double nextPlayerDistance(Game current, Player player)
    {
        double nextPlayer = 100000;
        for (int p = 0; p < current.getPlayerCount(); ++p)
        {
            Player other = current.getPlayer(p);
            if (other != player && other.isActive())
            {
                double dx = player.getX() - other.getX();
                double dy = player.getY() - other.getY();
                double distance = Math.sqrt(dx*dx+dy*dy);
                nextPlayer = Math.min(nextPlayer, distance);
            }
        }

        return nextPlayer;
    }

    public double evaluate(Game current, int playerId)
    {
        Player player = current.getPlayer(playerId);
        if (!player.isActive())
        {
            return 0;
        }
        finder.setGame(current);
        finder.findAreas();

        double score = 0;
        double enemySum = 0;

        for (int p = 0; p < current.getPlayerCount(); ++p)
        {
            Player pl = current.getPlayer(p);
            int max = 0;
            for (int i = 0; i < 4; ++i)
            {
                Direction dir = Game.int2Direction(i);
                int[] dxy = Game.direction2Delta(dir);
                int x = pl.getX() + dxy[0];
                int y = pl.getY() + dxy[1];
                if (current.positionExists(x, y))
                {
                    int eval = finder.getAreaAt(x, y);
                    max = Math.max(max, eval);
                }
            }

            if (p == playerId)
            {
                score += max;
            }
            else
            {
                enemySum += max;
            }
        }

        //score /= enemySum * 0.5 + 1.0;
        int mainEnem = getMainEnemies(current, playerId).size();
        score /= (double)mainEnem * 1.0 + 1.0;
        //score *= Math.pow(nextPlayerDistance(current, player) / (double) current.getWidth(), 5);
        return score;
    }

    public SortedSet<Player> getMainEnemies(Game current, int playerId)
    {
        Player player = current.getPlayer(playerId);
        Comparator<Player> comp = new DistanceComparator(player);
        SortedSet<Player> result = new TreeSet<>(comp);

        Clump ownSpace = finder.getClumpAt(player.getNextX(), player.getNextY());
        for(int p = 0; p < current.getPlayerCount(); ++p)
        {
            if (p != playerId)
            {
                Player other = current.getPlayer(p);
                if (finder.getClumpAt(other.getNextX(), other.getNextY()) == ownSpace)
                {
                    result.add(other);
                }
            }
        }

        return result;
    }

    public abstract GameMove decide(int depth);
}
