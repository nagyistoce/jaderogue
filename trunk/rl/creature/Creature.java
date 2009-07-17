package rl.creature;

import jade.core.Actor;
import jade.util.Coord;
import java.awt.Color;
import java.io.Serializable;

public abstract class Creature extends Actor implements Serializable
{
	protected Stat hp;
	protected Stat mp;
	protected Stat atk;
	protected Stat rfire;
	protected Stat relec;

	public Creature(char face, Color color, int hp, int mp, int str, int rfire,
			int relec)
	{
		super(face, color);
		this.hp = new Stat(hp);
		this.mp = new Stat(mp);
		this.atk = new Stat(str);
		this.rfire = new Stat(rfire);
		this.relec = new Stat(relec);
	}

	@Override
	public void move(int dx, int dy)
	{
		int x = x() + dx;
		int y = y() + dy;
		Creature bumped = world().getActorAt(x, y, Creature.class);
		if(bumped != null && bumped != this)
			attack(bumped);
		else if(world().passable(x() + dx, y() + dy))
			super.move(dx, dy);
	}

	public abstract Coord getTarget();

	private void attack(Creature bump)
	{
		bump.hpHurt(atk.value);
		if(bump.isExpired())
			appendMessage(this + " slays " + bump);
		else
			appendMessage(this + " hits " + bump);
	}

	public void hpHurt(int damage)
	{
		hp.value -= damage;
		if(hp.value < 0)
			expire();
	}

	public void hpHeal(int cure)
	{
		hp.value = Math.min(hp.value + cure, hp.base);
	}

	public int hp()
	{
		return hp.value;
	}

	public void mpFlow(int flow)
	{
		mp.value += flow;
	}

	public void mpRestore(int restore)
	{
		if(mp.value < mp.base)
			mp.value += restore;
	}

	public int mp()
	{
		return mp.value;
	}

	public int rfire()
	{
		return rfire.value;
	}

	public void rfireBuff(int buff)
	{
		rfire.value += buff;
	}

	public int relec()
	{
		return relec.value;
	}

	public void relecBuff(int buff)
	{
		relec.value += buff;
	}

	protected class Stat
	{
		public int value;
		public int base;

		public Stat(int base)
		{
			this.base = base;
			value = base;
		}

		public Stat(int base, int value)
		{
			this.base = base;
			this.value = value;
		}
	}
}
