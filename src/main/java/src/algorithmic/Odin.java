package src.algorithmic;

import src.game.Direction;
import src.game.Game;
import src.game.GameMove;
import src.game.Player;

import java.util.*;

public class Odin extends AlgorithmicAI
{
    protected EvaluationMethod evalMethod = EvaluationMethod.area_div_enemies;
    private int depth = 2;
    private double exploration = Math.sqrt(2);

    public void setEvalMethod(EvaluationMethod evalMethod)
    {
        this.evalMethod = evalMethod;
    }

    public int getDepth()
    {
        return depth;
    }

    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    public double getExploration()
    {
        return exploration;
    }

    public void setExploration(double exploration)
    {
        this.exploration = exploration;
    }

    public enum SearchMethod
    {
        max,
        monte_carlo_tree
    }

    public enum EvaluationMethod
    {
        area,
        area_div_enemies,
        monte_carlo,
        area_monte_carlo
    }

    public Odin(Game game, int playerId)
    {
        super(game, playerId);
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
        HashSet<Clump> areas = new HashSet<>();
        int enemySum = 0;

        double score = 0;

        for (int p = 0; p < current.getPlayerCount(); ++p)
        {
            Player pl = current.getPlayer(p);
            if (!pl.isActive())
            {
                continue;
            }
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
                    if (p != playerId)
                    {
                        enemySum += eval;
                    }
                    areas.add(finder.getClumpAt(x, y));
                    max = Math.max(max, eval);
                }
            }

            if (p == playerId)
            {
                score += max;
            }
        }

        double allAreas = 0;
        for (Clump area : areas)
        {
            if (area != null)
            {
                allAreas += area.getLeafCount();
            }
        }

        if (evalMethod == EvaluationMethod.area_div_enemies)
        {
            //int alivePlayers = 6 - current.getDeaths();
            score /= enemySum + 1.0; //((double) enemySum / alivePlayers) + 1.0;
            //if (score > 10.0)
            //{
            //    score = 10.0 + Math.log(score);
            //}
            return score;
        }
        return score / finder.getAreaLeft();
    }



    public Number[] max(Game current, int playerId, double currentBest, int depth)
    {
        double max = currentBest;
        int best = 0;
        GameMove[] options = getOptions();
        GameMove[] moves = new GameMove[current.getPlayerCount()];

        for (int p = 0; p < current.getPlayerCount(); ++p)
        {
            moves[p] = GameMove.change_nothing;
        }

        for (int m = 0; m < options.length; ++m)
        {
            GameMove move = options[m];
            moves[playerId] = move;
            Game next = null;
            try
            {
                next = current.variant(Arrays.asList(moves.clone()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            double eval = evaluate(next, playerId);

            if (eval > 0 && eval > max * (1.0 / exploration) && depth > 0)
            {
                assert next != null;
                eval = max(next, playerId, max, depth-1)[0].doubleValue();
            }
            if (eval > max)
            {
                max = eval;
                best = m;
            }
        }

        return new Number[]{max, best};
    }

    @Override
    public GameMove decide()
    {
        Number[] result = max(game, playerId,0, depth);
        int index = Math.round(result[1].intValue());
        return getOptions()[index];
    }
}
