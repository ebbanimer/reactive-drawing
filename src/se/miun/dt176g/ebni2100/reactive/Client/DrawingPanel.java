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
 * JPanel which represent the drawingpanel. It holds the canvas for drawing the shapes, and
 * displaying shapes.
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
     * Initialize drawing-panel with properties and mouse-events.
     * @param menu menu.
     */
    public DrawingPanel(Menu menu) {
        initializeProperties(menu);
        initializeMouseEvents();
    }

    /**
     * Set the object-output-stream to communicate to the server.
     * @param outputStream output-stream.
     */
    public void setObjectOutputStream(ObjectOutputStream outputStream) {
        this.objectOutputStream = outputStream;
    }

    /**
     * Initialize default shape-properties and subscribe to observables from the menu.
     * @param menu menu.
     */
    private void initializeProperties(Menu menu) {
        currentColor = Constants.COLOR_RED;
        currentThickness = Constants.MEDIUM;
        currentShape = createShape(lastSelectedShape);
        drawing = new Drawing();
        startPoint = new Point(0, 0);

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
        // Subscribe to the corresponding observables, and handle the event.
        Disposable mousePressDisposable = createMousePressObservable().subscribe(this::handleMousePress);
        Disposable mouseDragDisposable = createMouseDragObservable().subscribe(this::handleMouseDrag);
        Disposable mouseReleaseDisposable = createMouseReleaseObservable().subscribe(this::handleMouseRelease);
    }

    /**
     * Create shape based on provided ShapeType.
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
            // Create a Clear-object, send it to the server and clear the canvas.
            Clear clearShape = new Clear(Color.WHITE, 0);
            shapeSubject.onNext(clearShape);
            sendShapeToServer(clearShape);
            clearShapes();
        }
    }

    /**
     * Add shape to drawing.
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

        // Publish the shape to the subject
        shapeSubject.onNext(currentShape);

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
     * When mouse is being released, send the final shape to the server.
     * @param event mouse-release.
     */
    private void handleMouseRelease(MouseEvent event) {
        sendShapeToServer(currentShape);
    }

    /**
     * Send the shape to the server.
     * @param shape final drawn shape.
     */
    private void sendShapeToServer(Shape shape) {
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
     * Attach a mouse-pressed listener and emit the MouseEvent object.
     * @return custom observable for mouse-press.
     */
    private Observable<MouseEvent> createMousePressObservable() {
        return Observable.create(emitter -> {
            MouseAdapter listener = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // Emit the mouse press event
                    emitter.onNext(e);
                }
            };

            addMouseListener(listener);

            // Cleanup when the observable is disposed.
            emitter.setCancellable(() -> removeMouseListener(listener));
        });
    }

    /**
     * Attach a mouse-drag listener and emit the MouseEvent object.
     * @return custom observable for mouse-drag.
     */
    private Observable<MouseEvent> createMouseDragObservable() {
        return Observable.create(emitter -> {
            MouseAdapter listener = new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    // Emit the mouse drag event
                    emitter.onNext(e);
                }
            };

            addMouseMotionListener(listener);

            // Cleanup when the observable is disposed.
            emitter.setCancellable(() -> removeMouseMotionListener(listener));
        });
    }

    /**
     * Attach a mouse-release listener and emit the MouseEvent object.
     * @return custom observable for mouse-release.
     */
    private Observable<MouseEvent> createMouseReleaseObservable() {
        return Observable.create(emitter -> {
            MouseAdapter listener = new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    // Emit the mouse release event
                    emitter.onNext(e);
                }
            };

            addMouseListener(listener);

            // Cleanup when the observable is disposed.
            emitter.setCancellable(() -> removeMouseListener(listener));
        });
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
     * Get output-stream.
     * @return output-stream.
     */
    public ObjectOutputStream getObjectOutputStream(){
        return objectOutputStream;
    }

}

