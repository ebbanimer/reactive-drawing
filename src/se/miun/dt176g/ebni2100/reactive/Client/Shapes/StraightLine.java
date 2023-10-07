package se.miun.dt176g.ebni2100.reactive.Client.Shapes;

import se.miun.dt176g.ebni2100.reactive.Client.Shape;

import java.awt.*;

/**
 * Class representing the straight line. Extends shape.
 */
public class StraightLine extends Shape {
    private int endX;
    private int endY;

    /**
     * Initializes a straight-line shape with the given color and thickness.
     *
     * @param color Color of the shape.
     * @param thickness Thickness of the shape's outline.
     */
    public StraightLine(Color color, int thickness) {
        super(color, thickness);
    }

    /**
     * Define end-point of line.
     * @param x x-coordinate.
     * @param y y-coordinate.
     */
    public void setEndPoint(int x, int y){
        this.endX = x;
        this.endY = y;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getColor());
        g2.setStroke(new BasicStroke(getThickness()));

        // Draw the straight line using the inherited properties
        g2.drawLine(getX(), getY(), endX, endY);
    }
}
