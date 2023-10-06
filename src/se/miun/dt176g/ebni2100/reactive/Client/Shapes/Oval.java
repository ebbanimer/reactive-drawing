package se.miun.dt176g.ebni2100.reactive.Client.Shapes;

import se.miun.dt176g.ebni2100.reactive.Client.Shape;

import java.awt.*;

/**
 * Class representing the shape Oval.
 */
public class Oval extends Shape {

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
