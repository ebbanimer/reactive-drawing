package se.miun.dt176g.ebni2100.reactive;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import se.miun.dt176g.ebni2100.reactive.Shapes.Rectangle;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.*;

/**
 * <h1>DrawingPanel</h1> Creates a Canvas-object for displaying all graphics
 * already drawn.
 *
 * @author 	Ebba Nimér
 * @version 1.0
 * @since 	2022-09-08
 */

@SuppressWarnings("serial")
public class DrawingPanel extends JPanel {

    private Drawing drawing;
    private Point startPoint;
    private Point currentPoint;
    private Observable<MouseEvent> mouseEventObservable;
    private Disposable mouseEventDisposable;

    private Shape currentShape; // Store the shape being drawn
    private Color currentColor; // Store the selected color
    private int currentThickness; // Store the selected thickness


    /*private final Point mousePoint;
    int pointSize;
    private Rectangle selectedRectangle; // Use rectangle as default
    private Observable<Integer> thicknessObservable;
    private Observable<Color> colorObservable;
    private Disposable thicknessDisposable; // Store the disposable*/


    public DrawingPanel() {

        // Default values
        currentColor = Constants.COLOR_RED;
        currentThickness = Constants.MEDIUM;
        currentShape = new Rectangle(0, 0, 0, 0, currentColor, currentThickness);

        drawing = new Drawing();
        startPoint = new Point(0, 0);  // Initialize start point
        currentPoint = new Point(0, 0); // Initialize current point

        mouseEventObservable = Observable.create(emitter -> {

        });

        //mousePoint = new Point(0, 0);  // default values
        //pointSize = 7;   // default size (medium)

        // Get the observables from the Menu class (assuming you have a reference to Menu)
        //this.thicknessObservable = menu.getThicknessObservable();
        //this.colorObservable = menu.getColorObservable();

        // Subscribe to the observables
        //subscribeToThicknessObservable();
        //subscribeToColorObservable();

        // Create an observable for mouse point updates
        /*mouseEventObservable = Observable.create(emitter -> {
            MouseMotionListener listener = new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    emitter.onNext(e);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    emitter.onNext(e);
                }
            };

            addMouseMotionListener(listener);

            // Cleanup when the observable is disposed (e.g., panel removal)
            emitter.setCancellable(() -> removeMouseMotionListener(listener));
        });*/

        // Use chaining operators to handle mouse events and update the mouse point
        // convert mouse event to a Point
        // Update the x-coordinate of Point
        // Update the y-coordinate of Point
        // Trigger a repaint to update the mouse point on the panel
        /*Disposable mouseMotionDisposable = mouseEventObservable
                .map(event -> new Point(event.getX(), event.getY())) // convert mouse event to a Point
                .doOnNext(newPoint -> {
                    mousePoint.x(newPoint.x()); // Update the x-coordinate of Point
                    mousePoint.y(newPoint.y()); // Update the y-coordinate of Point
                    repaint(); // Trigger a repaint to update the mouse point on the panel
                })
                .subscribe(); // Subscribe to the observable*/
    }

    /*private void subscribeToThicknessObservable() {
        thicknessDisposable = thicknessObservable.subscribe(thickness -> {
            // Handle thickness selection here
            // For example, set the thickness based on the selected value
            if (Constants.BIG == thickness) {
                pointSize = Constants.BIG;
            } else if (Constants.MEDIUM == thickness) {
                pointSize = Constants.MEDIUM;
            } else if (Constants.SMALL == thickness) {
                pointSize = Constants.SMALL;
            }
            // Handle other cases as needed
        });
    }*/

    /*private void subscribeToColorObservable() {
        colorObservable.subscribe(color -> {
            // Handle color selection here
            // For example, set the color based on the selected value
            // You can use the 'color' object directly to set the drawing color
        });
    }

    public Observable<MouseEvent> getMouseMotionObservable() {
        return mouseEventObservable;
    }*/

    public void redraw() {
        repaint();
    }

    public void setDrawing(Drawing d) {
        drawing = d;
        repaint();
    }

    public Drawing getDrawing() {
        return drawing;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        drawing.draw(g);

        /*// Draw the mouse point using the most recent Graphics object
        g.setColor(Color.RED); // Set the color of the point
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2)); // Set medium thickness
        int x = mousePoint.x() - pointSize / 2;
        int y = mousePoint.y() - pointSize / 2;
        g2d.fillOval(x, y, pointSize, pointSize);*/

    }
}

