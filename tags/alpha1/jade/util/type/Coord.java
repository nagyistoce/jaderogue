package jade.util.type;

import java.io.Serializable;

/**
 * Represents a Cartesian integer coordinate.
 */
public class Coord implements Comparable<Coord>, Serializable
{
	private int x;
	private int y;

	/**
	 * Creates a new Coord at the origin.
	 */
	public Coord()
	{
		move(0, 0);
	}

	/**
	 * Creates a new Coord at the specified location.
	 */
	public Coord(int x, int y)
	{
		move(x, y);
	}

	/**
	 * Creates a new Coord at the specified location.
	 */
	public Coord(Coord coord)
	{
		move(coord);
	}

	/**
	 * Changes the Coord to the new location
	 */
	public Coord move(int x, int y)
	{
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * Changes the Coord to the new location
	 */
	public final Coord move(Coord coord)
	{
		return move(coord.x, coord.y);
	}

	/**
	 * Changes the Coord by the specified amount
	 */
	public Coord translate(int dx, int dy)
	{
		x += dx;
		y += dy;
		return this;
	}

	/**
	 * Translates the Coord in the specified direction
	 */
	public Coord translate(Direction dir)
	{
		x += dir.dx;
		y += dir.dy;
		return this;
	}

	/**
	 * Changes the Coord by the specified amount
	 */
	public final Coord translate(Coord coord)
	{
		return translate(coord.x, coord.y);
	}

	/**
	 * Gets a copy of this Coord translated by the specified amount
	 */
	public final Coord getTranslated(Coord coord)
	{
		return getTranslated(coord.x(), coord.y());
	}

	/**
	 * Gets a copy of this Coord translated by the specified amount
	 */
	public Coord getTranslated(int x, int y)
	{
		return new Coord(this).translate(x, y);
	}

	/**
	 * Gets a copy of this Coord translated in the specified direction
	 */
	public Coord getTranslated(Direction dir)
	{
		return new Coord(this).translate(dir);
	}

	/**
	 * Gets the x portion of the Coord
	 */
	public int x()
	{
		return x;
	}

	/**
	 * Gets the y portion of the Coord
	 */
	public int y()
	{
		return y;
	}

	public int compareTo(Coord other)
	{
		if(x == other.x)
			return y - other.y;
		return x - other.x;
	}

	public boolean equals(Coord other)
	{
		return x() == other.x() && y() == other.y();
	}

	public double distTo(Coord other)
	{
		int a = x - other.x;
		int b = y - other.y;
		return Math.sqrt(a * a + b * b);
	}

	public double taxiDistTo(Coord other)
	{
		int a = Math.abs(x - other.x);
		int b = Math.abs(y - other.y);
		return a + b;
	}
	
	public double rlDistTo(Coord other)
	{
		int a = Math.abs(x - other.x);
		int b = Math.abs(y - other.y);
		return Math.min(a, b);
	}

	/**
	 * Returns the direction needed to get from start to goal.
	 */
	public Direction directionTo(Coord goal)
	{
		int dx = goal.x - x;
		int dy = goal.y - y;
		if(dx < 0)
		{
			if(dy < 0)
				return Direction.NW;
			else if(dy > 0)
				return Direction.SW;
			else
				return Direction.W;
		}
		else if(dx > 0)
		{
			if(dy < 0)
				return Direction.NE;
			else if(dy > 0)
				return Direction.SE;
			else
				return Direction.E;
		}
		else
		{
			if(dy < 0)
				return Direction.N;
			else if(dy > 0)
				return Direction.S;
			else
				return Direction.O;
		}
	}
	
	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}