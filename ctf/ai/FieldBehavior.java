package ctf.ai;

import jade.core.Actor;
import jade.util.Coord;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import util.Vector;

public class FieldBehavior
{
    private Map<PotentialField, Double> weights;

    public FieldBehavior()
    {
        weights = new HashMap<PotentialField, Double>();
    }

    public void setWeight(PotentialField field, double weight)
    {
        weights.put(field, weight);
    }

    public void setWeights(Set<PotentialField> fields, double weight)
    {
        for(PotentialField field : fields)
            setWeight(field, weight);
    }

    public double getWeight(PotentialField field)
    {
        if(weights.containsKey(field))
            return weights.get(field);
        else
            return 0;
    }

    public Vector getInfluence(Coord pos)
    {
        Vector behavior = new Vector();
        for(PotentialField field : weights.keySet())
        {
            Vector vector = field.getInfluence(pos);
            vector.multiply(weights.get(field));
            behavior.add(vector);
        }
        return behavior;
    }

    public void removeExpired()
    {
        Set<Actor> expired = new HashSet<Actor>();
        for(Actor actor : weights.keySet())
            if(actor.expired())
                expired.add(actor);
        for(Actor actor : expired)
            if(weights.containsKey(actor))
                weights.remove(actor);
    }
}
