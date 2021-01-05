package src.threads;

import src.algorithmic.*;
import src.game.Direction;
import src.game.Game;
import src.game.GameMove;
import src.game.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("SuspiciousNameCombination")
public class Stage extends JPanel implements KeyListener
{
    public static final Random random = new Random();
    public Game game;
    public AreaFinder finder;

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

    public Stage(String title, boolean minimized)
    {
        super();
        addKeyListener(this);
        setFocusable(true);

        List<String> names = new ArrayList<>();
        names.add("Frodo");
        names.add("Gandalf");
        names.add("Sam");
        names.add("Bilbo");
        names.add("Aragorn");
        names.add("Sauron");

        Game game = Game.create(80, 80, names);
        this.game = game;
        finder = new AreaFinder(game);

        JFrame frame = new JFrame(title);
        frame.add(this);
        if (minimized) frame.setState(Frame.ICONIFIED);
        frame.setSize(1200, 700);
        frame.setVisible(true);
        setBackground(Color.black);
    }
    public Stage(boolean minimized)
    {
        super();
        addKeyListener(this);
        setFocusable(true);

        List<String> names = new ArrayList<>();
        names.add("Frodo");
        names.add("Gandalf");
        names.add("Sam");
        names.add("Bilbo");
        names.add("Aragorn");
        names.add("Sauron");

        this.game = Game.create(50, 50, names);
        finder = new AreaFinder(game);

        JFrame frame = new JFrame("src/gui");
        frame.add(this);
        if (minimized) frame.setState(Frame.ICONIFIED);
        frame.setSize(1200, 700);
        setBackground(Color.black);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public void paintComponent(Graphics g)
    {
        if (finder != null)
        {
            finder.findAreas();
        }
        super.paintComponent(g);

        int cellWidth = getHeight() / game.getCells().length;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        for (int y = 0; y < game.getCells().length; ++y)
        {
            for (int x = 0; x < game.getCells()[y].length; ++x)
            {
                g.setColor(cell2Color(game.getCells()[y][x]));
                if (finder != null)
                {
                    if (finder.getCollected()[y][x] != null)
                    {
                        float scale = (float)finder.getAreaAt(x, y) / (float)(game.getWidth() * game.getHeight());
                        scale = (float)Math.sqrt(Math.sqrt(scale));
                        g.setColor(new Color(1.0f-scale, scale, 0.0f));
                    }
                }

                int cellX = x * cellWidth + centerX - game.getCells()[y].length * cellWidth / 2;
                int cellY = y * cellWidth + centerY - game.getCells().length * cellWidth / 2;
                g.fillRect(cellX, cellY, cellWidth-1, cellWidth-1);
            }
        }
    }

    public Color cell2Color(int cell)
    {
        Color result;
        switch (cell)
        {
            case 1:
                result = Color.blue;
                break;
            case 2:
                result =  Color.cyan;
                break;
            case 3:
                result =  Color.green;
                break;
            case 4:
                result =  Color.yellow;
                break;
            case 5:
                result =  Color.orange;
                break;
            case 6:
                result = Color.red;
                break;
            case 0:
                result =  Color.black;
                break;
            case -1:
                result =  Color.white;
                break;
            default:
                Random r = new Random(cell);
                result =  new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
                break;
        }

        if (cell == game.getYou())
        {
            result =  Color.white;// Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }

        return result;
    }

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
        finder = new AreaFinder(game);
    }

    public void loop()
    {
        AreaFinder shared = new AreaFinder(game);
        AlgorithmicAI[] ais = new AlgorithmicAI[game.getPlayerCount()];
        for (int i = 0; i < game.getPlayerCount(); ++i)
        {
            if (false && i > 0)
            {
                ais[i] = new Odin(game, i);
            }
            else
            {
                ais[i] = new Thor(game, i);
                //ais[i] = new Siegfried(game, i);
                //ais[i].setFinder(shared);
            }
        }

        while (game.isRunning()){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            shared.findAreas();

            List<GameMove> moves = new ArrayList<GameMove>();
            for (int i = 0; i < game.getPlayerCount(); ++i)
            {
                GameMove move = ais[i].decide(i);
                moves.add(move);
            }
            game.tick(moves);
            this.repaint();
        }
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }
}