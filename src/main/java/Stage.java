import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Stage extends JPanel implements KeyListener
{
    public static Random random = new Random();
    public Game game;

    public static GameMove testAI(Game game, Player self)
    {
        if(!self.isActive())
        {
            return GameMove.change_nothing;
        }
        GameMove result = GameMove.change_nothing;
        boolean change_dir = false;

        // Randomly changes speed
        Random rand = random;
        if (rand.nextInt(100) > 50)
        {
            if (rand.nextBoolean())
            {
                if (self.getSpeed() < 10)
                {
                    result = GameMove.speed_up;
                }
            }
            else
            {
                if (self.getSpeed() > 1)
                {
                    result = GameMove.slow_down;
                }
            }
        }
        else if (rand.nextInt(100) > 80)
        {
            change_dir = true;
        }

        // Randomly changes direction if obstacle near
        if (self.getDirection() == Direction.right &&
                self.getX() + self.getSpeed() >= game.getWidth())
        {
            change_dir = true;
        }
        else if (self.getDirection() == Direction.down &&
                self.getY() + self.getSpeed() >= game.getHeight())
        {
            change_dir = true;
        }
        else if (self.getDirection() == Direction.left &&
                self.getX() - self.getSpeed() < 0)
        {
            change_dir = true;
        }
        else if (self.getDirection() == Direction.up &&
                self.getY() - self.getSpeed() < 0)
        {
            change_dir = true;
        }
        else
        {
            int[] delta = Game.direction2Delta(self.getDirection());
            for (int i = 1; i <= self.getSpeed(); ++i)
            {
                if (game.getCells()[self.getY() + i * delta[1]][self.getX() + i * delta[0]] != 0)
                {
                    change_dir = true;
                    break;
                }
            }
        }

        if (change_dir)
        {
            if(rand.nextBoolean())
            {
                result = GameMove.turn_left;
            }
            else
            {
                result = GameMove.turn_right;
            }
        }

        return result;
    }

    public Stage()
    {
        super();
        addKeyListener(this);
        setFocusable(true);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        int cellWidth = getHeight() / game.getCells().length;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        for (int y = 0; y < game.getCells().length; ++y)
        {
            for (int x = 0; x < game.getCells()[y].length; ++x)
            {
                g.setColor(cell2Color(game.getCells()[y][x]));
                int cellX = x * cellWidth + centerX - game.getCells()[y].length * cellWidth / 2;
                int cellY = y * cellWidth + centerY - game.getCells().length * cellWidth / 2;
                g.fillRect(cellX, cellY, cellWidth, cellWidth);
            }
        }
    }

    public static Color cell2Color(int cell)
    {
        Color result = Color.BLACK;
        switch (cell)
        {
            case 1:
                result =  Color.green;
                break;
            case 2:
                result =  Color.blue;
                break;
            case 3:
                result =  Color.red;
                break;
            case 4:
                result =  Color.yellow;
                break;
            case 5:
                result =  Color.cyan;
                break;
            case 6:
                result =  Color.magenta;
                break;
            case -1:
                result =  Color.white;
                break;
        }

        return result;
    }

    public static void loop() {
        Stage stage = new Stage();
        List<String> names = new ArrayList<String>();
        names.add("Frodo");
        names.add("Gandalf");
        names.add("Sam");
        names.add("Bilbo");
        names.add("Aragorn");
        names.add("Sauron");

        Game game = Game.create(50, 50, names);
        stage.game = game;

        JFrame frame = new JFrame("gui");
        frame.add(stage);
        frame.setVisible(true);
        frame.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Stage!");
        for (int t = 0; t < 1000; ++t) {
            //scanner.nextLine();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<GameMove> moves = new ArrayList<GameMove>();
            for (int i = 0; i < names.size(); ++i) {
                GameMove move = testAI(game, game.getPlayer(i));
                moves.add(move);
            }
            game.tick(moves);
            System.out.println(game.getState().getMap());

            for (int i = 0; i < names.size(); ++i) {
                if (game.getPlayer(i).isActive()) {
                    System.out.print(" " + String.valueOf(i + 1));
                }
            }

            stage.game = game;
            stage.repaint();
        }
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }
}