package se.miun.dt176g.ebni2100.reactive.Client.Shapes;

import se.miun.dt176g.ebni2100.reactive.Client.Shape;

import java.awt.*;


/**
 * Represents the rectangle shape.
 */
public class Rectangle extends Shape {

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
