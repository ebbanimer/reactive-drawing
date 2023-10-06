package se.miun.dt176g.ebni2100.reactive.Client;


import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;


/**
 * Class that handles the drawings.
 * @author 	Ebba Nimér
 */

public class Drawing implements Drawable {

    private final List<Shape> shapes;

    public Drawing(){
        shapes = new ArrayList<>();
    }

    /**
     * Add shape to the list.
     * @param s shape.
     */
    public void addShape(Shape s) {
        shapes.add(s);
    }

    /**
     * Empty shape-list.
     */
    public void emptyShapes(){
        shapes.clear();
    }

    /**
     * For each shape, call its draw-method.
     * @param g graphics.
     */
    @Override
    public void draw(Graphics g) {
        for (Shape shape : shapes) {
            shape.draw(g);
        }
    }

}

