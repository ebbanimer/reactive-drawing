package se.miun.dt176g.ebni2100.reactive.Client;

import java.awt.*;

/**
 * <h1>ConcreteShape</h1> Creates a Circle-object.
 * Concrete class which extends Shape.
 * In other words, this class represents ONE type of shape
 * i.e. a circle, rectangle, n-sided regular polygon (if that's your thing)
 *
 * @author 	Ebba Nimér
 * @version 1.0
 * @since 	2022-09-08
 */

public class ConcreteShape extends Shape {

    public ConcreteShape(Color color, int thickness) {
        super(color, thickness);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.

        // Draw using g2.
        // eg g2.fillOval(int x, int y, int width, int height)
    }

}
