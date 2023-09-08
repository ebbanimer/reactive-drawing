package se.miun.dt176g.ebni2100.reactive.Shapes;

import se.miun.dt176g.ebni2100.reactive.Shape;

import java.awt.*;

public class StraightLine extends Shape {
    public StraightLine(Color color, int thickness) {
        super(color, thickness);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getColor()); // Set the color of the line
        g2.setStroke(new BasicStroke(getThickness())); // Set the line thickness

        // Draw the straight line using the inherited properties
        g2.drawLine(getX(), getY(), getX() + getWidth(), getY() + getHeight());
    }
}
