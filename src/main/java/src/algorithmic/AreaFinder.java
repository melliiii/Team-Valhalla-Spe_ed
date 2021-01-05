package src.algorithmic;

import src.game.Game;

public class AreaFinder
{
    private Clump[][][] collected;
    private Game game;

    public AreaFinder(Game game)
    {
        this.game = game;
        collected = new Clump[1][][];
        collected[0] = new Clump[game.getHeight()][game.getWidth()];
        Clump.initialCapacity = game.getWidth() * game.getHeight();
    }

    private void connectAt(int c, int x, int y)
    {
        if (x > 0 && collected[c][y][x - 1] != null)
        {
            Clump.merge(collected[c][y][x], collected[0][y][x - 1]);
        }
        if (y > 0 && collected[c][y - 1][x] != null)
        {
            Clump.merge(collected[c][y][x], collected[c][y - 1][x]);
        }
    }

    public void setGame(Game g)
    {
        this.game = g;
    }

    public int countConnections(int c, int x, int y)
    {
        int counter = 0;
        for (int i = 0; i < 4; ++i)
        {
            int[] dxy = Game.direction2Delta(Game.int2Direction(i));
            if (    game.positionExists(x+dxy[0], y+dxy[1]) &&
                    collected[c][y+dxy[1]][x+dxy[0]] != null)
            {
                counter++;
            }
        }
        return counter;
    }

    public void findAreas()
    {
        findAreas(0);
    }
    public void findAreas(int c)
    {
        for (int x = 0; x < game.getWidth(); ++x)
        {
            for (int y = 0; y < game.getHeight(); ++y)
            {
                if (collected[c][y][x] == null && game.getCells()[y][x] == 0)
                {
                    collected[c][y][x] = new Clump(0);
                    collected[c][y][x].setLeafCount(1);
                }
                else if (game.getCells()[y][x] == 0)
                {
                    collected[c][y][x].clear();
                    collected[c][y][x].setLeafCount(1);
                }
                else {
                    collected[c][y][x] = null;
                }
            }
        }
        for (int i = 4; i > 0; --i)
        {
            for (int x = 0; x < game.getWidth(); ++x)
            {
                for (int y = 0; y < game.getHeight(); ++y)
                {
                    if (countConnections(c, x, y) != i)
                    {
                        if (game.getCells()[y][x] == 0)
                        {
                            connectAt(c, x, y);
                        }
                    }
                }
            }
        }
    }

    public Clump[][] getCollected()
    {
        return collected[0];
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
        if (collected == null || collected[0][py][px] == null)
        {
            return null;
        }
        return collected[0][py][px].getLastParent();
    }
}
