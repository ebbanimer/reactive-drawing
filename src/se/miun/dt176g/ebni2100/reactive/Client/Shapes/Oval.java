package se.miun.dt176g.ebni2100.reactive.Client.Shapes;

import se.miun.dt176g.ebni2100.reactive.Client.Shape;

import java.awt.*;

/**
 * Class representing an oval shape. Extends Shape.
 */
public class Oval extends Shape {

    /**
     * Initializes an oval shape with the given color and thickness.
     *
     * @param color Color of the shape.
     * @param thickness Thickness of the shape's outline.
     */
    public Oval(Color color, int thickness) {
        super(color, thickness);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(getColor());
        g2.setStroke(new BasicStroke(getThickness()));

        // Draw the rectangle using the inherited properties.
        g2.fillOval(getX(), getY(), getWidth(), getHeight());
    }
}
