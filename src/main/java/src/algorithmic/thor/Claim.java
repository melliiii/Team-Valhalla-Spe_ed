package src.algorithmic.thor;

import src.algorithmic.Odin;
import src.game.Game;
import src.game.GameMove;

public class Claim extends Thor implements Strategy
{
    public Claim(Game game, int playerId)
    {
        super(game, playerId);
        setEvalMethod(EvaluationMethod.area_monte_carlo);
        setIterations(1000);
        setBatchSize(1);
        setExploration(1.01);
        setMemorizeTree(true);
    }

    @Override
    public double evaluate(Game current, int playerId)
    {
        return super.evaluate(current, playerId);
    }

    @Override
    public StrategyType getAlgorithmType()
    {
        return StrategyType.Defensive;
    }

    @Override
    public boolean isTriggered(StrategySelector sm)
    {
        // Begin of the game
        // Min half of players in my area
        return getMainEnemies(game, playerId).size() > game.getPlayerCount() / 2;
    }

    @Override
    public int getPriority()
    {
        // Only do this in very good open Spaces with many enemies
        double enemyRatio = ((double)getMainEnemies(game, playerId).size() / game.getPlayerCount());
        double openness = finder.calculateOpenness(playerId);
        double area = finder.getMaxAreaAround(playerId);
        double allAreas = finder.getReachableGameArea();
        double quality = area / allAreas;
        return (int)(10.0 * enemyRatio * openness * quality);
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
        System.out.println("Claim");
        return decide();
    }
}
