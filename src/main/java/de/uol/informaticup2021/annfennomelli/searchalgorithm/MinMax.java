package de.uol.informaticup2021.annfennomelli.searchalgorithm;

import de.uol.informaticup2021.annfennomelli.Game;
import de.uol.informaticup2021.annfennomelli.GameMove;
import de.uol.informaticup2021.annfennomelli.GameState;
import de.uol.informaticup2021.annfennomelli.PlayerState;

import java.util.List;

public class MinMax {
    /**
     * @return den GameMove, der für den Spieler "you" in dem gegebenen GameState als der beste
     * bewertet wird
     */
    public static GameMove calculateNextMove(GameState state, int ticks) {
        Game game = new Game(state, ticks);
        int max = Integer.MIN_VALUE;
        GameMove bestCandidateMove = GameMove.change_nothing;
        for (GameMove ourMove : GameMove.values()) {
            int min = Integer.MAX_VALUE;
            // Gehe alle mögliche Kombinationen von GameMove durch, die die anderen Spieler machen könnten
            for (GameMove move1 : GameMove.values()) {
                for (GameMove move2 : GameMove.values()) {
                    for (GameMove move3 : GameMove.values()) {
                        for (GameMove move4 : GameMove.values()) {
                            for (GameMove move5 : GameMove.values()) {
                                Game variant = game.variant(List.of(ourMove, move1, move2, move3,
                                        move4, move5));
                                GameState variantState = variant.getState();
                                PlayerState ourPlayerState =
                                        variantState.players.get(Integer.toString(variantState.you));
                                int score = -evaluate(variantState, ourPlayerState);
                                if (score < min) {
                                    min = score;
                                }
                            }
                        }
                    }
                }
            }
            if (min > max) {
                // If the "worst that could happen" after ourMove is better than
                // what we have been able to find so far, we have found a new candidate move
                bestCandidateMove = ourMove;
            }
        }
        return bestCandidateMove;
    }

    /**
     * @return eine Punktezahl, die bewertet, wie gut der gegebenen GameState für den gegebenen
     * Spieler wäre
     */
    public static int evaluate(GameState state, PlayerState ourPlayerState) {
        if (!ourPlayerState.active) {
            return Integer.MIN_VALUE; // Wir wollen auf keinen Fall sterben
        }
        return deadPlayersScore(state, ourPlayerState);
    }

    /**
     * @return 1 Punkt für jeden anderen Spieler, der tot ist
     */
    public static int deadPlayersScore(GameState state, PlayerState ourPlayerState) {
        int score = 0;
        for (PlayerState playerState : state.players.values()) {
            if (playerState == ourPlayerState) {
                continue;
            }
            if (!playerState.active) {
                score += 1;
            }
        }
        return score;
    }
}
