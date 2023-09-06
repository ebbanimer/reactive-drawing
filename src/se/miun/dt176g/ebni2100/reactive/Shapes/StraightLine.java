package se.miun.dt176g.ebni2100.reactive.Shapes;

import se.miun.dt176g.ebni2100.reactive.Shape;

import java.awt.*;

public class StraightLine extends Shape {
    public StraightLine(int x, int y, int width, int height, Color color, int thickness) {
        super(x, y, width, height, color, thickness);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.

        // Draw using g2.
        // eg g2.fillOval(int x, int y, int width, int height)
    }
}
