package src.algorithmic;

import src.game.Game;

public class AreaFinder
{
    private Clump[][] collected;
    private Game game;

    /*
    * How to enable dynamic connection managing
    * 1. Dont delete nodes. Just deactivate them.
    * 2. If two active nodes are connected,
    *    their parents are connected, too
    * 3. If a node is connected to an inactive node,
    *    their parent is inactive, too
    *    But how?
    *
    * */

    public AreaFinder(Game game)
    {
        this.game = game;
        collected = new Clump[game.getHeight()][game.getWidth()];
        Clump.initialCapacity = game.getWidth() * game.getHeight();
    }

    private void connectAt(int x, int y)
    {
        if (x > 0 && collected[y][x - 1] != null)
        {
            Clump.merge(collected[y][x], collected[y][x - 1]);
        }
        if (y > 0 && collected[y - 1][x] != null)
        {
            Clump.merge(collected[y][x], collected[y - 1][x]);
        }
    }

    public void setGame(Game g)
    {
        this.game = g;
    }

    public void findAreas()
    {
        for (int x = 0; x < game.getWidth(); ++x)
        {
            for (int y = 0; y < game.getHeight(); ++y)
            {
                if (collected[y][x] == null && game.getCells()[y][x] == 0)
                {
                    collected[y][x] = new Clump(0);
                    collected[y][x].setLeafCount(1);
                }
                else if (game.getCells()[y][x] == 0)
                {
                    collected[y][x].clear();
                    collected[y][x].setLeafCount(1);
                }
                else {
                    collected[y][x] = null;
                }
            }
        }
        for (int x = 0; x < game.getWidth(); ++x)
        {
            for (int y = 0; y < game.getHeight(); ++y)
            {
                if (game.getCells()[y][x] == 0)
                {
                    connectAt(x, y);
                }
            }
        }
    }

    public Clump[][] getCollected()
    {
        return collected;
    }

    public int getAreaAt(int px, int py)
    {
        if (!game.positionExists(px, py) || game.getCells()[py][px] != 0)
        {
            return 0;
        }
        return getClumpAt(px, py).getLeafCount();
    }

    public Clump getClumpAt(int px, int py)
    {
        if(!game.positionExists(px, py))
        {
            return null;
        }
        if (collected == null || collected[py][px] == null)
        {
            return null;
        }
        return collected[py][px].getLastParent();
    }
}
