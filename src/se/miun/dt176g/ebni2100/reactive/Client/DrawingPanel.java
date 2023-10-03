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

    private Shape currentShape; // Store the shape being drawn
    private Color currentColor; // Store the selected color
    private int currentThickness; // Store the selected thickness
    private ShapeType lastSelectedShape = ShapeType.RECTANGLE; // default

    private Disposable mouseMotionDisposable;
    private Disposable mousePressDisposable;
    private Disposable mouseDragDisposable;
    private Disposable mouseReleaseDisposable;

    private Disposable colorDisposable;
    private Disposable thicknessDisposable;
    private Disposable shapeDisposable;
    private Disposable optionDisposable;

    // Declare a Subject to multicast shapes
    private final PublishSubject<Shape> shapeSubject = PublishSubject.create();

    private ObjectOutputStream objectOutputStream; // Used to send shapes to the server


    public DrawingPanel(Menu menu) {

        initializeProperties(menu);
        initializeMouseEvents();

    }

    public void setObjectOutputStream(ObjectOutputStream outputStream) {
        this.objectOutputStream = outputStream;
    }

    public void addShape(Shape shape){
        drawing.addShape(shape);
    }

    private void initializeProperties(Menu menu) {
        currentColor = Constants.COLOR_RED;
        currentThickness = Constants.MEDIUM;
        currentShape = createShape(lastSelectedShape);
        drawing = new Drawing();
        startPoint = new Point(0, 0);
        currentPoint = new Point(0, 0);

        // Subscribe to color, thickness, and shape observables
        colorDisposable = menu.getColorObservable().subscribe(this::updateColor);
        thicknessDisposable = menu.getThicknessObservable().subscribe(this::updateThickness);

        // Use map to transform the emitted ShapeType from the observable in the Menu-class into a Shape object,
        // and then set the shape using corresponding method.
        shapeDisposable = menu.getShapeObservable()
                .map(this::createShape)
                .subscribe(this::setShapeAndRepaint);

        optionDisposable = menu.getOptionObservable().subscribe(this::handleOptions);
    }

    private void initializeMouseEvents() {
        // Subscribe to the corresponding observables, and handle the event.
        mouseMotionDisposable = createMouseMotionObservable().subscribe(this::handleMouseMotion);
        mousePressDisposable = createMousePressObservable().subscribe(this::handleMousePress);
        mouseDragDisposable = createMouseDragObservable().subscribe(this::handleMouseDrag);
        mouseReleaseDisposable = createMouseReleaseObservable().subscribe(this::handleMouseRelease);
    }

    private Shape createShape(ShapeType shapeType) {
        lastSelectedShape = shapeType;
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

    private void setShapeAndRepaint(Shape newShape) {
        currentShape = newShape;
        repaint();
    }

    private void handleOptions(String option){
        if (option.equals("Clear")){
            drawing.emptyShapes();
            repaint();
        }
    }

    private void updateColor(Color selectedColor) {
        currentColor = selectedColor;
        repaint();
    }

    private void updateThickness(int selectedThickness) {
        currentThickness = selectedThickness;
        repaint();
    }

    private void handleMouseMotion(MouseEvent event) {
        Point newPoint = new Point(event.getX(), event.getY());
        updateCurrentPoint(newPoint);
        repaint();
    }

    private void updateCurrentPoint(Point newPoint) {
        currentPoint = newPoint;
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

    // Add this method to send the shape to the server
    private void sendShapeToServer() {
        if (objectOutputStream != null && currentShape != null) {
            try {
                objectOutputStream.writeObject(currentShape);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateStartPoint(Point point) {
        startPoint = point;
    }

    private void handleMouseDrag(MouseEvent event) {
        if (currentShape != null) {
            if (currentShape instanceof Freehand) {
                Freehand freehand = (Freehand) currentShape;
                freehand.addPoint(new Point(event.getX(), event.getY()));
            } else if (currentShape instanceof StraightLine) {
                StraightLine straightLine = (StraightLine) currentShape;
                straightLine.setEndPoint(event.getX(), event.getY());
            } else {
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

    private void handleMouseRelease(MouseEvent event) {
        // Call the existing method to send the shape to the server
        sendShapeToServer();
    }

    private Observable<MouseEvent> createMouseMotionObservable(){
        return Observable.create(emitter -> {
            MouseMotionAdapter listener = new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    emitter.onNext(e);
                }
            };

            addMouseMotionListener(listener);
            // Cleanup when the observable is disposed (e.g., panel removal)
            emitter.setCancellable(() -> removeMouseMotionListener(listener));
        });
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

            // Cleanup when the observable is disposed (e.g., panel removal)
            emitter.setCancellable(() -> removeMouseListener(listener));
        });
    }


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

            // Cleanup when the observable is disposed (e.g., panel removal)
            emitter.setCancellable(() -> removeMouseMotionListener(listener));
        });
    }

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

            // Cleanup when the observable is disposed (e.g., panel removal)
            emitter.setCancellable(() -> removeMouseListener(listener));
        });
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        drawing.draw(g);

    }

}

