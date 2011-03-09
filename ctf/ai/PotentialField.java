package ctf.ai;

import jade.core.Actor;
import jade.util.Coord;
import util.Vector;

public abstract class PotentialField extends Actor
{
    private int radius;

    public PotentialField(int radius)
    {
        super(null);
        this.radius = radius;
    }

    public PotentialField()
    {
        this(10);
    }

    public int radius()
    {
        return radius;
    }

    @Override
    public void act()
    {
        // noop
    }

    public abstract Vector getInfluence(int x, int y);

    public final Vector getInfluence(Coord pos)
    {
        return getInfluence(pos.x(), pos.y());
    }

    public final Vector getInfluence(Actor actor)
    {
        return getInfluence(actor.x(), actor.y());
    }
}
