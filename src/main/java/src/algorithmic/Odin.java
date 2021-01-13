package src.algorithmic;

import src.game.Direction;
import src.game.Game;
import src.game.GameMove;
import src.game.Player;

import java.util.*;

public class Odin extends AlgorithmicAI
{
    private EvaluationMethod evalMethod = EvaluationMethod.area_div_enemies;
    private SearchMethod searchMethod = SearchMethod.max;
    private int iterations = 100;
    private int batchSize = 10;
    private int depth = 2;
    private double exploration = Math.sqrt(2);
    private boolean useC = false;
    private RandomMethod randomMethod = RandomMethod.equal;
    private boolean memorizeTree = false;
    private MCTSNode root;
    private VariantTracker tracker = null;

    public void setUsingC()
    {
        useC = true;
    }

    public void setIterations(int iterations)
    {
        this.iterations = iterations;
    }

    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }

    public void setSearchMethod(SearchMethod searchMethod)
    {
        this.searchMethod = searchMethod;
    }

    public void setEvalMethod(EvaluationMethod evalMethod)
    {
        this.evalMethod = evalMethod;
    }

    public int getDepth()
    {
        return depth;
    }

    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    public double getExploration()
    {
        return exploration;
    }

    public void setExploration(double exploration)
    {
        this.exploration = exploration;
    }

    public void setMemorizeTree(boolean memorizeTree)
    {
        this.memorizeTree = memorizeTree;
    }

    public RandomMethod getRandomMethod()
    {
        return randomMethod;
    }

    public void setRandomMethod(RandomMethod randomMethod)
    {
        this.randomMethod = randomMethod;
    }

    public VariantTracker getTracker()
    {
        return tracker;
    }

    public void setTracker(VariantTracker tracker)
    {
        this.tracker = tracker;
    }

    public enum SearchMethod
    {
        max,
        monte_carlo_tree
    }

    public enum EvaluationMethod
    {
        area,
        area_div_enemies,
        monte_carlo,
        area_monte_carlo
    }

    public Odin(Game game, int playerId)
    {
        super(game, playerId);
        GameMove[] options = getOptions();
        root = MCTSNode.createTree(playerId, playerId, options);
        root.expand(options, playerId);
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

    public double evaluate(Game current, int playerId)
    {
        if (evalMethod == EvaluationMethod.monte_carlo)
        {
            if (this.playerId != playerId)
            {
                return 0;
            }
            return playRandomGames(current, playerId, batchSize);
        }

        Player player = current.getPlayer(playerId);
        if (!player.isActive())
        {
            return 0;
        }
        finder.setGame(current);
        finder.findAreas(useC);
        HashSet<Clump> areas = new HashSet<>();
        int enemySum = 0;

        double score = 0;

        for (int p = 0; p < current.getPlayerCount(); ++p)
        {
            Player pl = current.getPlayer(p);
            if (!pl.isActive())
            {
                continue;
            }
            int max = 0;
            for (int i = 0; i < 4; ++i)
            {
                Direction dir = Game.int2Direction(i);
                int[] dxy = Game.direction2Delta(dir);
                int x = pl.getX() + dxy[0];
                int y = pl.getY() + dxy[1];
                if (current.positionExists(x, y))
                {
                    int eval = finder.getAreaAt(x, y);
                    if (p != playerId)
                    {
                        enemySum += eval;
                    }
                    areas.add(finder.getClumpAt(x, y));
                    max = Math.max(max, eval);
                }
            }

            if (p == playerId)
            {
                score += max;
            }
        }

        double allAreas = 0;
        for (Clump area : areas)
        {
            if (area != null)
            {
                allAreas += area.getLeafCount();
            }
        }

        if (evalMethod == EvaluationMethod.area_div_enemies)
        {
            //int alivePlayers = 6 - current.getDeaths();
            score /= enemySum + 1.0; //((double) enemySum / alivePlayers) + 1.0;
            //if (score > 10.0)
            //{
            //    score = 10.0 + Math.log(score);
            //}
            return score;
        }
        if (evalMethod == EvaluationMethod.area_monte_carlo)
        {
            score /= enemySum + 1.0;
            double monte_carlo_score = playRandomGames(current, playerId, batchSize);
            score = Math.max(score, monte_carlo_score);
            //score = 0.75 * score + 0.25 * monte_carlo_score;
            return score;
        }
        return score / allAreas;
    }

    public GameMove MonteCarloTreeSearch(double c, int iterations)
    {
        GameMove[] options = getOptions();

        Game start = this.game.cloneGame();
        start.setFirstPlayer((playerId+1) % start.getPlayerCount());

        if (!memorizeTree || root.getChildCount() == 0)
        {
            root = MCTSNode.createTree(playerId, playerId, options);
            root.expand(options, playerId);
        }

        for (int i = 0; i < iterations; ++i)
        {
            // Select a node
            MCTSNode next = root.selectNode(c);

            List<GameMove> path = next.getPath();
            Game g = start.cloneGame();
            for (GameMove m : path)
            {
                // Move next players randomly
                for (int p = 1; p < g.getPlayerCount(); ++p)
                {
                    int pid = (p + playerId) % g.getPlayerCount();
                    GameMove otherMove = randomMove(g, pid, randomMethod);
                    g.performMove(otherMove);
                }
                g.performMove(m);
            }

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
                    scores[s] = evaluate(g, s);
                }
            }

            // Add scores to tree
            next.addSimulation(scores);
            if (next.isLeaf())
            {
                List<GameMove> opt = new LinkedList<>();
                for (int o = 0; o < options.length; ++o)
                {
                    //if (!g.isDeadlyMove(next.getPlayerId(), options[o]))
                    {
                        opt.add(options[o]);
                    }
                }
                //if (opt.isEmpty())
                //{
                //    opt.add(GameMove.change_nothing);
                //}
                //if (!opt.isEmpty())
                {
                    next.expand(opt.toArray(new GameMove[0]), playerId);
                }
            }
        }

        GameMove result = GameMove.change_nothing;

        MCTSNode nextRoot = root.getChild(0);
        double max = 0;
        for (int i = 0; i < root.getChildCount(); ++i)
        {
            double eval = root.getChild(i).evaluate();
            if (eval > max)
            {
                max = eval;
                nextRoot = root.getChild(i);
                result = root.getChild(i).getMove();
            }
        }

        //System.out.println("");
        //System.out.println("Score:" + max);
        //System.out.println("Depth:" + root.getDepth());
        root = nextRoot;
        root.setRoot();

        return result;
    }

    public Number[] max(Game current, int playerId, double currentBest, int depth)
    {
        double max = currentBest;
        int best = 0;
        GameMove[] options = getOptions();
        GameMove[] moves = new GameMove[current.getPlayerCount()];

        for (int p = 0; p < current.getPlayerCount(); ++p)
        {
            moves[p] = GameMove.change_nothing;
        }

        for (int m = 0; m < options.length; ++m)
        {
            GameMove move = options[m];
            moves[playerId] = move;
            Game next = null;
            try
            {
                next = current.variant(Arrays.asList(moves.clone()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            double eval = evaluate(next, playerId);

            if (eval > 0 && eval > max * (1.0 / exploration) && depth > 0)
            {
                eval = max(next, playerId, max, depth-1)[0].doubleValue();
            }
            if (eval > max)
            {
                max = eval;
                best = m;
            }
        }

        return new Number[]{max, best};
    }

    @Override
    public GameMove decide()
    {
        if (searchMethod == SearchMethod.monte_carlo_tree)
        {
            return MonteCarloTreeSearch(exploration, iterations);
        }
        else if (searchMethod == SearchMethod.max)
        {
            Number[] result = max(game, playerId,0, depth);
            int index = Math.round(result[1].intValue());
            return getOptions()[index];
        }
        return GameMove.change_nothing;
    }
}
