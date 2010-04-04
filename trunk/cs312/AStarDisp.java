package cs312;

import jade.core.Console;
import jade.core.World;
import jade.path.Path;
import jade.util.type.Coord;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class AStarDisp implements Path
{
	private Map<Coord, Node> nodes;
	private Console console;

	public AStarDisp(Console console)
	{
		nodes = new TreeMap<Coord, Node>();
		this.console = console;
	}

	public boolean hasPath(World world, Coord start, Coord goal)
	{
		return getPath(world, start, goal) != null;
	}

	public List<Coord> getPath(World world, Coord start, Coord goal)
	{
		nodes.clear();
		Set<Node> closed = new TreeSet<Node>();
		TreeSet<Node> open = new TreeSet<Node>();
		open.add(getNode(start));
		getNode(start).gScore = 0;
		getNode(start).hScore = hEstimate(start, goal, world);
		getNode(start).fScore = getNode(start).hScore;
		while(!open.isEmpty())
		{
			Node x = open.first();
			if(x.coord.equals(goal))
				return SHOWRECUONSTRUCTEDPATH(reconstructPath(x));
			SHOWPATHSEARCH(x);
			open.remove(x);
			closed.add(x);
			for(Node y : getAdjacentNodes(x, world))
			{
				if(closed.contains(y))
					continue;
				double tentativeGScore = x.gScore + dist(x, y);
				boolean tentativeIsBetter = false;
				if(!open.contains(y))
				{
					open.add(y);
					y.hScore = hEstimate(y.coord, goal, world);
					tentativeIsBetter = true;
				}
				else if(tentativeGScore < y.gScore)
					tentativeIsBetter = true;
				if(tentativeIsBetter)
				{
					open.remove(y);
					y.cameFrom = x;
					y.gScore = tentativeGScore;
					y.fScore = y.gScore + y.hScore;
					open.add(y);
				}
			}
		}
		return null;
	}

	private List<Coord> SHOWRECUONSTRUCTEDPATH(List<Coord> path)
	{
		for(Coord coord: path)
			console.buffChar(coord, '*', Color.red);
		console.refreshScreen();
		return path;
	}

	private void SHOWPATHSEARCH(Node x)
	{
		console.buffChar(x.coord, '*', Color.blue);
		console.refreshScreen();
		try
		{
			Thread.sleep(10);
		}
		catch(InterruptedException e)
		{
		}
	}

	/**
	 * This method is the heuristic estimate used to optimize A*. In other words,
	 * it is the likley hood that a square will be on the shortest path between
	 * two nodes. By default, it just calculates the distance from the c1 to c2,
	 * but could be overridden to factor in other cost in node traversal.
	 */
	protected double hEstimate(Coord c1, Coord c2, World world)
	{
		return c1.distTo(c2);
	}

	private Set<Node> getAdjacentNodes(Node node, World world)
	{
		Set<Node> adjacent = new TreeSet<Node>();
		adjacent.add(getNode(new Coord(node.coord.x() + 1, node.coord.y())));
		adjacent.add(getNode(new Coord(node.coord.x() - 1, node.coord.y())));
		adjacent.add(getNode(new Coord(node.coord.x(), node.coord.y() - 1)));
		adjacent.add(getNode(new Coord(node.coord.x(), node.coord.y() + 1)));
		adjacent.add(getNode(new Coord(node.coord.x() - 1, node.coord.y() - 1)));
		adjacent.add(getNode(new Coord(node.coord.x() - 1, node.coord.y() + 1)));
		adjacent.add(getNode(new Coord(node.coord.x() + 1, node.coord.y() - 1)));
		adjacent.add(getNode(new Coord(node.coord.x() + 1, node.coord.y() + 1)));
		Set<Node> nonpassable = new TreeSet<Node>();
		for(Node n : adjacent)
			if(!world.passable(n.coord.x(), n.coord.y()))
				nonpassable.add(n);
		adjacent.removeAll(nonpassable);
		return adjacent;
	}

	private List<Coord> reconstructPath(Node current)
	{
		if(current.cameFrom != null)
		{
			List<Coord> path = reconstructPath(current.cameFrom);
			path.add(current.coord);
			return path;
		}
		return new LinkedList<Coord>();
	}

	private double dist(Node n1, Node n2)
	{
		return n1.coord.distTo(n2.coord);
	}

	private Node getNode(Coord coord)
	{
		Node result = nodes.get(coord);
		if(result == null)
		{
			result = new Node(coord);
			nodes.put(coord, result);
		}
		return result;
	}

	private class Node implements Comparable<Node>
	{
		private final Coord coord;
		private double gScore;
		private double hScore;
		private double fScore;
		private Node cameFrom;

		public Node(Coord coord)
		{
			this.coord = coord;
		}
		
		public boolean equals(Object obj)
		{
			return super.equals(obj);
		}
		
		public int compareTo(Node other)
		{
			if(fScore < other.fScore)
				return -1;
			else if(fScore > other.fScore)
				return 1;
			else
				return coord.compareTo(other.coord);
		}
	}
}
