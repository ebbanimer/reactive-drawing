package se.miun.dt176g.ebni2100.reactive.Client;

import java.awt.*;
import java.io.Serializable;

/**
 * Shape class that has common properties for all derived shapes.
 */
public abstract class Shape implements Drawable, Serializable {

    private static final long serialVersionUID = 1L;

    private final Color color;
    private final int thickness;
    private int startX;
    private int startY;
    private int width;
    private int height;

    /**
     * Initialize shape with color and thickness.
     * @param color color of shape.
     * @param thickness thickness of shape.
     */
    public Shape(Color color, int thickness) {
        this.color = color;
        this.thickness = thickness;
    }

    /**
     * Set the size of shape.
     * @param width width.
     * @param height height.
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Set the position of shape.
     * @param x x-coordinate.
     * @param y y-coordinate.
     */
    public void setPosition(int x, int y){
        this.startX = x;
        this.startY = y;
    }

    public int getX(){
        return startX;
    }

    public int getY(){
        return startY;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int getThickness(){
        return thickness;
    }

    public Color getColor(){
        return color;
    }

}
