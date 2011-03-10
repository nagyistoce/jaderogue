package ctf.ai;

import jade.util.Direction;
import util.Vector;

public class UniformField extends PotentialField
{
    private Direction direction;

    public UniformField(Direction direction)
    {
        this(direction, 20);
    }

    public UniformField(Direction direction, int radius)
    {
        super(radius);
        this.direction = direction;
    }

    @Override
    public Vector getInfluence(int x, int y)
    {
        double dist = pos().distCart(x, y);
        return dist < radius() ? direction.vector() : new Vector();
    }
}
