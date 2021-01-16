package src.threads;

import src.algorithmic.AlgorithmicAI;
import src.algorithmic.AreaFinder;
import src.algorithmic.Odin;
import src.algorithmic.VariantTracker;
import src.algorithmic.thor.Thor;
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

public class Stage extends JPanel implements KeyListener
{
    public static final Random random = new Random();
    public Game game;
    public AreaFinder finder;
    public Direction nextHumanMove = null;
    public boolean humanPilot = false;
    private VariantTracker visualizeVariants;


    public Stage(String title, boolean minimized, boolean gui)
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
        //finder = new AreaFinder(game);

        if(gui){
            JFrame frame = new JFrame(title);
            frame.add(this);
            if (minimized)
            {
                frame.setState(Frame.ICONIFIED);
            }
            frame.setSize(500, 500);
            frame.setVisible(true);
            setBackground(Color.darkGray);
        }

    }

    public Stage(boolean minimized)
    {
        super();
        addKeyListener(this);
        setFocusable(true);

        List<String> names = new ArrayList<>();
        names.add("Sauron");
        names.add("Frodo");
        names.add("Gandalf");
        names.add("Sam");
        names.add("Bilbo");
        names.add("Aragorn");

        this.game = Game.create(50, 50, names);
        //finder = new AreaFinder(game);

        JFrame frame = new JFrame("src/gui");
        frame.add(this);
        if (minimized)
        {
            frame.setState(Frame.ICONIFIED);
        }
        frame.setSize(500, 500);
        setBackground(Color.darkGray);
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
                        float scale = (float) finder.getAreaAt(x, y) / (float) (game.getWidth() * game.getHeight());
                        scale = (float) Math.sqrt(Math.sqrt(scale));
                        g.setColor(new Color(1.0f - scale, scale, 0.0f));
                    }
                }
                if (visualizeVariants != null && game.getCells()[y][x] == 0)
                {
                    double scale = Math.sqrt(Math.sqrt(visualizeVariants.getAvgVisits(x, y)));
                    double enemyScale = Math.sqrt(Math.sqrt(visualizeVariants.getAvgEnemyVisits(x, y)));
                    g.setColor(new Color((float)enemyScale / 2, (float) scale, 0));
                }

                int cellX = x * cellWidth + centerX - game.getCells()[y].length * cellWidth / 2;
                int cellY = y * cellWidth + centerY - game.getCells().length * cellWidth / 2;
                g.fillRect(cellX, cellY, cellWidth - 1, cellWidth - 1);
            }
        }
    }

    public Color cell2Color(int cell)
    {
        Color result = Color.black;
        switch (cell)
        {
            case 1:
                result = Color.blue;
                break;
            case 2:
                result = Color.cyan;
                break;
            case 3:
                result = Color.magenta;
                break;
            case 4:
                result = Color.yellow;
                break;
            case 5:
                result = Color.orange;
                break;
            case 6:
                result = Color.red;
                break;
            case 0:
                result = Color.black;
                break;
            case -1:
                result = Color.white;
                break;
        }

        if (cell == game.getYou())
        {
            result = Color.white;// Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            if (humanPilot)
            {
                Random r = random;
                result = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
            }
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
        //finder = new AreaFinder(game);
    }

    public void loop()
    {
        AlgorithmicAI[] ais = new AlgorithmicAI[game.getPlayerCount()];
        for (int i = 0; i < game.getPlayerCount(); ++i)
        {
            if (i == 0)
            {
                Thor a = new Thor(game, i);
                visualizeVariants = new VariantTracker(game, i);
                a.setTracker(visualizeVariants);
                a.setIterations(1000);
                a.setMemorizeTree(true);
                ais[i] = a;
            }
            else
            {
                Odin odin = new Odin(game, i);
                odin.setDepth(2);
                ais[i] = odin;
            }
        }

        VariantTracker nextTracker = null;
        while (game.isRunning())
        {
            List<GameMove> moves = new ArrayList<>();
            for (int i = 0; i < game.getPlayerCount(); ++i)
            {
                GameMove move;
                if (i == 0)
                {
                    Thor a = (Thor) ais[i];
                    a.setTracker(new VariantTracker(game, i));
                    nextTracker = a.getTracker();
                }

                if (i == 0 && humanPilot)
                {
                    move = humanPlayer(game.getPlayer(i));
                }
                else
                {
                    move = ais[i].decide();
                }
                moves.add(move);
            }
            visualizeVariants = nextTracker;

            this.repaint();
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            game.tick(moves);
        }
    }

    public GameMove humanPlayer(Player p)
    {
        if (nextHumanMove == null)
        {
            return GameMove.change_nothing;
        }
        int pdir = Game.direction2Int(p.getDirection());
        int hdir = Game.direction2Int(nextHumanMove);
        int dif = (4 + pdir - hdir) % 4;
        GameMove result = dif == 0 ? GameMove.speed_up :
                dif == 2 ? GameMove.slow_down :
                        dif == 1 ? GameMove.turn_right : GameMove.turn_left;

        nextHumanMove = null;
        return result;
    }

    public void keyTyped(KeyEvent e)
    {

    }

    public void keyPressed(KeyEvent e)
    {
        nextHumanMove = e.getKeyCode() == KeyEvent.VK_UP ? Direction.up :
                e.getKeyCode() == KeyEvent.VK_DOWN ? Direction.down :
                        e.getKeyCode() == KeyEvent.VK_LEFT ? Direction.left :
                                e.getKeyCode() == KeyEvent.VK_RIGHT ? Direction.right : null;
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            humanPilot = !humanPilot;
        }
    }

    public void keyReleased(KeyEvent e)
    {

    }

    public VariantTracker getVisualizeVariants()
    {
        return visualizeVariants;
    }

    public void setVisualizeVariants(VariantTracker visualizeVariants)
    {
        this.visualizeVariants = visualizeVariants;
    }
}