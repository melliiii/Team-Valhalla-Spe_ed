package src.algorithmic;

import java.util.HashSet;
import java.util.Set;

public class Connector
{
    private int layer;
    private Connector parent;
    private int leafCount;
    private Set<Connector> children;
    public static int initialCapacity;

    public Connector(int layer)
    {
        this.layer = layer;
        parent = null;
        leafCount = 0;
        children = new HashSet<>(layer > 0 ? initialCapacity : 0);
    }

    public void clear()
    {
        this.layer = 0;
        parent = null;
        leafCount = 0;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public Connector getParent() {
        return parent;
    }

    public int getLeafCount() {
        return leafCount;
    }

    public void setLeafCount(int leafCount) {
        updateLeafCount(leafCount);
    }

    public Set<Connector> getChildren() {
        return children;
    }

    public void setSealed()
    {
        if (parent != null)
        {
            parent.getHead().setSealed();
            return;
        }
        layer = 0;
    }

    public boolean isSealed()
    {
        return layer == 0 && children.size() > 0;
    }

    private void updateLeafCount(int value)
    {
        int change = value - leafCount;
        if (parent != null)
        {
            parent.setLeafCount(parent.getLeafCount() + change);
        }

        leafCount = value;
    }

    private void setParent(Connector newParent)
    {
        if (parent != null)
        {
            parent.getChildren().remove(this);
            parent.setLeafCount(parent.getLeafCount() - leafCount);
            parent = null;
        }

        if (newParent == null)
        {
            return;
        }

        parent = newParent;

        if (!parent.getChildren().contains(this))
        {
            parent.getChildren().add(this);
            parent.setLeafCount(parent.getLeafCount() + leafCount);
        }
    }

    public Connector getHead()
    {
        if (parent == null)
        {
            return this;
        }
        if (parent.isSealed())
        {
            return parent;
        }
        return parent.getHead();
    }

    public Connector getLastHead()
    {
        if (parent == null)
        {
            return this;
        }
        Connector head = getHead();
        if(head.isSealed())
        {
            return head.getLastHead();
        }
        return head;
    }

    public static void merge(Connector c1, Connector c2)
    {
        if (c1.getHead() == c2.getHead())
        {
            return;
        }

        if (c1.getLayer() > c2.getLayer())
        {
            merge(c1.getChildren().iterator().next(), c2);
            return;
        }

        if (c2.getLayer() > c1.getLayer())
        {
            merge(c2.getChildren().iterator().next(), c1);
            return;
        }

        if (c1.getParent() == null)
        {
            if (c2.getParent() == null)
            {
                Connector p = new Connector(c1.getLayer() + 1);
                c1.setParent(p);
                c2.setParent(p);
            }
            else
            {
                c1.setParent(c2.getParent());
            }
        }
        else
        {
            if (c2.getParent() == null)
            {
                c2.setParent(c1.getParent());
            }
            else
            {
                merge(c1.getParent(), c2.getParent());
            }
        }
    }
}
