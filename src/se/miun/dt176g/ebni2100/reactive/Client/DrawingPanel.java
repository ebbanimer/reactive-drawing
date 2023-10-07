package se.miun.dt176g.ebni2100.reactive.Client;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import se.miun.dt176g.ebni2100.reactive.Client.Shapes.*;
import se.miun.dt176g.ebni2100.reactive.Client.Shapes.Rectangle;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.*;

/**
 * JPanel representing the drawing panel. It holds the canvas for drawing shapes and displaying them.
 * @author 	Ebba Nimér
 */

@SuppressWarnings("serial")
public class DrawingPanel extends JPanel {

    private Drawing drawing;
    private Point startPoint;

    private Shape currentShape; // Shape being drawn.
    private Color currentColor; // Selected color.
    private int currentThickness; // Selected thickness.
    private ShapeType lastSelectedShape = ShapeType.RECTANGLE; // Default shape.

    // Declare a Subject to multicast shapes
    private final PublishSubject<Shape> shapeSubject = PublishSubject.create();

    private ObjectOutputStream objectOutputStream; // Used to send shapes to the server

    /**
     * Initialize the drawing panel with properties and mouse events.
     * @param menu The menu for controlling drawing options.
     */
    public DrawingPanel(Menu menu) {
        initializeProperties(menu);
        initializeMouseEvents();
    }

    /**
     * Set the object output stream to communicate with the server.
     * @param outputStream The output stream.
     */
    public void setObjectOutputStream(ObjectOutputStream outputStream) {
        this.objectOutputStream = outputStream;
    }

    /**
     * Initialize default shape properties and subscribe to observables from the menu.
     * @param menu The menu for controlling drawing options.
     */
    private void initializeProperties(Menu menu) {
        currentColor = Constants.COLOR_RED;
        currentThickness = Constants.MEDIUM;
        currentShape = createShape(lastSelectedShape);
        drawing = new Drawing();
        startPoint = new Point(0, 0);

        // Subscribe to the shapeSubject.
        Disposable shapeSubscription = shapeSubject.subscribe(this::handleShapeEvent);

        // Subscribe to color, thickness, option, and shape observables
        Disposable colorDisposable = menu.getColorObservable().subscribe(this::updateColor);
        Disposable thicknessDisposable = menu.getThicknessObservable().subscribe(this::updateThickness);
        Disposable optionDisposable = menu.getOptionObservable().subscribe(this::handleOptions);

        // Use map to transform the emitted ShapeType from the observable in the Menu-class into a Shape object,
        // and then set the shape using corresponding method.
        Disposable shapeDisposable = menu.getShapeObservable()
                .map(this::createShape)
                .subscribe(this::setShapeAndRepaint);

    }

    /**
     * Initialize mouse-events.
     */
    private void initializeMouseEvents() {
        Disposable dp = createMouseEventObservable()
                .subscribe(event -> {
                    if (event.getID() == MouseEvent.MOUSE_PRESSED) {
                        handleMousePress(event);
                    } else if (event.getID() == MouseEvent.MOUSE_DRAGGED) {
                        handleMouseDrag(event);
                    } else if (event.getID() == MouseEvent.MOUSE_RELEASED) {
                        handleMouseRelease(event);
                    }
                });
    }

    /**
     * Create custom-observables for mouse-events, and attach the mouse-listeners to the panel.
     * @return Observable that emits mouse-events.
     */
    private Observable<MouseEvent> createMouseEventObservable() {
        return Observable.create(emitter -> {
            MouseAdapter listener = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    emitter.onNext(e);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    emitter.onNext(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    emitter.onNext(e);
                }
            };

            addMouseListener(listener);
            addMouseMotionListener(listener);

            emitter.setCancellable(() -> {
                removeMouseListener(listener);
                removeMouseMotionListener(listener);
            });
        });
    }

    /**
     * Create a shape based on provided ShapeType.
     * @param shapeType shape-type.
     * @return corresponding shape-object.
     */
    private Shape createShape(ShapeType shapeType) {
        lastSelectedShape = shapeType;  // store the selected shape-type.
        switch (shapeType) {
            case RECTANGLE:
                return new Rectangle(currentColor, currentThickness);
            case OVAL:
                return new Oval(currentColor, currentThickness);
            case STRAIGHT_LINE:
                return new StraightLine(currentColor, currentThickness);
            case FREEHAND:
                return new Freehand(currentColor, currentThickness);
            default:
                return currentShape; // Use the current shape as a fallback
        }
    }

    /**
     * Set the new shape.
     * @param newShape new shape.
     */
    private void setShapeAndRepaint(Shape newShape) {
        currentShape = newShape;
        repaint();
    }

    /**
     * Handle the chosen option from menu.
     * @param option option.
     */
    private void handleOptions(String option){
        if (option.equals("Clear")){
            // Create a Clear-object, emit it to shapeSubject and clear the canvas.
            Clear clearShape = new Clear(Color.WHITE, 0);
            shapeSubject.onNext(clearShape);
            clearShapes();
        }
    }

    /**
     * Send the provided shape to the server.
     * @param shape shape to send.
     */
    private void handleShapeEvent(Shape shape){
        if (objectOutputStream != null && shape != null) {
            try {
                // Send the shape through output-stream.
                objectOutputStream.writeObject(shape);
                objectOutputStream.reset();  // Reset the stream after sending shape.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add shape to Drawing.
     * @param shape shape to be added.
     */
    public void addShape(Shape shape){
        drawing.addShape(shape);
    }

    /**
     * Clear the canvas by emptying the shape-list.
     */
    public void clearShapes(){
        drawing.emptyShapes();
        repaint();
    }

    /**
     * Update chosen color.
     * @param selectedColor selected color.
     */
    private void updateColor(Color selectedColor) {
        currentColor = selectedColor;
        repaint();
    }

    /**
     * Update selected thickness.
     * @param selectedThickness new thickness.
     */
    private void updateThickness(int selectedThickness) {
        currentThickness = selectedThickness;
        repaint();
    }


    /**
     * Method that is triggered when the mouse is pressed.
     * @param event mouse-pressed event.
     */
    private void handleMousePress(MouseEvent event) {

        // Get coordinates where the mouse was pressed.
        int startX = event.getX();
        int startY = event.getY();

        // Update the start-point using corresponding method, and store the shape to variable.
        updateStartPoint(new Point(startX, startY));
        currentShape = createShape(lastSelectedShape);

        // Set the start-position and add the shape to the drawing.
        currentShape.setPosition(startX, startY);
        drawing.addShape(currentShape);

    }

    /**
     * Update the start-point.
     * @param point new start-point.
     */
    private void updateStartPoint(Point point) {
        startPoint = point;
    }

    /**
     * When mouse is being dragged, update the current shape's properties.
     * @param event mouse-drag.
     */
    private void handleMouseDrag(MouseEvent event) {
        if (currentShape != null) {
            if (currentShape instanceof Freehand) {
                Freehand freehand = (Freehand) currentShape;

                // Add a new point to the list.
                freehand.addPoint(new Point(event.getX(), event.getY()));
            } else if (currentShape instanceof StraightLine) {
                StraightLine straightLine = (StraightLine) currentShape;

                // Set new end-points of line.
                straightLine.setEndPoint(event.getX(), event.getY());
            } else {

                // If shape is oval or rectangle, set new position and size.
                int newX = Math.min(event.getX(), startPoint.x());
                int newY = Math.min(event.getY(), startPoint.y());
                int newWidth = Math.abs(event.getX() - startPoint.x());
                int newHeight = Math.abs(event.getY() - startPoint.y());

                currentShape.setPosition(newX, newY);
                currentShape.setSize(newWidth, newHeight);
            }
            repaint();
        }
    }

    /**
     * When mouse is being released, emit the final shape to shapeSubject.
     * @param event mouse-release.
     */
    private void handleMouseRelease(MouseEvent event) {
        shapeSubject.onNext(currentShape);
    }


    /**
     * Draw the shapes using drawing-class.
     * @param g graphics.
     */
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        drawing.draw(g);

    }

    /**
     * Get the output-stream.
     * @return output-stream.
     */
    public ObjectOutputStream getObjectOutputStream(){
        return objectOutputStream;
    }

}

