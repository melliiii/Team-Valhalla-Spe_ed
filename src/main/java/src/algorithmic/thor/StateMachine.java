package src.algorithmic.thor;

import src.algorithmic.AlgorithmicAI;
import src.game.Game;
import src.game.GameMove;

public class StateMachine extends AlgorithmicAI
{
    StrategySelector selector;
    public StateMachine(Game game, int playerId)
    {
        super(game, playerId);
        selector = new StrategySelector();
        selector.addStrategy(new Coil(game, playerId));
        selector.addStrategy(new Escape(game, playerId));
        selector.addStrategy(new Thor(game, playerId));
        selector.addStrategy(new Defend(game, playerId));
        selector.addStrategy(new Claim(game, playerId));
    }

    @Override
    public GameMove decide()
    {
        return selector.selectStrategy(game).execute();
    }
}
