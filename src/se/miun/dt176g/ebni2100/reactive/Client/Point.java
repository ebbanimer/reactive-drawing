package se.miun.dt176g.ebni2100.reactive.Client;

import java.io.Serializable;

/**
 * Class representing a point.
 * @author 	Ebba Nimér
 */

public class Point implements Serializable {

    private final int x;
    private final int y;

    /**
     * Initalize point with coordinates.
     * @param x x-coordinate.
     * @param y y-coordinate.
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        Point p = (Point) o;
        return (x == p.x() && y == p.y());
    }

    @Override
    public String toString() {
        return "["+x+","+y+"]";
    }

}

