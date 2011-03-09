package ctf.ai;

import util.Vector;

public class AttractiveField extends PotentialField
{
    public AttractiveField(int radius)
    {
        super(radius);
    }

    public AttractiveField()
    {
        this(10);
    }

    @Override
    public Vector getInfluence(int x, int y)
    {
        double dist = pos().distCart(x, y);
        if(dist < 1)
            return new Vector();
        double theta = Math.atan2(y() - y, x() - x);
        Vector influence = new Vector(Math.cos(theta), Math.sin(theta));
        influence.multiply(Math.min(dist, radius()));
        return influence;
    }
}
