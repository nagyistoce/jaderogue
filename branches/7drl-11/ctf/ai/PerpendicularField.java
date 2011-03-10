package ctf.ai;

import util.Vector;

public class PerpendicularField extends PotentialField
{
    private boolean vert;
    private int length;

    public PerpendicularField(boolean vert, int length)
    {
        this(vert, length, 3);
    }

    public PerpendicularField(boolean vert, int length, int radius)
    {
        super(radius);
        this.vert = vert;
        this.length = length;
    }

    @Override
    public Vector getInfluence(int x, int y)
    {
        if(vert && (y < y() || y > y() + length))
            return new Vector();
        if(!vert && (x < x() || x > x() + length))
            return new Vector();
        double dist = vert ? x - x() : y - y();
        double sign = Math.signum(dist);
        dist = Math.abs(dist);
        if(dist < 1 || dist > radius())
            return new Vector();
        else if(vert)
            return new Vector(sign * (radius() - dist), 0);
        else
            return new Vector(0, sign * (radius() - dist));
    }
}
