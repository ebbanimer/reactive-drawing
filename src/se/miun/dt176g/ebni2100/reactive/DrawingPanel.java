package se.miun.dt176g.ebni2100.reactive;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import se.miun.dt176g.ebni2100.reactive.Shapes.Rectangle;

import java.awt.*;
import java.awt.event.MouseAdapter;
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
    private Point endPoint;
    private Observable<MouseEvent> mouseEventObservable;
    private Disposable mouseEventDisposable;

    private Shape currentShape; // Store the shape being drawn
    private Color currentColor; // Store the selected color
    private int currentThickness; // Store the selected thickness

    private Disposable mousePressDisposable;
    private Disposable mouseDragDisposable;
    private Disposable mouseReleaseDisposable;

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

        drawing = new Drawing();
        currentPoint = new Point(0, 0); // Initialize current point
        currentShape = null; // Initialize the current shape to null

        // Create Observables for mouse events using custom Point objects
        Observable<Point> mousePressEventObservable = createMouseEventObservable(MouseEvent.MOUSE_PRESSED);
        Observable<Point> mouseDragEventObservable = createMouseEventObservable(MouseEvent.MOUSE_DRAGGED);
        Observable<Point> mouseReleaseEventObservable = createMouseEventObservable(MouseEvent.MOUSE_RELEASED);

        // Subscribe to mouse events using chaining operators
        mousePressDisposable = mousePressEventObservable.subscribe(this::onMousePressed);
        mouseDragDisposable = mouseDragEventObservable.subscribe(this::onMouseDragged);
        mouseReleaseDisposable = mouseReleaseEventObservable.subscribe(this::onMouseReleased);

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

    // Implement event handlers for mouse press, drag, and release
    private void onMousePressed(Point point) {
        // Handle mouse press event here
        // Set the start point and create a new shape, e.g., currentShape = new Rectangle(...);
        startPoint = new Point(point.x(), point.y());
        currentShape = new Rectangle(startPoint.x(), startPoint.y(), 0, 0, currentColor, currentThickness);
    }

    private void onMouseDragged(Point point) {
        // Handle mouse drag event here
        // Update the shape's size or position using the provided Point
        if (currentShape != null) { // Check if a shape is being resized
            int newWidth = Math.abs(point.x() - currentShape.getX());
            int newHeight = Math.abs(point.y() - currentShape.getY());
            currentShape.setSize(newWidth, newHeight); // Update the size
            repaint(); // Redraw the panel with the updated shape size
        }
    }

    private void onMouseReleased(Point point) {
        // Handle mouse release event here
        // Finalize the shape's size or position using the provided Point
        if (currentShape != null) {
            // Finalize the resizing
            // Add the finalized shape to your drawing container (e.g., drawing.addShape(currentShape))
            currentShape = null; // Stop resizing by setting currentShape to null
            repaint(); // Repaint to display the drawn shape
        }
    }

    // Create an Observable for a specific mouse event type (press, drag, or release)
    private Observable<Point> createMouseEventObservable(int mouseEventType) {
        return Observable.create(emitter -> {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Emit Point objects for the specified mouse event type
                    if (e.getID() == mouseEventType) {
                        emitter.onNext(new Point(e.getX(), e.getY()));
                    }
                }
            });
        });
    }


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

