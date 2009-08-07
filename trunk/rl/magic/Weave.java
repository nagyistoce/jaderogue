package rl.magic;

import jade.core.Actor;
import java.io.Serializable;
import rl.magic.Instant.Effect;

public class Weave extends Actor implements Serializable
{
	private final Instant instant;
	private int duration;

	public Weave(Effect effect, int magnitude, int duration)
	{
		super('*', effect.color());
		instant = new Instant(effect, magnitude);
		this.duration = duration;
	}

	@Override
	public void act()
	{
		instant.doIt(x(), y(), world());
		if (duration == 0)
			expire();
		duration--;
	}

	@Override
	public void expire()
	{
		instant.undoIt();
		super.expire();
	}
}
