package jade.gen;

import jade.core.World;
import jade.util.Coord;
import jade.util.Dice;
import jade.util.Rect;

import java.awt.Color;
import java.util.Stack;

/**
 * This implementation of Gen uses cellular automata to generate interesting
 * cave levels. The maps will always be connected either by throwing away
 * disconnected maps for small worlds, or filling in small disconnected parts on
 * large maps. Note that this algorithm doesn't work tend to work on anything
 * smaller than 7x7 maps. Instead, to avoid infinite loops, it will just result
 * in a solid block.
 */
public class Cellular implements Gen {
	private static final char UNCONNECTED_TILE = '%';
	private static final char OPEN_TILE = '.';
	private static final char CLOSED_TILE = '#';
	private static final int MIN_COUNT1 = 5;
	private static final int MAX_COUNT2 = 3;
	private static final float TILE_PERCENT = .4f;
	private static final int TINY_MAX = 20;
	private final Dice dice;

	protected Cellular() {
		dice = new Dice();
	}

	private void apply45(char[][] buffer, World world, Rect rect) {
		for (int x = rect.xMin() + 1; x < rect.xMax() - 1; x++)
			for (int y = rect.yMin() + 1; y < rect.yMax() - 1; y++)
				if (getWallcount(world, x, y, 1, rect) >= MIN_COUNT1
						|| getWallcount(world, x, y, 2, rect) <= MAX_COUNT2)
					buffer[x - rect.xMin()][y - rect.yMin()] = CLOSED_TILE;
				else
					buffer[x - rect.xMin()][y - rect.yMin()] = OPEN_TILE;
		for (int x = rect.xMin() + 1; x < rect.xMax() - 1; x++)
			for (int y = rect.yMin() + 1; y < rect.yMax() - 1; y++)
				if (buffer[x - rect.xMin()][y - rect.yMin()] == OPEN_TILE)
					setOpenTile(world, x, y);
				else
					setWallTile(world, x, y);
	}

	private boolean isConnected(World world, Rect rect) {
		if (!openExist(world, rect))
			return false;

		final float filled = floodAreaOpen(world, world.getOpenTile(dice, rect
				.xMin(), rect.yMin(), rect.xMax() - 1, rect.yMax() - 1), rect)
				/ (rect.width() * rect.height());
		if (filled > TILE_PERCENT) {
			deleteUnconnected(world, rect);
			return true;
		}
		return false;
	}

	private void deleteUnconnected(World world, Rect rect) {
		for (int x = rect.xMin(); x < rect.xMax(); x++)
			for (int y = rect.yMin(); y < rect.yMax(); y++)
				if (world.getLook(x, y).getCh() == UNCONNECTED_TILE)
					world.getTile(x, y)
							.setTile(CLOSED_TILE, Color.white, false);
	}

	private float floodAreaOpen(World world, Coord coord, Rect rect) {
		final Stack<Coord> stack = new Stack<Coord>();
		stack.push(coord);
		int count = 1;
		while (!stack.isEmpty()) {
			final Coord curr = stack.pop();
			if (!isOutOfBounds(rect, curr.getX(), curr.getY())
					&& world.getLook(curr.getX(), curr.getY()).getCh() == UNCONNECTED_TILE) {
				count++;
				world.getTile(curr.getX(), curr.getY()).setTile(OPEN_TILE,
						Color.white, true);
				stack.push(new Coord(curr.getX() + 1, curr.getY()));
				stack.push(new Coord(curr.getX() - 1, curr.getY()));
				stack.push(new Coord(curr.getX(), curr.getY() + 1));
				stack.push(new Coord(curr.getX(), curr.getY() - 1));
			}
		}
		return count;
	}

	public void generate(World world, long seed) {
		generate(world, seed, new Rect(world.width, world.height));
	}

	public void generate(World world, long seed, Rect rect) {
		boolean tooSmall = rect.width() < 7 || rect.height() < 7;
		int count = 0;
		dice.setSeed(seed);
		do {
			initialize(world, rect);
			final char[][] buffer = new char[rect.width()][rect.height()];
			for (int i = 0; i < 5; i++)
				apply45(buffer, world, rect);
			count++;
		} while (!isConnected(world, rect) && (!tooSmall || count < TINY_MAX));
	}

	private void initialize(World world, Rect rect) {
		for (int x = rect.xMin(); x < rect.xMax(); x++)
			for (int y = rect.yMin(); y < rect.yMax(); y++)
				if (x == rect.xMin() || x == rect.xMax() - 1
						|| y == rect.yMin() || y == rect.yMax() - 1)
					setWallTile(world, x, y);
				else if (dice.nextDouble() < TILE_PERCENT)
					setWallTile(world, x, y);
				else
					setOpenTile(world, x, y);
	}

	private boolean openExist(World world, Rect rect) {
		boolean result = false;
		for (int x = rect.xMin(); x < rect.xMax(); x++)
			for (int y = rect.yMin(); y < rect.yMax(); y++)
				if (world.isPassable(x, y)) {
					result = true;
					break;
				}
		return result;
	}

	private boolean isOutOfBounds(Rect rect, int x, int y) {
		return x < rect.xMin() || x >= rect.xMax() || y < rect.yMin()
				|| y >= rect.yMax();
	}

	private void setOpenTile(World world, int x, int y) {
		world.getTile(x, y).setTile(UNCONNECTED_TILE, Color.white, true);
	}

	private void setWallTile(World world, int x, int y) {
		world.getTile(x, y).setTile(CLOSED_TILE, Color.white, false);
	}

	private int getWallcount(World world, int x, int y, int range, Rect rect) {
		int count = 0;
		for (int dx = x - range; dx <= x + range; dx++)
			for (int dy = y - range; dy <= y + range; dy++) {
				if (isOutOfBounds(rect, dx, dy))
					continue;
				if (!world.isPassable(dx, dy))
					count++;
			}
		return count;
	}
}
