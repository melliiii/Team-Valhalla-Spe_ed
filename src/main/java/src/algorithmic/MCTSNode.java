package src.algorithmic;

import src.game.Game;
import src.game.GameMove;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MCTSNode
{
    private int n = 0;
    private double v;
    private MCTSNode parent;
    private MCTSNode[] children = null;
    private GameMove move;

    public int getPlayerId()
    {
        return playerId;
    }

    private int playerId;
    private int depth = 0;
    private boolean evaluated = false;

    public MCTSNode(MCTSNode parent, GameMove move, int playerId)
    {
        this.parent = parent;
        this.move = move;
        this.playerId = playerId;
    }

    public static MCTSNode createTree(int playerId, int nextPlayer, GameMove[] options)
    {
        MCTSNode result = new MCTSNode(null, GameMove.change_nothing, playerId);
        result.expand(options, nextPlayer);
        return result;
    }

    public double getConfidence(double c)
    {
        if (n == 0)
        {
            return 100000000;
        }
        if (v == 0)
        {
            return 0;
        }
        return v / n + c * Math.sqrt(Math.log(parent.n) / n);
    }

    public void expand(GameMove[] options, int nextPlayer)
    {
        if (!evaluated && !isLeaf())
        {
            return;
        }
        updateDepth(1);
        children = new MCTSNode[options.length];
        for (int i = 0; i < options.length; ++i)
        {
            children[i] = new MCTSNode(this, options[i], nextPlayer);
        }
    }

    public void addSimulation(double[] wins)
    {
        evaluated = true;
        this.n++;

        if (parent != null)
        {
            this.v += wins[getMovedPlayer()];
            parent.addSimulation(wins);
        }
    }

    public MCTSNode selectNode(double c)
    {
        if (children == null || children.length == 0 || !evaluated)
        {
            return this;
        }
        double max = 0;
        MCTSNode next = children[0];
        for (int i = 0; i < children.length; ++i)
        {
            double confidence = children[i].getConfidence(c);
            if (confidence > max)
            {
                max = confidence;
                next = children[i];
            }
        }
        return next.selectNode(c);
    }

    public GameMove getMove()
    {
        return move;
    }

    public boolean isLeaf()
    {
        return children == null;
    }

    public double evaluate()
    {
        if (n == 0)
        {
            return 0;
        }
        return v / n;
    }

    public MCTSNode getChild(int index)
    {
        return children[index];
    }

    public List<GameMove> getPath()
    {
        if (parent == null)
        {
            return new LinkedList<>();
        }
        List<GameMove> result = parent.getPath();
        result.add(move);
        return result;
    }

    public int getMovedPlayer()
    {
        return parent.playerId;
    }

    public void updateDepth(int childDepth)
    {
        this.depth = Math.max(this.depth, childDepth + 1);
        if (parent != null)
        {
            parent.updateDepth(this.depth);
        }
    }

    public int getDepth()
    {
        return depth;
    }

    public int getChildCount()
    {
        return children.length;
    }

    public void resetN()
    {
        if (n == 0)
        {
            return;
        }
        v /= n;
        n = 1;
        evaluated = false;
        if (!isLeaf())
        {
            for (int i = 0; i < children.length; ++i)
            {
                children[i].resetN();
            }
        }
    }

    public void setRoot()
    {
        parent = null;
        resetN();
    }
}