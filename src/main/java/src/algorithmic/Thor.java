package src.algorithmic;

import src.game.Direction;
import src.game.Game;
import src.game.GameMove;
import src.game.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Thor extends AlgorithmicAI
{
    public enum Strategy
    {
        // Basic Survival Strategies
        Coil,       // Fill the current Space perfectly
        Escape,     // Pathfind to the promised land

        // Defensive Strategies
        Claim,      // Expand or create base
        Castle,     // Strengthen defensive structures
        Defend,     // React to Invasion

        // Aggressive Strategies
        Hunt,       // Get close to an enemy
        Attack,     // Seek and Destroy an enemy
        Invade      // Get into
    }

    public Map<Strategy, AlgorithmicAI> strategy = new HashMap<>();

    public Thor(Game game, int playerId)
    {
        super(game, playerId);
    }

    public Number[] maximax(Game current, double currentBest, int depth)
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

            if (eval > max * 0.25 && depth > 0)
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

    public GameMove coil()
    {
        finder.setGame(game);
        finder.findAreas();
        Player player = game.getPlayer(playerId);
        GameMove result = GameMove.change_nothing;

        // Collect some data to decide upon
        int[] dxy = Game.direction2Delta(player.getDirection());
        double nextPlayer = nextPlayerDistance(game, player);

        int max = -1;

        int playerDir = Game.direction2Int(player.getDirection());
        int offset = playerDir;

        for (int i = 0; i < 4; ++i)
        {
            int head = (i+offset) % 4;
            int[] idxy = Game.direction2Delta(Game.int2Direction(head));
            if (game.positionExists(player.getX() + idxy[0], player.getY() + idxy[1]))
            {
                int ahead = lookAhead(player.getX(), player.getY(), Game.int2Direction(head));
                int space = finder.getAreaAt(player.getX() + idxy[0], player.getY() + idxy[1]);

                if (space > max)
                {
                    max = space;
                    result = GameMove.change_nothing;

                    if(playerDir == (head+3) % 4)
                    {
                        result = GameMove.turn_left;
                    }
                    if(playerDir == (head+1) % 4)
                    {
                        result = GameMove.turn_right;
                    }
                }
            }
        }
        return result;
    }

    public GameMove escape()
    {
        return GameMove.change_nothing;
    }

    public GameMove claim()
    {
        return GameMove.change_nothing;
    }

    public GameMove castle()
    {
        return GameMove.change_nothing;
    }

    public GameMove defend()
    {
        return GameMove.change_nothing;
    }

    public GameMove hunt()
    {
        return GameMove.change_nothing;
    }

    public GameMove attack()
    {
        return GameMove.change_nothing;
    }

    public GameMove invade()
    {
        return GameMove.change_nothing;
    }

    public Strategy selectBestStrategy()
    {
        return Strategy.Coil;
    }

    @Override
    public GameMove decide(int depth)
    {
        Strategy s = selectBestStrategy();


        return GameMove.change_nothing;
    }
}
