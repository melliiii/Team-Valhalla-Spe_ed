import java.awt.geom.Area;
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

public class Brunhilde extends AlgorithmicAI
{
    public Brunhilde(Game game, int playerId)
    {
        super(game, playerId);
    }

    public double min(Game current, GameMove playerMove, int depth, double alpha, double beta)
    {
        GameMove[] options = getOptions();
        SortedSet<Player> enemies = getMainEnemies(current, playerId);
        int enemyCount = Math.min(enemies.size(), 1);

        GameMove[][] possible = new GameMove[(int)Math.pow(options.length, enemyCount)][current.getPlayerCount()];

        double vmin = beta;
        for (int o = 0; o < possible.length; ++o)
        {
            int op = 0;
            for (int p = 0; p < current.getPlayerCount(); ++p)
            {
                if (op < enemyCount && enemies.contains(current.getPlayer(p)))
                {
                    int option = (o / (int)(Math.pow(options.length, op))) % options.length;
                    possible[o][p] = options[option];
                    ++op;
                }
                else if (p == playerId)
                {
                    possible[o][p] = playerMove;
                }
                else
                {
                    possible[o][p] = GameMove.change_nothing;
                }
            }

            Game next = current.variant(Arrays.asList(possible[o].clone()));
            finder.setGame(next);
            finder.findAreas();
            double eval = evaluate(next, playerId);
            if (depth > 0 && eval > alpha)
            {
                eval = max(next, depth - 1, alpha, vmin)[0].doubleValue();
            }

            if (eval < vmin)
            {
                vmin = eval;
                if (vmin <= alpha)
                {
                    break;
                }
            }
        }

        return vmin;
    }

    public Number[] max(Game current, int depth, double alpha, double beta)
    {
        finder.setGame(current);
        finder.findAreas();
        if (evaluate(current, playerId) == 0 || !current.getPlayer(playerId).isActive())
        {
            return new Number[]{0.0, 0};
        }
        GameMove[] options = getOptions();
        double bestEval = alpha;
        int bestMove = 0;
        for(int o = 0; o < options.length; ++o)
        {
            double eval = min(current, options[o], depth, bestMove, beta);

            if (eval > bestEval)
            {
                bestEval = eval;
                bestMove = o;
                if (bestEval >= beta)
                {
                    break;
                }
            }
        }
        return new Number[]{bestEval, bestMove};
    }

    public GameMove minimax(Game current, int depth)
    {
        return getOptions()[max(current, depth, -1000000, 1000000)[1].intValue()];
    }

    @Override
    public GameMove decide(int depth)
    {
        return minimax(game, depth);
    }
}
