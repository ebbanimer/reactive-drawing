package se.miun.dt176g.ebni2100.reactive;


/**
 * <h1>Shape</h1> Abstract class which derived classes builds on.
 * <p>
 * This class consists of the attributes common to all geometric shapes.
 * Specific shapes are based on this class.
 *
 * @author 	Ebba Nimér
 * @version 1.0
 * @since 	2022-09-08
 */

import java.awt.*;

/**
 *
 */
public abstract class Shape implements Drawable {

    // private member : some container storing coordinates
    private final Color color;
    private final int thickness;
    private int startX;
    private int startY;
    private int width;
    private int height;

    public Shape(int x, int y, int width, int height, Color color, int thickness) {
        this.startX = x;
        this.startY = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.thickness = thickness;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setPosition(int x, int y){
        this.startX = x;
        this.startY = y;
    }

    protected int getX(){
        return startX;
    }

    protected int getY(){
        return startY;
    }

    protected int getWidth(){
        return width;
    }

    protected int getHeight(){
        return height;
    }

    protected int getThickness(){
        return thickness;
    }

    protected Color getColor(){
        return color;
    }

}
