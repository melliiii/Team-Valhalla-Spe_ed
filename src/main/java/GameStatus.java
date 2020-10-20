import java.util.Date;

public class GameStatus {
    private int width = 40;
    private int height = 40;
    private int[][] cells;

    private Player[] players;

    private int you = 0;
    private boolean runnning = false;
    private Date deadline;


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
