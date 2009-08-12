package rl.creature;

import jade.core.Console;
import jade.fov.Camera;
import jade.fov.FoV.FoVFactory;
import jade.util.Coord;
import jade.util.Tools;
import java.awt.Color;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import rl.item.Inventory;
import rl.item.Item;
import rl.item.Item.Type;
import rl.magic.Spell;
import rl.magic.Spellbook;
import rl.magic.Instant.Effect;
import rl.magic.Spell.Target;
import rl.world.Dungeon;

public class Player extends Creature implements Serializable, Camera
{
	public static final int VISION = 4;
	private transient Console console;
	private final Spellbook spellbook;
	private final Set<Effect> effects;
	private Collection<Coord> fov;
	private final Inventory inventory;
	private final Dungeon dungeon;
	private static final float REGEN = .05f;
	private final String name;
	private boolean playing;

	public Player(Console console, Dungeon dungeon, String name)
	{
		super('@', Color.white, 20, 10, 10, 10, 1, 0, 10);
		onDeserialize(console);
		this.dungeon = dungeon;
		this.name = name;
		inventory = new Inventory(this);
		spellbook = new Spellbook();
		effects = new HashSet<Effect>();
		spellbook.add(new Spell(this, Effect.FIRE, Target.AREA, 10, 5, 1,
				"fire trap"));
		spellbook.add(new Spell(this, Effect.ELEC, Target.OTHER, 10, 5, 1,
				"elec trap"));
		spellbook.add(new Spell(this, Effect.STONEFALL, Target.AREA, 10, 0, 1,
				"collapse"));
		spellbook.add(new Spell(this, Effect.CHANNEL, Target.SELF, 10, 10, 1,
				"concentrate"));
		spellbook.add(new Spell(this, Effect.RFIRE, Target.SELF, 20, 5, 1,
				"resist fire"));
		spellbook.add(new Spell(this, Effect.RELEC, Target.SELF, 20, 5, 1,
				"resist elec"));
	}

	@Override
	public void act()
	{
		char key = '\0';
		boolean moved = false;
		while (!moved)
		{
			key = console.getKey();
			moved = true;
			switch (key)
			{
			case 'G':
				// make a new spell based on known effects
				break;
			case 'X':
				playing = false;
				break;
			case 'p':
				spellbook();
				moved = false;
				break;
			case 'm':
				cast();
				break;
			case 'i':
				inventory();
				moved = false;
				break;
			case 'g':
				inventory.get();
				break;
			case 'd':
				inventory.drop(inventory());
				break;
			case 'e':
				equipment();
				moved = false;
				break;
			case 'w':
				inventory.equip(inventory());
				break;
			case 't':
				inventory.unequip(equipment());
				break;
			case '>':
				dungeon.descend();
				break;
			case '<':
				dungeon.ascend();
				break;
			case 'r':
				final Item scroll = choose(inventory.getTypedItems(Type.SCROLL),
						"Scrolls");
				if (scroll != null)
				{
					appendMessage(this + " reads " + scroll);
					scroll.act();
				}
				else
					appendMessage("Invalid Selection");
				break;
			case 'q':
				final Item potion = choose(inventory.getTypedItems(Type.POTION),
						"Potions");
				if (potion != null)
				{
					appendMessage(this + " quaffs " + potion);
					potion.act();
				}
				else
					appendMessage("Invalid Selection");
				break;
			default:
				final Coord dir = Tools.keyToDir(key, true, false);
				if (dir != null)
					move(dir.x(), dir.y());
				else
					moved = false;
				break;
			}
		}
		inventory.removeExpired();
		if (dice.nextFloat() < REGEN)
			hp().cappedBuff(1);
		if (dice.nextFloat() < REGEN)
			mp().cappedBuff(1);
		calcFoV();
	}

	@Override
	public Coord getTarget()
	{
		console.saveBuffer();
		final Coord target = new Coord(x(), y());
		char key = '\0';
		while (key != 't')
		{
			console.recallBuffer();
			console.buffCamera(this, 4, 4, target, '*', Color.white);
			console.refreshScreen();
			key = console.getKey();
			final Coord dir = Tools.keyToDir(key, true, false);
			if (dir != null)
			{
				target.translate(dir);
				if (!fov.contains(target))
					target.translate(-dir.x(), -dir.y());
			}
		}
		console.recallBuffer();
		console.refreshScreen();
		return target;
	}

	public void calcFoV()
	{
		fov = FoVFactory.get(FoVFactory.CircularShadow).calcFoV(world(), x(), y(),
				VISION);
	}

	public Collection<Coord> getFoV()
	{
		return fov;
	}

	public void addEffect(Effect effect)
	{
		effects.add(effect);
	}

	public String status()
	{
		String result = "";
		result += "hp:" + hp() + '\n';
		result += "mp:" + mp() + '\n';
		result += "atk:" + atk() + '\n';
		result += "def:" + def() + '\n';
		result += "dmg:" + dmg() + '\n';
		return result;
	}

	private <T> T choose(List<T> elements, String title)
	{
		console.saveBuffer();
		console.buffString(0, 0, title, Color.white);
		int i = 0;
		for (final Object element : elements)
		{
			console.buffString(0, i + 1, Tools.intToAlpha(i) + " " + element,
					Color.white);
			i++;
		}
		console.refreshScreen();
		final int index = Tools.alphaToInt(console.getKey());
		console.recallBuffer();
		console.refreshScreen();
		if (index < 0 || index >= elements.size())
			return null;
		return elements.get(index);
	}

	private Item inventory()
	{
		return choose(inventory.getAllItems(), "Inventory");
	}

	private Item equipment()
	{
		return choose(inventory.getEquiped(), "Equipment");
	}

	private Spell spellbook()
	{
		return choose(spellbook.spells(), "Spellbook");
	}

	private void cast()
	{
		final Spell spell = spellbook();
		if (spell == null)
			appendMessage("Invalid selection");
		else
			spell.cast();
	}

	public void learn(Effect effect)
	{
		appendMessage(this + " learns " + effect);
	}
	
	public void onDeserialize(Console console)
	{
		this.console = console;
		playing = true;
	}

	public boolean playing()
	{
		return playing && !isExpired();
	}

	public String toString()
	{
		return name;
	}
}
