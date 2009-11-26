package jade.util;

public enum Direction {
	N(0, -1), E(1, 0), S(0, 1), W(-1, 0);
	public int dx;
	public int dy;

	private Direction(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public Direction next() {
		return (Direction) ExtraTools
				.next(values(), ordinal(), values().length);
	}

	public Direction prev() {
		return (Direction) ExtraTools
				.prev(values(), ordinal(), values().length);
	}

	public static Direction deltaToDirect(int dx, int dy) {
		dx = ExtraTools.normalize(dx);
		dy = ExtraTools.normalize(dy);
		
		for (Direction d : Direction.values())
			if (d.dx == dx && d.dy == dy)
				return d;
		throw new IllegalArgumentException("Invalid direction: (" + dx + ", "
				+ dy + ")");
	}

}