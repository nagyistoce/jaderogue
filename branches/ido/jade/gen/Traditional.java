package jade.gen;

import jade.core.World;
import jade.util.Coord;
import jade.util.Dice;
import jade.util.Rect;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This implementation of Gen uses a binary space partition tree to generate a
 * traditional looking dungeon with a set of interconnected rectangular rooms.
 */
public class Traditional implements Gen {
	private class BSP {
		private final int x1;
		private final int y1;
		private final int x2;
		private final int y2;
		private int rx1;
		private int ry1;
		private int rx2;
		private int ry2;
		private BSP left;
		private BSP right;
		private BSP parent;
		private boolean connected;
		private boolean readyConnect;

		private BSP(BSP parent, int div, boolean vert, boolean left) {
			this.parent = parent;
			connected = false;
			readyConnect = true;
			x1 = parent.x1 + (vert && !left ? div + 1 : 0);
			y1 = parent.y1 + (!vert && !left ? div + 1 : 0);
			x2 = vert && left ? parent.x1 + div : parent.x2;
			y2 = !vert && left ? parent.y1 + div : parent.y2;
		}

		public BSP(Rect rect) {
			connected = true;
			readyConnect = true;
			x1 = rect.xMin();
			y1 = rect.yMin();
			x2 = rect.xMax() - 1;
			y2 = rect.yMax() - 1;
		}

		public void addCycles(World world) {
			List<BSP> leaves = new ArrayList<BSP>(getLeaves());
			if (leaves.size() < 5)
				return;
			for (int i = 0; i < dice.nextInt(leaves.size() / 2, leaves.size()); i++) {
				BSP leaf1 = leaves.remove(leaves.size() - 1);
				BSP leaf2 = leaves.get(dice.nextInt(leaves.size()));
				leaf1.connectTo(world, leaf2);
			}
		}

		public void connect(World world) {
			if (!leaf()) {
				left.connect(world);
				right.connect(world);
			}
			readyConnect = true;
			connectToSibling(world);
		}

		public void connectTo(World world, BSP leaf) {
			final Coord start = world.getOpenTile(dice, x1, y1, x2, y2);
			final Coord end = world.getOpenTile(dice, leaf.x1, leaf.y1,
					leaf.x2, leaf.y2);
			final List<Coord> corridor = new LinkedList<Coord>();
			final Coord curr = new Coord(start);
			while (!curr.equals(end)) {
				corridor.add(new Coord(curr));
				final boolean digging = !world.isPassable(curr);
				if (curr.getX() == end.getX())
					curr.translate(0, curr.getY() < end.getY() ? 1 : -1);
				else
					curr.translate(curr.getX() < end.getX() ? 1 : -1, 0);
				if (digging && world.isPassable(curr)) {
					if (leaf.inside(curr))
						break;
					corridor.clear();
				}
			}
			for (final Coord coord : corridor)
				world.getTile(coord).setTile('.', Color.white, true);
		}

		private void connectToSibling(World world) {
			if (connected)
				return;
			final BSP sibling = this == parent.left ? parent.right
					: parent.left;
			if (!sibling.readyConnect)
				return;
			final Coord start = world.getOpenTile(dice, x1, y1, x2, y2);
			final Coord end = world.getOpenTile(dice, sibling.x1, sibling.y1,
					sibling.x2, sibling.y2);
			final List<Coord> corridor = new LinkedList<Coord>();
			final Coord curr = new Coord(start);
			while (!curr.equals(end)) {
				corridor.add(new Coord(curr));
				final boolean digging = !world.isPassable(curr);
				if (curr.getX() == end.getX())
					curr.translate(0, curr.getY() < end.getY() ? 1 : -1);
				else
					curr.translate(curr.getX() < end.getX() ? 1 : -1, 0);
				if (digging && world.isPassable(curr)) {
					if (sibling.inside(curr))
						break;
					corridor.clear();
				}
			}
			for (final Coord coord : corridor)
				world.getTile(coord).setTile('.', Color.white, true);
			connected = true;
			sibling.connected = true;
		}

		public void divide() {
			while (divideAux())
				;
		}

		private boolean divideAux() {
			if (!leaf()) {
				final boolean divleft = left.divideAux();
				final boolean divRight = right.divideAux();
				return divleft || divRight;
			}
			boolean vert = dice.nextBoolean();
			final int min = MIN_SIZE + 4;
			if (divTooSmall(vert, min))
				vert = !vert;
			if (divTooSmall(vert, min))
				return false;
			final int div = dice.nextInt(min, (vert ? x2 - x1 : y2 - y1) - min);
			left = new BSP(this, div, vert, true);
			right = new BSP(this, div, vert, false);
			readyConnect = false;
			return true;
		}

		private boolean divTooSmall(boolean vert, int min) {
			min *= 2;
			return vert ? x2 - x1 < min : y2 - y1 < min;
		}

		public Set<BSP> getLeaves() {
			Set<BSP> leaves = new HashSet<BSP>();
			getLeavesAux(leaves);
			return leaves;
		}

		private void getLeavesAux(Set<BSP> leaves) {
			if (leaf())
				leaves.add(this);
			else {
				if (left != null)
					left.getLeavesAux(leaves);
				if (right != null)
					right.getLeavesAux(leaves);
			}
		}

		private boolean inside(Coord coord) {
			return coord.getX() < x2 && coord.getX() > x1 && coord.getY() < y2
					&& coord.getY() > y1;
		}

		private boolean leaf() {
			return left == null && right == null;
		}

		public void makeRooms(World world) {
			if (leaf()) {
				rx1 = dice.nextInt(x1 + 1, x2 - 1 - MIN_SIZE);
				rx2 = dice.nextInt(rx1 + MIN_SIZE, x2 - 1);
				ry1 = dice.nextInt(y1 + 1, y2 - 1 - MIN_SIZE);
				ry2 = dice.nextInt(ry1 + MIN_SIZE, y2 - 1);
				for (int x = rx1; x <= rx2; x++)
					for (int y = ry1; y <= ry2; y++)
						world.getTile(x, y).setTile('.', Color.white, true);
			} else {
				left.makeRooms(world);
				right.makeRooms(world);
			}
		}
	}

	private final Dice dice;

	private static final int MIN_SIZE = 3;

	protected Traditional() {
		dice = new Dice();
	}

	private void floodWithWall(World world, Rect rect) {
		for (int x = rect.xMin(); x < rect.xMax(); x++)
			for (int y = rect.yMin(); y < rect.yMax(); y++)
				world.getTile(x, y).setTile('#', Color.white, false);
	}

	public void generate(World world, long seed) {
		generate(world, seed, new Rect(world.width, world.height));
	}

	public void generate(World world, long seed, Rect rect) {
		dice.setSeed(seed);
		floodWithWall(world, rect);
		final BSP head = new BSP(rect);
		head.divide();
		head.makeRooms(world);
		head.connect(world);
		head.addCycles(world);
	}
}