package jade.core;

import jade.util.ColoredChar;
import jade.util.Coord;

import java.awt.Color;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 * This class represents any that could be placed on a jade world. Using the act
 * method, the actor can perform its specified action. Actors can also be
 * attached to other actors, so they move as one. Actors can also be expired,
 * meaning that they should be ignored by all actors and discarded as soon as
 * possible.
 */
public abstract class Actor extends Messenger implements Serializable {
	private ColoredChar face;
	private Coord pos;
	private World world;
	private Actor holder;
	private final HashSet<Actor> holds;
	private boolean expired;

	/**
	 * Constructs an actor with the given appearance.
	 * 
	 * @param face
	 *            the actor's ascii representation
	 * @param color
	 *            the actor's color
	 */
	public Actor(char face, Color color) {
		this(new ColoredChar(face, color));
	}

	/**
	 * Constructs an actor with the given appearance.
	 * 
	 * @param face
	 *            the actor's ascii representation
	 */
	public Actor(ColoredChar face) {
		this.face = face;
		pos = new Coord();
		holds = new HashSet<Actor>();
		expired = false;
	}

	/**
	 * Specifies the actors behavior. This method is intended to be called by
	 * the world's tick method call the act method on all actors who should have
	 * a turn.
	 */
	public abstract void act();

	/**
	 * Attaches this actor to another. The actor should not be held by another.
	 * If the actor belongs to a world, it will be removed and placed on the new
	 * holder's world. This actors position will then be the holder's position.
	 * 
	 * @param actor
	 */
	protected void attachTo(Actor actor) {
		assert !isHeld();
		if (isBound())
			getWorld().removeActor(this);
		if (actor.isBound()) {
			setWorld(actor.world);
			world.registerActor(this);
		}
		holder = actor;
		actor.holds.add(this);
		pos = actor.pos;
	}

	/**
	 * Detaches this actor from its holder. The actor must be held. The actor
	 * will be placed on the holder's world at the holder's position (assuming
	 * the holder belongs to a world).
	 */
	protected void detachFrom() {
		assert isHeld();
		holder.holds.remove(this);
		final Coord holderPos = new Coord(holder.pos);
		holder = null;
		if (isBound())
			setPos(holderPos.getX(), holderPos.getY());
	}

	/**
	 * Expires this actor. This will also expire all actors attached to this
	 * actor.
	 */
	protected void expire() {
		expired = true;
		for (final Actor held : holds)
			held.expire();
	}

	/**
	 * Returns the appearance of this actor.
	 * 
	 * @return the appearance of this actor.
	 */
	public ColoredChar getLook() {
		return face;
	}

	public Coord getPos() {
		assert isBound();
		return pos;
	}

	/**
	 * Returns the world the actor currently belongs too, or null if it does not
	 * belong to a world.
	 * 
	 * @return the world the actor currently belongs too, or null if it does not
	 *         belong to a world.
	 */
	protected World getWorld() {
		return world;
	}

	/**
	 * Returns the x-coordinate of the actor. The actor must belong to a world.
	 * If it is attached to another actor, this will be the x-coordinate of the
	 * actor's holder.
	 * 
	 * @return the x-coordinate of the actor
	 */
	public int getX() {
		assert isBound();
		return pos.getX();
	}

	/**
	 * Returns the y-coordinate of the actor. The actor must belong to a world.
	 * If it is attached to another actor, this will be the y-coordinate of the
	 * actor's holder.
	 * 
	 * @return the y-coordinate of the actor
	 */
	public int getY() {
		assert isBound();
		return pos.getY();
	}

	/**
	 * Returns the actor if the actor is held, or null if it is not.
	 * 
	 * @return the actor if the actor is held, or null if it is not.
	 */
	protected Actor getHolder() {
		return holder;
	}

	/**
	 * Returns a collection of the actors this actor holds. The collection
	 * returned is not the actual backing collection, so any changes made to
	 * this collection will not be reflected in the actor.
	 * 
	 * @return a collection of the actors this actor holds.
	 */
	protected Collection<Actor> getHolds() {
		return new HashSet<Actor>(holds);
	}

	/**
	 * Returns true if the actor belongs to a world.
	 * 
	 * @return true if the actor belongs to a world.
	 */
	protected boolean isBound() {
		return world != null;
	}

	/**
	 * Checks whether the actor belongs to the given world.
	 * 
	 * @param world
	 *            the world to be checked
	 * @return true if the actor belongs to the world, false otherwise
	 */
	protected boolean isBoundTo(World world) {
		assert world != null;
		return this.world == world;
	}

	/**
	 * Returns true if the actor is expired.
	 * 
	 * @return true if the actor is expired.
	 */
	public boolean isExpired() {
		return expired;
	}

	/**
	 * Returns true if the actor is held.
	 * 
	 * @return true if the actor is held.
	 */
	protected boolean isHeld() {
		return holder != null;
	}

	/**
	 * Moves uses setPos to move the actor by the specified amount. The actor
	 * must belong to a world, and should not be attached to another actor.
	 * 
	 * @param dx
	 *            the amount to move horizontally
	 * @param dy
	 *            the amount to move vertically
	 */
	protected void move(int dx, int dy) {
		setPos(getX() + dx, getY() + dy);
	}

	/**
	 * Sets the face of this actor
	 */
	protected void setFace(ColoredChar face) {
		this.face = face;
	}

	/**
	 * Moves the actor to the specified location on the actor's world. The actor
	 * must belong to a world, and should not be attached to another actor.
	 * 
	 * @param x
	 *            the new x location
	 * @param y
	 *            the new y location
	 */
	public void setPos(int x, int y) {
		assert isBound();
		assert !isHeld();
		world.removeFromGrid(this);
		pos.move(x, y);
		world.addToGrid(this);
	}

	protected void setWorld(World world) {
		assert !isHeld() || holder.world == world;
		this.world = world;
		for (final Actor held : holds)
			held.setWorld(world);
	}

	/**
	 * Returns the character representation of this actor.
	 */
	@Override
	public String toString() {
		return face.toString();
	}
}