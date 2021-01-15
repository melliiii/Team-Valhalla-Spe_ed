package src.algorithmic.thor;

import src.algorithmic.AreaFinder;
import src.algorithmic.Odin;
import src.game.Game;
import src.game.GameMove;
import src.game.Player;

import java.util.SortedSet;

public class Thor extends Odin implements Strategy
{
    private int targetId;
    private int radius = 20;
    private boolean test = false;

    public Thor(Game game, int playerId)
    {
        super(game, playerId);
        if (playerId == 0)
        {
            //test = true;
        }
        setSearchMethod(SearchMethod.monte_carlo_tree);
        setDepth(3);
        setEvalMethod(EvaluationMethod.area_div_enemies);
        setIterations(15000);
        setBatchSize(30);
        setExploration(1.5);//Math.sqrt(2.0));
        setMemorizeTree(true);
        //setUsingC();
        setRandomMethod(RandomMethod.equal);
    }

    @Override
    public double evaluate(Game current, int playerId)
    {
        if (playerId != this.playerId)
        {
            return 0;
        }
        double score = super.evaluate(current, playerId);
        //    targetId = mainEnemies.first().getID()-1;
        //    enemyScore = super.evaluate(current, targetId);
        //}

        //Player p = current.getPlayer(playerId);
        //Player target = current.getPlayer(targetId);
        //double closeness = (current.getWidth() - p.getDistanceTo(target)) / current.getWidth();
        // Closer to Enemy == Better
        // Dead Enemies == Perfect
        //score *= 1+current.getDeaths();
        //score *= closeness * closeness;
        //score /= getMainEnemies(current, playerId).size() + 1.0;
        //score /= enemyScore;
        return score;
    }

    @Override
    public StrategyType getAlgorithmType()
    {
        return StrategyType.Aggressive;
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
        return mainEnemies.first().getDistanceTo(game.getPlayer(playerId)) < radius;
    }

    @Override
    public int getPriority()
    {
        SortedSet<Player> mainEnemies = getMainEnemies(game, playerId);

        // Important factors
        // 1. Closeness of the enemy
        // 2. Weakness of the enemy (Tbd)
        // 3. Area score (Tbd)

        return (int)(10.0 * ((radius - mainEnemies.first().getDistanceTo(game.getPlayer(playerId))) / radius));
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
        System.out.println("Thor");
        return decide();
    }

    @Override
    public GameMove decide()
    {
        SortedSet<Player> mainEnemies = getMainEnemies(game, playerId);
        if (test)
        {
            setMemorizeTree(mainEnemies.size() == 0);
        }
        return super.decide();
    }
}
