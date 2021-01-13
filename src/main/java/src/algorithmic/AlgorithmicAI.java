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
    protected static Random random;
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

    public enum RandomMethod
    {
        equal,
        equal_cn_first,
        weighted,
        weighted_cn_first,
        equal_allow_death,
        bot_look_ahead
    }

    public static GameMove botLookAhead(Game game, int playerId)
    {
        GameMove[] options = new GameMove[]{GameMove.turn_right, GameMove.change_nothing, GameMove.turn_left};
        Player p = game.getPlayer(playerId);
        int max = 0;
        GameMove result = GameMove.change_nothing;
        int head = (Game.direction2Int(p.getDirection()) + 3) % 4;
        for (int i = 0; i < 3; ++i)
        {
            int eval = lookAhead(game, p.getX(), p.getY(), Game.int2Direction(head));
            if (eval > max)
            {
                max = eval;
                result = options[i];
            }
            head += 1;
            head %= 4;
        }

        if (result == GameMove.change_nothing && p.getSpeed() > 1)
        {
            result = GameMove.slow_down;
        }

        return result;
    }

    public static GameMove randomMove(Game game, int playerId, RandomMethod randomMethod)
    {
        if (randomMethod == RandomMethod.bot_look_ahead)
        {
            return botLookAhead(game, playerId);
        }
        GameMove[] options = new GameMove[]{GameMove.change_nothing, GameMove.turn_right, GameMove.turn_left, GameMove.slow_down, GameMove.speed_up};

        Player player = game.getPlayer(playerId);
        List<GameMove> goodOptions = new ArrayList<>();

        for (int i = 0; i < 5; ++i)
        {
            if (randomMethod == RandomMethod.equal_allow_death || !game.isDeadlyMove(playerId, options[i]))
            {
                goodOptions.add(options[i]);
            }
        }

        if(goodOptions.size() > 0)
        {
            if ((   randomMethod == RandomMethod.equal_cn_first ||
                    randomMethod == RandomMethod.weighted_cn_first ) &&
                    goodOptions.get(0) == GameMove.change_nothing )
            {
                return GameMove.change_nothing;
            }

            int index = random.nextInt(goodOptions.size());
            if (    randomMethod == RandomMethod.equal ||
                    randomMethod == RandomMethod.equal_cn_first ||
                    randomMethod == RandomMethod.equal_allow_death)
            {
                return goodOptions.get(index);
            }

            double v = random.nextDouble();
            if (v < 0.8 && goodOptions.contains(GameMove.change_nothing))
            {
                return GameMove.change_nothing;
            }
            if (v < 0.875 && goodOptions.contains(GameMove.turn_left))
            {
                return GameMove.turn_left;
            }
            if (v < 0.95 && goodOptions.contains(GameMove.turn_right))
            {
                return GameMove.turn_right;
            }
            if (v < 0.99 && goodOptions.contains(GameMove.slow_down))
            {
                return GameMove.slow_down;
            }
            return GameMove.speed_up;
        }
        return GameMove.change_nothing;
    }

    public static double playRandomGame(Game g, int playerId, RandomMethod randomMethod)
    {
        while(g.isRunning())
        {
            int index = g.getCurrentPlayer();
            g.performMove(randomMove(g, index, randomMethod));
        }
        return (double) (g.getDeath(playerId)) / g.getPlayerCount();
    }

    public static int lookAhead(Game game, int px, int py, Direction d)
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

    public abstract GameMove decide();
}
