package src;

import java.util.Arrays;

public class Odin extends AlgorithmicAI
{
    public Odin(Game game, int playerId)
    {
        super(game, playerId);
    }

    public Number[] maximax(Game current, double currentBest, int depth)
    {
        double max = currentBest;
        int best = 0;
        GameMove[] options = getOptions();
        GameMove[] moves = new GameMove[6];

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

            if (eval > max / 2 && depth > 0)
            {
                eval = maximax(next, max, depth-1)[0].doubleValue();
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
    public GameMove decide(int depth)
    {
        return getOptions()[Math.round(maximax(game, 0, depth)[1].intValue())];
    }
}
