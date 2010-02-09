package jade;

import jade.core.Actor;
import jade.core.Console;
import jade.core.World;
import jade.fov.FoV.FoVFactory;
import jade.gen.Gen.GenFactory;
import jade.util.ColoredChar;
import jade.util.Coord;
import jade.util.Dice;
import jade.util.Direction;
import jade.util.Tools;
import java.awt.Color;
import java.util.Collection;

public class Demo
{
	private static Console console;

	public static void main(String[] args) throws InterruptedException
	{
		console = Console.getFramedConsole("Demo");
		console.buffChar(0, 0, '@', Color.white);
		console.refreshScreen();
		World world = new DemoWorld();
		GenFactory.traditional().generate(world, System.currentTimeMillis());
		Actor hero = new DemoActor();
		world.addActor(hero, Dice.global);
		for(int i = 0; i < 5; i++)
			world.addActor(new DemoTrap(), Dice.global);
		boolean display2 = true;
		while(!hero.isExpired())
		{
			if(display2)
				displayWorld2(world, hero);
			else
				displayWorld(world);
			world.tick();
		}
		console.clearBuffer();
		console.buffString(0, 0, "The trap got you!", Color.white);
		console.refreshScreen();
		console.getKey();
		console.exit();
	}

	private static void displayWorld(World world)
	{
		console.clearBuffer();
		for(int x = 0; x < world.width; x++)
			for(int y = 0; y < world.height; y++)
				console.buffChar(x, y, world.look(x, y));
		console.refreshScreen();
	}
	
	private static void displayWorld2(World world, Actor actor)
	{
		Collection<Coord> fov = FoVFactory.shadowCircle().calcFoV(actor, 10);
		console.clearBuffer();
		for(Coord coord : fov)
			console.buffChar(coord, world.look(coord));
		console.refreshScreen();
	}

	private static class DemoWorld extends World
	{
		public DemoWorld()
		{
			super(Console.DEFAULT_WIDTH, Console.DEFAULT_HEIGHT);
		}

		public void tick()
		{
			for(DemoActor actor : getActors(DemoActor.class))
				actor.act();
			for(DemoTrap trap : getActors(DemoTrap.class))
				trap.act();
			removeExpired();
		}

		public ColoredChar look(int x, int y)
		{
			Actor actor = getActorAt(x, y, DemoActor.class);
			if(actor != null)
				return actor.look();
			actor = getActorAt(x, y, DemoTrap.class);
			if(actor != null)
				return actor.look();
			return super.look(x, y);
		}
	}

	private static class DemoActor extends Actor
	{
		public DemoActor()
		{
			super('@', Color.white);
		}

		public void act()
		{
			char key = console.getKey();
			Direction dir = Tools.keyToDir(key, true, false);
			if(dir != null)
				move(dir);
		}
	}

	private static class DemoTrap extends Actor
	{
		private int timer = 10;

		public DemoTrap()
		{
			super('*', Color.red);
		}

		public void act()
		{
			if(!held())
			{
				DemoActor hero = world().getActorAt(pos(), DemoActor.class);
				if(hero != null)
					attachTo(hero);
			}
			else
			{
				if(timer-- == 0)
					holder().expire();
			}
		}
	}
}
