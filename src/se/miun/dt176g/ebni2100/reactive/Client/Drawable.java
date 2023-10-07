package se.miun.dt176g.ebni2100.reactive.Client;

/**
 * The Drawable interface represents objects that can be drawn using a Graphics context.
 */

@FunctionalInterface
interface Drawable {
    /**
     * Draws the object using the provided Graphics context.
     *
     * @param g Graphics context for drawing.
     */
    void draw(java.awt.Graphics g);
}
