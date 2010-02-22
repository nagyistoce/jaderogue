package jade.path;

import jade.core.World;
import jade.util.type.Coord;
import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of Bresenham's Line Drawing Algorithm to find straight
 * paths. This algorithm is also useful in ray casting field of vision.
 */
public class Bresenham implements Path
{
	protected Bresenham()
	{
	}

	public List<Coord> getPath(World world, Coord start, Coord goal)
	{
		final List<Coord> path = castray(world, start, goal);
		return path.get(path.size() - 1).equals(goal) ? path : null;
	}

	public boolean hasPath(World world, Coord start, Coord goal)
	{
		return getPath(world, start, goal) != null;
	}

	protected List<Coord> castray(World world, Coord start, Coord goal)
	{
		return castray(world, start.x(), start.y(), goal.x(), goal.y());
	}

	protected List<Coord> castray(World world, int x1, int y1, int x2, int y2)
	{
		List<Coord> path = new LinkedList<Coord>();
		path.add(new Coord(x1, y1));
		int dx = Math.abs(x2 - x1) << 1;
		int dy = Math.abs(y2 - y1) << 1;
		int ix = x2 > x1 ? 1 : -1;
		int iy = y2 > y1 ? 1 : -1;
		if(dx >= dy)
		{
			int error = dy - (dx >> 1);
			while(x1 != x2)
			{
				if(error >= 0 && (error != 0 || ix > 0))
				{
					y1 += iy;
					error -= dx;
				}
				x1 += ix;
				error += dy;
				path.add(new Coord(x1, y1));
				if(!world.passable(x1, y1))
					break;
			}
		}
		else
		// dx < dy
		{
			int error = dx - (dy >> 1);
			while(y1 != y2)
			{
				if(error >= 0 && (error != 0 || iy > 0))
				{
					x1 += ix;
					error -= dy;
				}
				y1 += iy;
				error += dx;
				path.add(new Coord(x1, y1));
				if(!world.passable(x1, y1))
					break;
			}
		}
		return path;
	}// end castray
}
