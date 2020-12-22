package src.algorithmic;

import java.util.*;

public class Clump
{
    private int layer;
    private Clump parent;
    private int leafCount;
    private Set<Clump> children;
    private Object data;
    private int id;
    public static int initialCapacity;

    public Clump(int layer)
    {
        this.layer = layer;
        parent = null;
        leafCount = 0;
        children = new HashSet<>(layer > 0 ? initialCapacity : 0);
        data = null;
        id = 0;
    }

    public void clear()
    {
        this.layer = 0;
        parent = null;
        leafCount = 0;
        data = null;
        id = 0;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public Clump getParent() {
        return parent;
    }

    public int getLeafCount() {
        return leafCount;
    }

    public void setLeafCount(int leafCount) {
        updateLeafCount(leafCount);
    }

    public Set<Clump> getChildren() {
        return children;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    private void setParent(Clump newParent)
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

    public Clump getLastParent()
    {
        if (parent == null)
        {
            return this;
        }
        return parent.getLastParent();
    }

    public static void merge(Clump c1, Clump c2)
    {
        if (c1.getLastParent() == c2.getLastParent())
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
                Clump p = new Clump(c1.getLayer() + 1);
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
