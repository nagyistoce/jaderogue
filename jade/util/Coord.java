package jade.util;

/**
 *	Represents an integer cartesian coordinate.
 */
public class Coord implements Comparable<Coord>
{
	private int x;
	private int y;

	/**
	 * Creates a default coord at (0,0)
	 */
	public Coord()
	{
		move(0, 0);
	}

	/**
	 * Creates a coord with the specifed coordinates
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public Coord(int x, int y)
	{
		move(x, y);
	}

	/**
	 * A deep copy constructor
	 * @param coord to be copied
	 */
	public Coord(Coord coord)
	{
		move(coord);
	}

	/**
	 * Moves this coord to the specified coordinate
	 * @param x the new x-coordinate
	 * @param y the new y-coordinate
	 */
	public void move(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Moves the coordinate to the location of the given coordinate
	 * @param coord the coordinate to move this coord to
	 */
	public void move(Coord coord)
	{
		move(coord.x, coord.y);
	}

	public void translate(int dx, int dy)
	{
		x += dx;
		y += dy;
	}

	public void translate(Coord coord)
	{
		translate(coord.x, coord.y);
	}

	public int x()
	{
		return x;
	}

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

	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}
