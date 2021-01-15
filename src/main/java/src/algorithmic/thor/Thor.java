package src.algorithmic.thor;

import src.algorithmic.AreaFinder;
import src.algorithmic.MCTSNode;
import src.algorithmic.Odin;
import src.algorithmic.VariantTracker;
import src.game.Game;
import src.game.GameMove;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Thor extends Odin
{
    private RandomMethod randomMethod = RandomMethod.equal;
    private boolean memorizeTree = false;
    private MCTSNode root;
    private VariantTracker tracker = null;
    private int iterations = 100;
    protected int batchSize = 10;
    private Game start;

    public Thor(Game game, int playerId)
    {
        super(game, playerId);

        GameMove[] options = getOptions();
        root = MCTSNode.createTree(playerId, playerId, options);
        root.expand(options, playerId);

        setDepth(3);
        setEvalMethod(EvaluationMethod.area);
        setIterations(1000);
        setBatchSize(30);
        setExploration(Math.sqrt(2.0));
        setMemorizeTree(true);
        setRandomMethod(RandomMethod.equal);
    }

    // Plays random but avoids deadly moves
    public GameMove enemyAI(Game game, int playerId)
    {
        return randomMove(game, playerId, randomMethod);
    }

    public double playRandomGames(Game game, int playerId, int count)
    {
        double score = 0.0;
        for (int i = 0; i < count; ++i)
        {
            Game g = game.cloneGame();
            score += playRandomGame(g, playerId, randomMethod);
        }

        return score / count;
    }

    @Override
    public double evaluate(Game current, int playerId)
    {
        if (playerId != this.playerId)
        {
            return 0;
        }
        if (evalMethod == EvaluationMethod.monte_carlo)
        {
            return playRandomGames(current, playerId, batchSize);
        }

        return super.evaluate(current, playerId);
    }

    void treeSearchIteration()
    {
        double c = getExploration();
        GameMove[] options = getOptions();

        // Select a node
        MCTSNode next = root.selectNode(c);

        int enemyAreaBefore = 0;

        List<GameMove> path = next.getPath();
        Game g = start.cloneGame();
        for (int step = 0; step < path.size(); ++step)
        {
            GameMove m = path.get(step);

            // Move next players randomly
            for (int p = 1; p < g.getPlayerCount(); ++p)
            {
                int pid = (p + playerId) % g.getPlayerCount();
                GameMove otherMove = enemyAI(g, pid);
                g.performMove(otherMove);
            }

            if (step == path.size()-1)
            {
                AreaFinder finder = new AreaFinder(g);
                finder.findAreas();
                for (int p = 1; p < g.getPlayerCount(); ++p)
                {
                    int pid = (p + playerId) % g.getPlayerCount();
                    enemyAreaBefore += finder.getMaxAreaAround(pid);
                }
            }

            g.performMove(m);
        }

        int enemyAreaAfter = 0;
        AreaFinder finder = new AreaFinder(g);
        finder.findAreas();
        for (int p = 1; p < g.getPlayerCount(); ++p)
        {
            int pid = (p + playerId) % g.getPlayerCount();
            enemyAreaAfter += finder.getMaxAreaAround(pid);
        }

        double enemyAreaTaken = (double) enemyAreaBefore / (enemyAreaAfter+1);

        // If set, allow variant tracking
        if (tracker != null)
        {
            tracker.addVariant(g);
        }

        // Evaluate player scores
        double[] scores = new double[g.getPlayerCount()];
        for (int s = 0; s < g.getPlayerCount(); ++s)
        {
            scores[s] = 0;
            if (s == playerId)
            {
                scores[s] = evaluate(g, s) * (Math.pow(enemyAreaTaken, 2.0) + 0.1);
            }
        }

        // Add scores to tree
        next.addSimulation(scores);
        if (next.isLeaf())
        {
            next.expand(options, playerId);
        }
    }

    public void beginTurn()
    {
        GameMove[] options = getOptions();

        start = game.cloneGame();
        start.setFirstPlayer((playerId+1) % start.getPlayerCount());

        if (!memorizeTree || root.getChildCount() == 0)
        {
            root = MCTSNode.createTree(playerId, playerId, options);
            root.expand(options, playerId);
        }
    }

    public GameMove endTurn()
    {
        GameMove result = GameMove.change_nothing;
        MCTSNode nextRoot = root.getChild(GameMove.change_nothing);
        double max = 0;
        for (GameMove move : getOptions())
        {
            if (root.getChild(move) != null)
            {
                double eval = root.getChild(move).evaluate();
                if (eval > max)
                {
                    max = eval;
                    nextRoot = root.getChild(move);
                    result = root.getChild(move).getMove();
                }
            }
        }

        //System.out.println("");
        //System.out.println("Score:" + max);
        //System.out.println("Depth:" + root.getDepth());
        root = nextRoot;
        root.setRoot();

        return result;
    }

    public GameMove treeSearch(double c, int iterations)
    {
        beginTurn();

        for (int i = 0; i < iterations; ++i)
        {
            treeSearchIteration();
        }

        return endTurn();
    }

    @Override
    public GameMove decide()
    {
        return treeSearch(getExploration(), iterations);
    }

    public void setRandomMethod(RandomMethod randomMethod)
    {
        this.randomMethod = randomMethod;
    }

    public void setIterations(int iterations)
    {
        this.iterations = iterations;
    }

    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }

    public void setMemorizeTree(boolean memorizeTree)
    {
        this.memorizeTree = memorizeTree;
    }

    public VariantTracker getTracker()
    {
        return tracker;
    }

    public void setTracker(VariantTracker tracker)
    {
        this.tracker = tracker;
    }
}
