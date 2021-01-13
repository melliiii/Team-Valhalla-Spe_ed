package src.algorithmic.thor;

import src.algorithmic.Clump;
import src.algorithmic.Odin;
import src.game.Direction;
import src.game.Game;
import src.game.GameMove;
import src.game.Player;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Coil extends Odin implements Strategy
{
    public Coil(Game game, int playerId)
    {
        super(game, playerId);
    }

    @Override
    public GameMove[] getOptions()
    {
        finder.setGame(game);
        finder.findAreas();

        // Sort options by field connections
        List<GameMove>[] order = new List[4];
        for (int i = 0; i < 4; ++i)
        {
            order[i] = new LinkedList<>();
        }

        GameMove[] basic = new GameMove[]{GameMove.turn_right, GameMove.change_nothing, GameMove.turn_left};

        Player player = game.getPlayer(playerId);
        int head = Game.direction2Int(player.getDirection());
        for (int i = -1; i < 2; ++i)
        {
            Direction dir = Game.int2Direction((head+i+4) % 4);
            int[] dxy = Game.direction2Delta(dir);
            int x = player.getX() + dxy[0];
            int y = player.getY() + dxy[1];
            if (game.positionExists(x, y) && game.getCells()[y][x] == 0)
            {
                int c = finder.getFieldAt(x, y).getConnections();
                if(c > 0)
                {
                    order[c-1].add(basic[i+1]);
                }
            }
        }

        List<GameMove> result = new LinkedList<>();
        int c = 0;
        for (int i = 0; i < 3; ++i)
        {
            while (c < 4 && order[c].size() == 0)
            {
                ++c;
            }
            if (c > 3)
            {
                break;
            }

            GameMove elem = order[c].iterator().next();
            result.add(elem);
            order[c].remove(elem);
        }
        return result.toArray(basic);
    }

    @Override
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

        int max = 0;
        for (int i = 0; i < 4; ++i)
        {
            Direction dir = Game.int2Direction(i);
            int[] dxy = Game.direction2Delta(dir);
            int x = player.getX() + dxy[0];
            int y = player.getY() + dxy[1];
            if (current.positionExists(x, y))
            {
                int eval = finder.getAreaAt(x, y);
                max = Math.max(max, eval);
            }
        }

        score += max;
        return score;
    }

    @Override
    public StrategyType getAlgorithmType()
    {
        return StrategyType.Basic;
    }

    @Override
    public boolean isTriggered(StrategySelector sm)
    {
        return getMainEnemies(game, playerId).size() == 0 && game.getPlayer(playerId).getSpeed() == 1;
    }

    @Override
    public int getPriority()
    {
        // Is this the best Place to be in?
        double area = finder.getMaxAreaAround(playerId);
        double allAreas = finder.getReachableGameArea();
        return (int)(10.0 * (area / allAreas));
    }

    @Override
    public void prepare(Game game)
    {
        setGame(game);
        finder.findAreas();
    }

    @Override
    public GameMove execute()
    {
        System.out.println("Coil");
        return decide();
    }
}
