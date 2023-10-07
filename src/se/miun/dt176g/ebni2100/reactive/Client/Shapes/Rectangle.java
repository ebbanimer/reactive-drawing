package se.miun.dt176g.ebni2100.reactive.Client.Shapes;

import se.miun.dt176g.ebni2100.reactive.Client.Shape;

import java.awt.*;


/**
 * Represents the rectangle shape. Extends shape.
 */
public class Rectangle extends Shape {

    /**
     * Initializes a rectangle shape with the given color and thickness.
     *
     * @param color Color of the shape.
     * @param thickness Thickness of the shape's outline.
     */
    public Rectangle(Color color, int thickness) {
        super(color, thickness);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getColor());
        g2.setStroke(new BasicStroke(getThickness()));

        // Draw the straight line using the inherited properties.
        g2.drawRect(getX(), getY(), getWidth(), getHeight());
    }

}
