package rl.world;

import jade.core.Actor;
import jade.core.World;
import jade.gen.Gen;
import jade.gen.Gen.GenFactory;
import jade.util.ColoredChar;
import rl.creature.Creature;
import rl.item.Item;

public class Level extends World
{
	public Level()
	{
		super(80, 23);
		GenFactory.get(Gen.Cellular).generate(this, 0);
	}

	public void tick()
	{
		for(Actor creature : getActors(Creature.class))
			creature.act();
		for(Actor creature : getActors(Creature.class))
			retrieveMessages(creature);
		removeExpired();
	}

	public ColoredChar look(int x, int y)
	{
		Creature creature = (Creature)getActorAt(x, y, Creature.class);
		if(creature != null)
			return creature.look();
		Item item = (Item)getActorAt(x, y, Item.class);
		if(item != null)
			return item.look();
		return super.look(x, y);
	}
}