package jade.core;

import jade.util.ColoredChar;
import jade.util.Coord;
import jade.util.Dice;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A level in which Jade Actors interact.
 */
public class World extends Messenger
{
    private Tile[][] grid;
    private int width;
    private int height;
    private Set<Actor> actorRegister;

    /**
     * Constructs an empty World with the given dimensions.
     * @param width the width of the new World
     * @param height the height of the new World
     */
    public World(int width, int height)
    {
        this.width = width;
        this.height = height;
        grid = new Tile[width][height];
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                grid[x][y] = new Tile();
        actorRegister = new HashSet<Actor>();
    }

    /**
     * Returns the width of the World
     * @return the width of the World
     */
    public int width()
    {
        return width;
    }

    /**
     * Returns the height of the World
     * @return the height of the World
     */
    public int height()
    {
        return height;
    }

    /**
     * Adds an Actor to the World at (x, y).
     * @param actor the Actor to be added
     * @param x the x value of the location of the actor
     * @param y the y value of the location of the actor
     */
    public void addActor(Actor actor, int x, int y)
    {
        actor.setWorld(this);
        registerActor(actor);
        actor.setPos(x, y);
    }

    /**
     * Adds an Actor to the World at (x, y), as given by the Coord
     * @param actor the Actor to be added
     * @param coord the location of the actor
     */
    public final void addActor(Actor actor, Coord coord)
    {
        addActor(actor, coord.x(), coord.y());
    }

    /**
     * Removes an Actor from the World. The method raises an error if the Actor
     * is not bound to this World.
     * @param actor the Actor to be removed
     */
    public void removeActor(Actor actor)
    {
        if(!actor.bound(this))
            throw new IllegalStateException("Actor not bound to World!");
        unoccupyGrid(actor);
        unregisterActor(actor);
        actor.setWorld(null);
    }

    /**
     * Removes all actors which have been marked as expired.
     */
    public final void removeExpired()
    {
        Set<Actor> expired = new HashSet<Actor>();
        for(Actor actor : actorRegister)
            if(actor.expired())
                expired.add(actor);
        for(Actor actor : expired)
            if(actor.bound(this))
                removeActor(actor);
    }

    /**
     * Returns all instances of Actor of type T that are currently bound to this
     * World. Note that the return type is based on T, and is determined by cls.
     * @param <T> the type that filters the instances of Actor
     * @param cls the Class that determines T
     * @return the set of all Actors of type T
     */
    public <T extends Actor> Set<T> getActors(Class<T> cls)
    {
        return getActors(actorRegister, cls);
    }

    /**
     * Returns an arbitrary actor of type T that is currently bound to the
     * World, or null if there is none. Note that the return type is T and is
     * determined by cls.
     * @param <T> the return type, which must extend Actor
     * @param cls the Class that determines T
     * @return an actor of type T that is bound to the World
     */
    public <T extends Actor> T getActor(Class<T> cls)
    {
        return getActor(actorRegister, cls);
    }

    /**
     * Returns all instances of Actor of type T occupying the tile at (x, y).
     * Note that the return type is based on T, and is determined by cls.
     * @param <T> the type that filters each instance of Actor
     * @param cls the Class that determines T
     * @param x the x value of the location being queried
     * @param y the y value of the location being queried
     * @return all instances of actor of type T at (x, y)
     */
    public <T extends Actor> Set<T> getActorsAt(Class<T> cls, int x, int y)
    {
        return getActors(grid[x][y].occupants, cls);
    }

    /**
     * Returns all instances of Actor of type T occupying the tile at (x, y),
     * which is determined by the given Coord. Note that the return type is
     * based on T, and is determined by cls.
     * @param <T> the type that filters each instance of Actor
     * @param cls the Class that determines T
     * @param coord the value of the location being queried
     * @return all instances of actor of type T at (x, y)
     */
    public final <T extends Actor> Set<T> getActorsAt(Class<T> cls, Coord coord)
    {
        return getActorsAt(cls, coord.x(), coord.y());
    }

    /**
     * Returns an arbitrary Actor of type T which occupies the tile at (x, y),
     * or returns null if there is none. Note that the return type is T and is
     * determined by cls.
     * @param <T> the return type
     * @param cls the Class that determines T
     * @param x the x value of the location being queried
     * @param y the y value of the location being queried
     * @return an arbitrary Actor of type t which occupies (x, y)
     */
    public <T extends Actor> T getActorAt(Class<T> cls, int x, int y)
    {
        return getActor(grid[x][y].occupants, cls);
    }

    /**
     * Returns an arbitrary Actor of type T which occupies the tile at (x, y),
     * or returns null if there is none, where (x, y) is determined by coord.
     * Note that the return type is T and is determined by cls.
     * @param <T> the return type
     * @param cls the Class that determines T
     * @param coord the value of the location being queried
     * @return an arbitrary Actor of type t which occupies (x, y)
     */
    public final <T extends Actor> T getActorAt(Class<T> cls, Coord coord)
    {
        return getActorAt(cls, coord.x(), coord.y());
    }

    @SuppressWarnings("unchecked")
    private <T extends Actor> Set<T> getActors(Set<Actor> actors, Class<T> cls)
    {
        Set<T> filtered = new HashSet<T>();
        for(Actor actor : actors)
            if(cls.isInstance(actor))
                filtered.add((T)actor);
        return filtered;
    }

    @SuppressWarnings("unchecked")
    private <T extends Actor> T getActor(Set<Actor> actors, Class<T> cls)
    {
        for(Actor actor : actors)
            if(cls.isInstance(actor))
                return (T)actor;
        return null;
    }

    public List<ColoredChar> lookAll(int x, int y)
    {
        List<ColoredChar> look = new ArrayList<ColoredChar>();
        look.add(tileAt(x, y));
        return look;
    }

    public final List<ColoredChar> lookAll(Coord coord)
    {
        return lookAll(coord.x(), coord.y());
    }

    public final ColoredChar look(int x, int y)
    {
        List<ColoredChar> look = lookAll(x, y);
        return look.get(look.size() - 1);
    }

    public final ColoredChar look(Coord coord)
    {
        return look(coord.x(), coord.y());
    }

    /**
     * Returns the face of the tile at (x, y).
     * @param x the x value of the location being queried
     * @param y the y value of the location being queried
     * @return the face of the tile at (x, y)
     */
    public ColoredChar tileAt(int x, int y)
    {
        return grid[x][y].face;
    }

    /**
     * Returns the face of the tile at (x, y), as given by the Coord
     * @param pos the position of the location being queried
     * @return the face of the tile at (x, y)
     */
    public final ColoredChar tileAt(Coord pos)
    {
        return tileAt(pos.x(), pos.y());
    }

    /**
     * Returns a randomly choosen open tile within the given rectangle bounds
     * using the given instance of Dice for random number generation.
     * @param dice the instance of Dice used for random number generation
     * @param x1 the minimum x value of the tile
     * @param y1 the minumum y value of the tile
     * @param x2 the maximum x value of the tile
     * @param y2 the maximum y value of the tile
     * @return a randomly choosen open tile
     */
    public Coord getOpenTile(Dice dice, int x1, int y1, int x2, int y2)
    {
        for(int i = 0; i < 100; i++)
        {
            int x = dice.next(x1, x2);
            int y = dice.next(y1, y2);
            if(passable(x, y))
                return new Coord(x, y);
        }
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                if(passable(x, y))
                    return new Coord(x, y);
        return new Coord();
    }

    /**
     * Returns a randomly choosen open tile within the given rectangle bounds.
     * @param x1 the minimum x value of the tile
     * @param y1 the minumum y value of the tile
     * @param x2 the maximum x value of the tile
     * @param y2 the maximum y value of the tile
     * @return a randomly choosen open tile
     */
    public final Coord getOpenTile(int x1, int y1, int x2, int y2)
    {
        return getOpenTile(Dice.global, x1, y1, x2, y2);
    }

    /**
     * Returns the location of a randomly choosen open tile using the given
     * instance of Dice is used for random number generation.
     * @param dice the instance of Dice used for random number generation
     * @return the location of a randomly choosen open tile
     */
    public final Coord getOpenTile(Dice dice)
    {
        return getOpenTile(dice, 0, 0, width - 1, height - 1);
    }

    /**
     * Returns the location of a randomly choosen open tile. The global instance
     * of Dice is used for random number generation.
     * @return the location of a randomly choosen open tile
     */
    public final Coord getOpenTile()
    {
        return getOpenTile(Dice.global);
    }

    /**
     * Returns true if the tile at (x, y) is passable.
     * @param x the x value of the location being queried
     * @param y the y value of the location being queried
     * @return true if the tile at (x, y) is passable
     */
    public boolean passable(int x, int y)
    {
        return grid[x][y].passable;
    }

    /**
     * Returns true if the tile at (x, y), as specified by the given Coord, is
     * passable.
     * @param pos the location being queried
     * @return true if the tile at (x, y) is passable
     */
    public final boolean passable(Coord pos)
    {
        return passable(pos.x(), pos.y());
    }

    /**
     * Sets the face and passable value of the tile at (x, y)
     * @param x the x value of the location being queried
     * @param y the y value of the location being queried
     * @param face the new face of the tile
     * @param passable new the passable value of the tile
     */
    public void setTile(int x, int y, ColoredChar face, boolean passable)
    {
        grid[x][y].face = face;
        grid[x][y].passable = passable;
    }

    /**
     * Sets the face and passable value of the tile at (x, y), as given by the
     * Coord
     * @param pos the position of the location being queried
     * @param face the new face of the tile
     * @param passable new the passable value of the tile
     */
    public final void setTile(Coord pos, ColoredChar face, boolean passable)
    {
        setTile(pos.x(), pos.y(), face, passable);
    }

    void registerActor(Actor actor)
    {
        actorRegister.add(actor);
    }

    void unregisterActor(Actor actor)
    {
        actorRegister.remove(actor);
    }

    void unoccupyGrid(Actor actor)
    {
        grid[actor.x()][actor.y()].occupants.remove(actor);
    }

    void occupyGrid(Actor actor)
    {
        grid[actor.x()][actor.y()].occupants.add(actor);
    }

    private class Tile
    {
        public ColoredChar face;
        public boolean passable;
        public Set<Actor> occupants;

        public Tile()
        {
            face = new ColoredChar('.');
            passable = true;
            occupants = new HashSet<Actor>();
        }
    }
}
