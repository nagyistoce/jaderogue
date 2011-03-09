package util;

import jade.util.Coord;
import jade.util.Direction;

public class Vector
{
    private static final double EPSILON = .000001;
    private double x;
    private double y;

    public Vector()
    {
        this(0, 0);
    }

    public Vector(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double x()
    {
        return x;
    }

    public double y()
    {
        return y;
    }

    public void add(Vector vector)
    {
        this.x += vector.x;
        this.y += vector.y;
    }

    public void multiply(double scalar)
    {
        this.x *= scalar;
        this.y *= scalar;
    }

    public void scale(double magnitude)
    {
        normalize();
        x *= magnitude;
        y *= magnitude;
    }

    public void normalize()
    {
        double normalization = magnitude();
        x /= normalization;
        y /= normalization;
    }

    public double magnitude()
    {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public Direction direction()
    {
        int dx = x < -EPSILON ? -1 : x > EPSILON ? 1 : 0;
        int dy = y < -EPSILON ? -1 : y > EPSILON ? 1 : 0;
        return new Coord().directionTo(dx, dy);
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }
}