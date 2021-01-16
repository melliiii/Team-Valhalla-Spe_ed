package src.algorithmic;

import src.game.Game;

/**
 *
 */
public class VariantTracker
{
    private int[][] cellVisits;
    private int[][] enemyCellVisits;
    private int variantCount = 1;
    private Game original;
    private int playerId;
    public VariantTracker(Game original, int playerId)
    {
        this.original = original;
        this.playerId = playerId;
        cellVisits = new int[original.getHeight()][original.getWidth()];
        enemyCellVisits = new int[original.getHeight()][original.getWidth()];
        for (int x = 0; x < original.getWidth(); ++x)
        {
            for (int y = 0; y < original.getHeight(); ++y)
            {
                cellVisits[y][x] = 0;
                enemyCellVisits[y][x] = 0;
            }
        }
    }

    public void addVariant(Game g)
    {
        // Look for playerIds changes in the maps
        for (int x = 0; x < g.getWidth(); ++x)
        {
            for (int y = 0; y < g.getHeight(); ++y)
            {
                if (original.getCells()[y][x] != g.getCells()[y][x])
                {
                    if (g.getCells()[y][x] == (playerId+1))
                    {
                        cellVisits[y][x]++;
                    }
                    else
                    {
                        enemyCellVisits[y][x]++;
                    }
                }
            }
        }
        variantCount++;
    }

    public double getAvgVisits(int x, int y)
    {
        return (double) cellVisits[y][x] / variantCount;
    }

    public double getAvgEnemyVisits(int x, int y)
    {
        return (double) enemyCellVisits[y][x] / variantCount;
    }
}
