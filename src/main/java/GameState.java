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
        String map = "";
        for (int[] row : cells) {
            map += "||";
        }
        map += "||||\n";
        for (int[] row : cells) {
            map += "||";
            for (int i : row) {
                map += (i == 0 ? " " : i == -1 ? "X" : i) + " ";
            }
            map += "||\n";
        }
        for (int[] row : cells) {
            map += "||";
        }
        map += "||||";
        return map + "\n";
    }

    public Date getDeadline() {
        return deadline;
    }
}
