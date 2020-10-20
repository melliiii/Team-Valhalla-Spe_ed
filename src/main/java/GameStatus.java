import java.util.Date;
import java.util.Map;

public class GameStatus {
    public int width = 40;
    public int height = 40;
    public int[][] cells;

    public Map<String, Player> players;

    public int you = 0;
    public boolean running = false;
    public Date deadline;


    public String getMap(){
        String map = "";
        for (int[] row : cells) {
            for (int i : row) {
                map += i + " ";
            }
            map += "\n";
        }
        return map;
    }

    public Date getDeadline() {
        return deadline;
    }
}
