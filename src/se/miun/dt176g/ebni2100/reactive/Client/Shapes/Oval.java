package se.miun.dt176g.ebni2100.reactive.Client.Shapes;

import se.miun.dt176g.ebni2100.reactive.Client.Shape;

import java.awt.*;

public class Oval extends Shape {

    public Oval(Color color, int thickness) {
        super(color, thickness);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.

        g2.setColor(getColor()); // Set the color of the rectangle
        g2.setStroke(new BasicStroke(getThickness())); // Set the line thickness

        // Draw the rectangle using the inherited properties
        g2.fillOval(getX(), getY(), getWidth(), getHeight());
    }
}