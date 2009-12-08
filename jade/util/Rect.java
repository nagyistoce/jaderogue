package jade.util;

/**
 * This class represents a rectangle. Rect is different java.awt.Rectangle in
 * that it stores the upper left coordinates of the rectangle, as well as the
 * lower right coordinates, as opposed to java.awt.Rectangle which stores the
 * upper left corner and a height and width. Rect also implements comparable,
 * though this might not make sense in the real world, this allows rectangles to
 * be inserted in to search tree's.
 */
public class Rect {
	private Coord upleft;
	private Coord lowright;
	private int width;
	private int height;

	/**
	 * Constructs a Rect with one point at the origin and the other at the
	 * specified coordinate.
	 * 
	 * @param coord
	 *            x coordinate
	 */
	public Rect(Coord coord) {
		this(coord.getX(), coord.getY());
	}

	/**
	 * Constructs a Rect with both points as vertices.
	 * 
	 * @param coord1
	 *            the coordinate of the first vertex
	 * @param coord2
	 *            the -coordinate of the second vertex
	 */
	public Rect(Coord coord1, Coord coord2) {
		this(coord1.getX(), coord1.getY(), coord2.getX(), coord2.getY());
	}

	/**
	 * Constructs a Rect with one point at the origin and the other at (x,y)
	 * 
	 * @param x
	 *            an x coordinate
	 * @param y
	 *            a y coordinate
	 */
	public Rect(int x, int y) {
		this(0, 0, x, y);
	}

	/**
	 * Constructs a Rect with both points as vertices.
	 * 
	 * @param x1
	 *            the x-coordinate of the first vertex
	 * @param y1
	 *            the y-coordinate of the first vertex
	 * @param x2
	 *            the x-coordinate of the second vertex
	 * @param y2
	 *            the y-coordinate of the second vertex
	 */
	public Rect(int x1, int y1, int x2, int y2) {
		upleft = new Coord(Math.min(x1, x2), Math.max(y1, y2));
		lowright = new Coord(Math.max(x1, x2), Math.min(y1, y2));
		calcWidth();
		calcHeight();
	}

	private void calcHeight() {
		height = yMax() - yMin();
	}

	private void calcWidth() {
		width = xMax() - xMin();
	}

	public Rect getTranslated(int dx, int dy) {
		Rect result = new Rect(upleft, lowright);
		result.translate(dx, dy);
		return result;
	}

	public int height() {
		return height;
	}

	@Override
	public String toString() {
		return upleft.toString() + " " + lowright.toString();
	}

	public void translate(int dx, int dy) {
		upleft.translate(dx, dy);
		lowright.translate(dx, dy);
	}

	public int width() {
		return width;
	}

	public int xMax() {
		return lowright.getX();
	}

	public int xMin() {
		return upleft.getX();
	}

	public int yMax() {
		return upleft.getY();
	}

	public int yMin() {
		return lowright.getY();
	}
}
