package ctf.ai;

import util.Vector;

public class RepulsiveField extends PotentialField
{
    public RepulsiveField(int radius)
    {
        super(radius);
    }

    public RepulsiveField()
    {
        this(10);
    }

    @Override
    public Vector getInfluence(int x, int y)
    {
        double dist = pos().distCart(x, y);
        if(dist < 1 || dist > radius())
            return new Vector();
        else
        {
            double theta = Math.atan2(y() - y, x() - x);
            Vector influence = new Vector(Math.cos(theta), Math.sin(theta));
            influence.multiply(dist - radius());
            return influence;
        }
    }
}