package se.miun.dt176g.ebni2100.reactive.Shapes;

import se.miun.dt176g.ebni2100.reactive.Shape;
import se.miun.dt176g.ebni2100.reactive.Point;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class Freehand extends Shape {

    private final List<Point> points;

    public Freehand(int x, int y, int width, int height, Color color, int thickness) {
        super(x, y, width, height, color, thickness);
        this.points = new ArrayList<>();
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getColor());
        g2.setStroke(new BasicStroke(getThickness()));

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
