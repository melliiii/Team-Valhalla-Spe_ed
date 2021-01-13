package src.algorithmic;

import src.game.Game;

/**
 *
 */
public class VariantTracker
{
    private int[][] cellVisits;
    private int variantCount = 1;
    private Game original;
    private int playerId;
    public VariantTracker(Game original, int playerId)
    {
        this.original = original;
        this.playerId = playerId;
        cellVisits = new int[original.getHeight()][original.getWidth()];
        for (int x = 0; x < original.getWidth(); ++x)
        {
            for (int y = 0; y < original.getHeight(); ++y)
            {
                cellVisits[y][x] = 0;
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
                if (original.getCells()[y][x] != g.getCells()[y][x])// 0 && g.getCells()[y][x] == (playerId+1))
                {
                    cellVisits[y][x]++;
                }
            }
        }
        variantCount++;
    }

    public double getAvgVisits(int x, int y)
    {
        return (double) cellVisits[y][x] / variantCount;
    }
}
