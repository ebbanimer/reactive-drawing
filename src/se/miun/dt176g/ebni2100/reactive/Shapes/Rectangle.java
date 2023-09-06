package se.miun.dt176g.ebni2100.reactive.Shapes;

import se.miun.dt176g.ebni2100.reactive.Shape;

import java.awt.*;


/**
 * Represents the rectangle shape. Define its own draw-method for drawing the rectangle.
 * It needs to know the start-coordinates, width, height, and stop resizing when the mouse
 * is released.
 */

public class Rectangle extends Shape {


    public Rectangle(int x, int y, int width, int height, Color color, int thickness) {
        super(x, y, width, height, color, thickness);
    }


    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getColor()); // Set the color of the rectangle
        g2.setStroke(new BasicStroke(getThickness())); // Set the line thickness

        // Draw the rectangle using the inherited properties
        g2.drawRect(getX(), getY(), getWidth(), getHeight());
    }
}
