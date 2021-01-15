package src.algorithmic.thor;

import src.game.Game;
import src.game.GameMove;

public interface Strategy
{
	StrategyType getAlgorithmType();
	boolean isTriggered(StrategySelector sm);
	int getPriority();
	void prepare(Game game);
	GameMove execute();
}
