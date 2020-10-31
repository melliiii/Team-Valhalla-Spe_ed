package de.uol.informaticup2021.annfennomelli.searchalgorithm;

import de.uol.informaticup2021.annfennomelli.GameMove;
import de.uol.informaticup2021.annfennomelli.GameState;
import de.uol.informaticup2021.annfennomelli.PlayerState;

public class MinMax {
    public static GameMove calculateNextMove(GameState state) {
        PlayerState ourPlayerState = state.players.get(Integer.toString(state.you));
        return GameMove.change_nothing;
    }

    /**
     * Weist dem gegebenen GameState eine Punktezahl zu, die bewertet, wie gut der GameState
     * für den Player des gegebenen PlayerStates sein würde.
     */
    public static int evaluate(GameState state, PlayerState ourPlayerState) {
        if (!ourPlayerState.active) {
            return Integer.MIN_VALUE; // Wir wollen auf keinen Fall sterben
        }
        return deadPlayersScore(state, ourPlayerState);
    }

    // Weist Pluspunkte zu, wenn andere Spieler tot sind.
    public static int deadPlayersScore(GameState state, PlayerState ourPlayerState) {
        int score = 0;
        for (PlayerState playerState : state.players.values()) {
            if (playerState == ourPlayerState) {
                continue;
            }
            if (!playerState.active) { // 1 Punkt für jeden anderen Spieler, der tot ist
                score += 1;
            }
        }
        return score;
    }
}
