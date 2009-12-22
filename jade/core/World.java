package jade.core;

import jade.util.ColoredChar;
import jade.util.Coord;
import jade.util.Dice;
import jade.util.Rect;

import java.awt.Color;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is a 2D grid based world in which jade actors live. Each tile in
 * the grid has an appearance and can hold actors. Through the tick method, the
 * world's behavior is controled. Most likely, the tick method will call the act
 * method on the appropriate actors.
 */
public abstract class World extends Messenger implements Serializable {

	/**
	 * This class represents a single tile on a jade World. They can be accessed
	 * using the tile method of the jade world.
	 */
	public final class Tile implements Serializable {
		private ColoredChar tile;
		private boolean passable;
		private final HashSet<Actor> actors;

		Tile() {
			setTile('.', Color.white, true);
			actors = new HashSet<Actor>();
		}

		Set<Actor> getActors() {
			return actors;
		}

		/**
		 * Returns the tile's character representation. Note that for most
		 * cases, calling look on the jade World is preferable since it could be
		 * overriden to consider actor occupants.
		 * 
		 * @return the tile's character representation
		 */
		public ColoredChar getLook() {
			return tile;
		}

		/**
		 * Returns the tile's default passability. Note that for most cases,
		 * calling passable on the jade World is prefereable since it could be
		 * overriden to consider actor occupants.
		 * 
		 * @return the tile's default passability
		 */
		public boolean isPassable() {
			return passable;
		}

		/**
		 * Changes the tile's appearance and default passability.
		 * 
		 * @param ch
		 *            the tile's new character representation.
		 * @param color
		 *            the tile's new color
		 * @param passable
		 *            the tile's new passability.
		 */
		public void setTile(char ch, Color color, boolean passable) {
			tile = new ColoredChar(ch, color);
			this.passable = passable;
		}
	}

	public final int width;
	public final int height;
	private final Tile[][] grid;

	private final HashSet<Actor> actorRegister;

	/**
	 * Constructs a new world with the specified size. This size is immutable
	 * once the world is created.
	 * 
	 * @param width
	 *            the width of the world
	 * @param height
	 *            the height of the world
	 */
	public World(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new Tile[width][height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				grid[x][y] = new Tile();
		actorRegister = new HashSet<Actor>();
	}

	/**
	 * Adds an actor on the world at the specified location. The actors world
	 * will be set to this world. Also, any actors attached to the actor will
	 * also be added to this world.
	 * 
	 * @param actor
	 *            the actor to be added
	 * @param coord
	 *            the coordinate where the actor will be placed
	 */
	public void addActor(Actor actor, Coord coord) {
		addActor(actor, coord.getX(), coord.getY());
	}

	/**
	 * Adds an actor to a random, open tile. A tile is open if there are no
	 * actors on the tile and the tile is passable.
	 * 
	 * @param actor
	 *            the actor to be added
	 * @param random
	 *            the psuedorandom number generator to be used in selecting the
	 *            open tile
	 */
	public void addActor(Actor actor, Dice random) {
		final Coord pos = getOpenTile(random);
		addActor(actor, pos.getX(), pos.getY());
	}

	/**
	 * Adds an actor on the world at the specified location. The actors world
	 * will be set to this world. Also, any actors attached to the actor will
	 * also be added to this world.
	 * 
	 * @param actor
	 *            the actor to be added
	 * @param x
	 *            the x-coordinate where the actor will be placed
	 * @param y
	 *            the x-coordinate where the actor will be placed
	 */
	public void addActor(Actor actor, int x, int y) {
		assert !actor.isBound();
		assert !actor.isHeld();
		actor.setWorld(this);
		registerActor(actor);
		actor.setPos(x, y);
	}

	protected void addToGrid(Actor actor) {
		assert actor.isBoundTo(this);
		grid[actor.getX()][actor.getY()].getActors().add(actor);
	}

	/**
	 * Returns one actor of the specified class from the given location, or null
	 * if none is found. The return type of this method will be the class passed
	 * into the method. If there are more than one actor of the specified class
	 * at the location, there is no guarantee as to which one will be returned.
	 * 
	 * @param <T>
	 *            extends Actor. Is the return type.
	 * @param coord
	 *            the coordinate to search at
	 * @param cls
	 *            determines T
	 * @return one actor of the specified class from the given location, or null
	 *         if none is found.
	 */
	public final <T extends Actor> T getActorAt(Coord coord, Class<T> cls) {
		return getActorAt(coord.getX(), coord.getY(), cls);
	}

	/**
	 * Returns one actor of the specified class from the given location, or null
	 * if none is found. The return type of this method will be the class passed
	 * into the method. If there are more than one actor of the specified class
	 * at the location, there is no guarantee as to which one will be returned.
	 * 
	 * @param <T>
	 *            extends Actor. Is the return type.
	 * @param x
	 *            the x-coordinate to search at
	 * @param y
	 *            the y-coordinate to search at
	 * @param cls
	 *            determines T
	 * @return one actor of the specified class from the given location, or null
	 *         if none is found.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Actor> T getActorAt(int x, int y, Class<T> cls) {
		for (final Actor actor : grid[x][y].getActors())
			if (cls.isInstance(actor))
				return (T) actor;
		return null;
	}

	/**
	 * Returns a collection of all actors of the specified class currently on
	 * the world. The collection will be parameterized on the given class.
	 * 
	 * @param <T>
	 *            extends Actor. Collection<T> will be the return type
	 * @param cls
	 *            determins T
	 * @return a collection of all actors of the specified class currently on
	 *         the world.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Actor> Collection<T> getActors(Class<T> cls) {
		final Collection<T> result = new HashSet<T>();
		for (final Actor actor : actorRegister)
			if (cls.isInstance(actor))
				result.add((T) actor);
		return result;
	}

	/**
	 * Returns a collection with all the actors of the specified class at a
	 * location. The collection will be parameterized based on the given class.
	 * 
	 * @param <T>
	 *            extends Actor. Collection<T> will be the return type.
	 * @param coord
	 *            the coordinate to search at
	 * @param cls
	 *            determines T
	 * @return a collection with all the actors of the specified class at the
	 *         given location
	 */
	public final <T extends Actor> Collection<T> getActorsAt(Coord coord,
			Class<T> cls) {
		return getActorsAt(coord.getX(), coord.getY(), cls);
	}

	/**
	 * Returns a collection with all the actors of the specified class at a
	 * location. The collection will be parameterized based on the given class.
	 * 
	 * @param <T>
	 *            extends Actor. Collection<T> will be the return type.
	 * @param x
	 *            the x-coordinate to search at
	 * @param y
	 *            the y-coordinate to search at
	 * @param cls
	 *            determines T
	 * @return a collection with all the actors of the specified class at the
	 *         given location
	 */
	@SuppressWarnings("unchecked")
	public <T extends Actor> Collection<T> getActorsAt(int x, int y,
			Class<T> cls) {
		final Collection<T> result = new HashSet<T>();
		for (final Actor actor : grid[x][y].getActors())
			if (cls.isInstance(actor))
				result.add((T) actor);
		return result;
	}

	/**
	 * Returns a random open tile on the world. A tile is open if it is passable
	 * and there are no actors on it.
	 * 
	 * @param random
	 *            the psuedorandom number generator to be used in finding the
	 *            open tile
	 * @return a random open tile on the world
	 */
	public final Coord getOpenTile(Dice random) {
		return getOpenTile(random, 0, 0, width - 1, height - 1);
	}

	/**
	 * Returns a random open tile on the world from within the specified bounds.
	 * A tile is open if it is passable and there are no actors on it.
	 * 
	 * @param random
	 *            the psuedorandom number generator to be used in finding the
	 *            open tile
	 * @param upperleft
	 *            the upper left boundry of the area from which to choose the
	 *            random tile
	 * @param lowerright
	 *            the lower right boundry of the area from which to choose the
	 *            random tile
	 * @return a random open tile on the world
	 */
	public final Coord getOpenTile(Dice random, Coord upperleft,
			Coord lowerright) {
		return getOpenTile(random, upperleft.getX(), upperleft.getY(),
				lowerright.getX(), lowerright.getY());
	}

	/**
	 * Returns a random open tile on the world from within the specified bounds.
	 * A tile is open if it is passable and there are no actors on it.
	 * 
	 * @param random
	 *            the psuedorandom number generator to be used in finding the
	 *            open tile
	 * @param x1
	 *            the left boundry of the area from which to choose the random
	 *            tile
	 * @param y1
	 *            the upper boundry of the area from which to choose the random
	 *            tile
	 * @param x2
	 *            the right boundry of the area from which to choose the random
	 *            tile
	 * @param y2
	 *            the lower boundry of the area from which to choose the random
	 *            tile
	 * @return a random open tile on the world
	 */
	public Coord getOpenTile(Dice random, int x1, int y1, int x2, int y2) {
		int x, y;
		int count = 0;
		do {
			x = random.nextInt(x1, x2);
			y = random.nextInt(y1, y2);
			count++;
			if (count == 1000)
				for (int b = 0; b < height; b++) {
					for (int a = 0; a < width; a++)
						System.out.print(getLook(a, b).getCh());
					System.out.println();
				}
		} while (!isPassable(x, y) || getActorsAt(x, y, Actor.class).size() > 0);
		return new Coord(x, y);
	}

	public final Coord getOpenTile(Dice random, Rect rect) {
		return getOpenTile(random, rect.xMin(), rect.yMin(), rect.xMax(), rect
				.yMax());
	}

	/**
	 * Returns the appearance of the specified tile. By default this method
	 * returns the appearance of the tile itself, but could be overriden to
	 * return the look method of the actors on the tile.
	 * 
	 * @param coord
	 *            the coordinate location of the tile
	 * @return the appearance of the specified tile
	 */
	public ColoredChar getLook(Coord coord) {
		return getLook(coord.getX(), coord.getY());
	}

	/**
	 * Returns the appearance of the specified tile. By default this method
	 * returns the appearance of the tile itself, but could be overriden to
	 * return the look method of the actors on the tile.
	 * 
	 * @param x
	 *            the x-coordinate of the tile
	 * @param y
	 *            the y-coordinate of the tile
	 * @return the appearance of the specified tile
	 */
	public ColoredChar getLook(int x, int y) {
		return grid[x][y].getLook();
	}

	/**
	 * Returns true if the specified tile is passable, false otherwise. By
	 * default, this method only examins the passable property of the tile, but
	 * could be overriden to consider the presence of actors.
	 * 
	 * @param coord
	 *            the coordinate location of the tile
	 * @return true if the specified tile is passable, false otherwise
	 */
	public final boolean isPassable(Coord coord) {
		return isPassable(coord.getX(), coord.getY());
	}

	/**
	 * Returns true if the specified tile is passable, false otherwise. By
	 * default, this method only examins the passable property of the tile, but
	 * could be overriden to consider the presence of actors.
	 * 
	 * @param x
	 *            the x-coordinate of the tile
	 * @param y
	 *            the y-coordinate of the tile
	 * @return true if the specified tile is passable, false otherwise
	 */
	public boolean isPassable(int x, int y) {
		return grid[x][y].isPassable();
	}

	protected void registerActor(Actor actor) {
		actorRegister.add(actor);
		for (final Actor held : actor.getHolds())
			registerActor(held);
	}

	/**
	 * Removes an actor, all with all actors that are attached to it from the
	 * world. The actor must belong to this world.
	 * 
	 * @param actor
	 *            the actor to remove
	 */
	public void removeActor(Actor actor) {
		assert actor.isBoundTo(this);
		if (actor.isHeld())
			actor.detachFrom();
		removeFromGrid(actor);
		unregisterActor(actor);
		actor.setWorld(null);
	}

	/**
	 * Removes all expired actors from the world. In most cases, this method
	 * should be called somewhere in the tick method so that expired actors are
	 * removed in a timely manner.
	 */
	public void removeExpired() {
		final Collection<Actor> expired = new HashSet<Actor>();
		for (final Actor actor : actorRegister)
			if (actor.isExpired())
				expired.add(actor);
		for (final Actor actor : expired)
			if (actor.isBoundTo(this))
				removeActor(actor);
	}

	void removeFromGrid(Actor actor) {
		assert actor.isBoundTo(this);
		grid[actor.getX()][actor.getY()].getActors().remove(actor);
	}

	/**
	 * Specifies the behavior of the world
	 */
	public abstract void tick();

	/**
	 * Returns the tile at the specifed location.
	 * 
	 * @param coord
	 *            the coordinate of the tile to be returned
	 * @return the tile at the specifed location.
	 */
	public Tile getTile(Coord coord) {
		return getTile(coord.getX(), coord.getY());
	}

	/**
	 * Returns the tile at the specifed location.
	 * 
	 * @param x
	 *            the x-coordinate of the tile to be returned
	 * @param y
	 *            the y-coordinate of the tile to be returned
	 * @return the tile at the specifed location.
	 */
	public Tile getTile(int x, int y) {
		return grid[x][y];
	}

	protected void unregisterActor(Actor actor) {
		actorRegister.remove(actor);
		for (final Actor held : actor.getHolds())
			unregisterActor(held);
	}
}