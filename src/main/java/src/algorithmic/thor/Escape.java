package src.algorithmic.thor;

import src.algorithmic.Odin;
import src.game.Game;
import src.game.GameMove;
import src.game.Player;

import java.util.SortedSet;

public class Escape extends Odin implements Strategy
{
    public Escape(Game game, int playerId)
    {
        super(game, playerId);
        setSearchMethod(SearchMethod.monte_carlo_tree);
        setEvalMethod(EvaluationMethod.area);
        setIterations(500);
    }

    @Override
    public double evaluate(Game current, int playerId)
    {
        double score = super.evaluate(current, playerId);
        score /= getMainEnemies(current, playerId).size();
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
        return true;
    }

    @Override
    public int getPriority()
    {
        // Important factors
        // 1. This is not the best place to be in
        // 2. This is a sort of closed area
        double closedness = 1.0 - finder.calculateOpenness(playerId);
        double areaQuality = finder.getMaxAreaAround(playerId) / finder.getReachableGameArea();
        areaQuality /= getMainEnemies(game, playerId).size()+1;

        return (int)(10.0 * (closedness * (1.0-areaQuality)));
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
        System.out.println("Escape");
        return decide();
    }
}
