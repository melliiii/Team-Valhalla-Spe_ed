package src.algorithmic.thor;

import src.algorithmic.Odin;
import src.game.Game;
import src.game.GameMove;
import src.game.Player;

import java.util.SortedSet;

public class Defend extends Odin implements Strategy
{
    public Defend(Game game, int playerId)
    {
        super(game, playerId);
        setSearchMethod(SearchMethod.monte_carlo_tree);
        setEvalMethod(EvaluationMethod.area_div_enemies);
        setIterations(500);
    }

    @Override
    public double evaluate(Game current, int playerId)
    {
        double score = super.evaluate(current, playerId);
        return score;
    }

    @Override
    public StrategyType getAlgorithmType()
    {
        return StrategyType.Defensive;
    }

    @Override
    public boolean isTriggered(StrategySelector sm)
    {
        // A player needs to be in the radius
        SortedSet<Player> mainEnemies = getMainEnemies(game, playerId);
        if (mainEnemies.size() == 0)
        {
            return false;
        }
        return true;
    }

    @Override
    public int getPriority()
    {
        // Important factors
        // 1. This is the best place to be in
        // 2. There is an enemy here (trigger)
        // 3. This is a sort of closed area
        double closedness = 1.0 - finder.calculateOpenness(playerId);
        double areaQuality = finder.getMaxAreaAround(playerId) / finder.getReachableGameArea();

        return (int)(10.0 * (closedness * areaQuality));
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
        System.out.println("Defend");
        return decide();
    }
}
