package se.miun.dt176g.ebni2100.reactive.Client;

import java.awt.*;
import java.io.Serializable;

/**
 * Abstract class representing a basic shape with common properties.
 * Implements the Drawable and Serializable interfaces.
 */
public abstract class Shape implements Drawable, Serializable {

    private static final long serialVersionUID = 1L;

    // Common properties for all shapes
    private final Color color;
    private final int thickness;
    private int startX;
    private int startY;
    private int width;
    private int height;

    /**
     * Initializes a shape with a specified color and thickness.
     * @param color Color of the shape.
     * @param thickness Thickness of the shape's outline.
     */
    public Shape(Color color, int thickness) {
        this.color = color;
        this.thickness = thickness;
    }

    /**
     * Sets the size of the shape.
     * @param width Width of the shape.
     * @param height Height of the shape.
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the position of the shape.
     * @param x X-coordinate of the shape's starting point.
     * @param y Y-coordinate of the shape's starting point.
     */
    public void setPosition(int x, int y){
        this.startX = x;
        this.startY = y;
    }

    /**
     * Gets the X-coordinate of the shape's starting point.
     * @return X-coordinate.
     */
    protected int getX(){
        return startX;
    }

    /**
     * Gets the Y-coordinate of the shape's starting point.
     * @return Y-coordinate.
     */
    protected int getY(){
        return startY;
    }

    /**
     * Gets the width of the shape.
     * @return Width of the shape.
     */
    protected int getWidth(){
        return width;
    }

    /**
     * Gets the height of the shape.
     * @return Height of the shape.
     */
    protected int getHeight(){
        return height;
    }

    /**
     * Gets the thickness of the shape's outline.
     * @return Thickness of the outline.
     */
    protected int getThickness(){
        return thickness;
    }

    /**
     * Gets the color of the shape.
     * @return Color of the shape.
     */
    protected Color getColor(){
        return color;
    }

}
