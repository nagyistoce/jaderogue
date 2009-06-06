package rl.world;

import jade.core.Actor;
import java.awt.Color;
import rl.creature.Creature;

public class Feature extends Actor
{
	public Feature(char face, Color color)
	{
		super(face, color);
	}
	
	public void act()
	{
		Actor target = world().getActorAt(x(), y(), Creature.class);
		if(target != null)
		{
			target.expire();
			appendMessage(target + " dies in a trap");
		}
	}
}
