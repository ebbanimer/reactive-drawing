package se.miun.dt176g.ebni2100.reactive.Client.Shapes;

import se.miun.dt176g.ebni2100.reactive.Client.Shape;

import java.awt.*;

public class StraightLine extends Shape {
    private int endX;
    private int endY;

    public StraightLine(Color color, int thickness) {
        super(color, thickness);
    }

    public void setEndPoint(int x, int y){
        this.endX = x;
        this.endY = y;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getColor()); // Set the color of the line
        g2.setStroke(new BasicStroke(getThickness())); // Set the line thickness

        g2.drawLine(getX(), getY(), endX, endY);

        // Draw the straight line using the inherited properties
        //g2.drawLine(getX(), getY(), getX() + getWidth(), getY() + getHeight());
    }
}
