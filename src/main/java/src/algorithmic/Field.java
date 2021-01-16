package src.algorithmic;

public class Field extends Connector
{
    private int x;
    private int y;
    private int connections;

    public Field(int layer, int connections, int x, int y)
    {
        super(layer);
        this.connections = connections;
        this.x = x;
        this.y = y;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getConnections()
    {
        return connections;
    }

    public Connector getNConnectedArea(int n)
    {
        Connector node = this;
        for (int i = 0; i < 4 - n; ++i)
        {
            if (node == null)
            {
                // This field is not n-connected
                return null;
            }
            node = node.getHead();
        }
        return node;
    }
}
