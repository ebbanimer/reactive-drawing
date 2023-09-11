package se.miun.dt176g.ebni2100.reactive;


import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;


/**
 * <h1>Drawing</h1>
 * Let this class store an arbitrary number of AbstractShape-objects in
 * some kind of container.
 *
 * @author 	Ebba Nimér
 * @version 1.0
 * @since 	2022-09-08
 */


public class Drawing implements Drawable {


    private List<Shape> shapes;

    public Drawing(){
        shapes = new ArrayList<>();
    }


    /**
     * <h1>addShape</h1> add a shape to the "SomeContainer shapes"
     *
     * @param s a {@link Shape} object.
     */
    public void addShape(Shape s) {
        shapes.add(s);
    }

    public void emptyShapes(){
        shapes.clear();
    }


    @Override
    public void draw(Graphics g) {

        for (Shape shape : shapes) {
            shape.draw(g);
        }
    }

}

