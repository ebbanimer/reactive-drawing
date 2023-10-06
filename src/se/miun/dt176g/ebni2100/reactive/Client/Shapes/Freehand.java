package se.miun.dt176g.ebni2100.reactive.Client.Shapes;

import se.miun.dt176g.ebni2100.reactive.Client.Shape;
import se.miun.dt176g.ebni2100.reactive.Client.Point;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

/**
 * Class represents freehand. Extends Shape.
 */
public class Freehand extends Shape {

    // Points in the line to be displayed.
    private final List<Point> points;

    /**
     * Initialize shape with given color and thickness, and initialize points.
     * @param color color of shape.
     * @param thickness thickness of shape.
     */
    public Freehand(Color color, int thickness) {
        super(color, thickness);
        this.points = new ArrayList<>();
    }

    /**
     * Add point to list.
     * @param point point.
     */
    public void addPoint(Point point) {
        points.add(point);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getColor());
        g2.setStroke(new BasicStroke(getThickness()));

        // Go through all points in list and draw a connecting line.
        if (points.size() > 1) {
            Point prevPoint = points.get(0);

            for (int i = 1; i < points.size(); i++) {
                Point currentPoint = points.get(i);
                g2.drawLine(prevPoint.x(), prevPoint.y(), currentPoint.x(), currentPoint.y());
                prevPoint = currentPoint;
            }
        }
    }
}
