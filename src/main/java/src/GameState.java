package src;

import java.util.Date;
import java.util.Map;

public class GameState {
    public int width = 40;
    public int height = 40;
    public int[][] cells;

    public Map<String, PlayerState> players;

    public int you = 0;
    public boolean running = false;
    public Date deadline;

    public String getMap(){
        StringBuilder map = new StringBuilder();
        for (int[] ignored : cells) {
            map.append("||");
        }
        map.append("||||\n");
        for (int[] row : cells) {
            map.append("||");
            for (int i : row) {
                map.append(i == 0 ? " " : i == -1 ? "X" : String.valueOf(i)).append(" ");
            }
            map.append("||\n");
        }
        for (int[] ignored : cells) {
            map.append("||");
        }
        map.append("||||");
        return map + "\n";
    }

    public Date getDeadline() {
        return deadline;
    }
}
