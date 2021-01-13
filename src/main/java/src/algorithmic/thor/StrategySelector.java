package src.algorithmic.thor;

import src.algorithmic.AreaFinder;
import src.game.Game;
import src.game.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrategySelector
{
    /*
     * StateMachine Variables
     */
    List<Strategy> algorithms;

    /*
     *  Decision Variables
     */
    double openness = 0.6;                      // Openness of the Map
    double aggro = 0;                           // Current Algorithm Aggressiveness
    double baseAggro = 0.5;                     // Default Aggressiveness
    HashMap<Player, Double> playerAggro;        // Aggressiveness of each player in the Lobby
    HashMap<Player, Area> playerClaims;         // Claims of each player

    public StrategySelector()
    {
        algorithms = new ArrayList<>();
    }

    public Strategy selectStrategy(Game game)
    {
        HashMap<Integer, Strategy> available_algorithms = new HashMap<>();
        for (Strategy algo : algorithms)
        {
            algo.prepare(game);
            if (algo.isTriggered(this))
            {
                available_algorithms.put(algo.getPriority(), algo);
            }
        }
        int highest_prio = 0;
        Strategy algo = null;
        for (Map.Entry<Integer, Strategy> entry : available_algorithms.entrySet())
        {
            if (entry.getKey() >= highest_prio)
            {
                highest_prio = entry.getKey();
                algo = entry.getValue();
            }
        }
        return algo;
    }

    public List<Strategy> getStrategies()
    {
        return algorithms;
    }

    public void addStrategy(Strategy algorithm)
    {
        this.algorithms.add(algorithm);
    }

    public double getOpenness()
    {
        return openness;
    }

    public void setOpenness(double openness)
    {
        this.openness = openness;
    }

    public double getAggro()
    {
        return aggro;
    }

    public void setAggro(double aggro)
    {
        this.aggro = aggro;
    }

    public double getBaseAggro()
    {
        return baseAggro;
    }

    public void setBaseAggro(double baseAggro)
    {
        this.baseAggro = baseAggro;
    }

    public HashMap<Player, Double> getPlayerAggro()
    {
        return playerAggro;
    }

    public void setPlayerAggro(HashMap<Player, Double> playerAggro)
    {
        this.playerAggro = playerAggro;
    }

    public HashMap<Player, Area> getPlayerClaims()
    {
        return playerClaims;
    }

    public void setPlayerClaims(HashMap<Player, Area> playerClaims)
    {
        this.playerClaims = playerClaims;
    }
}
