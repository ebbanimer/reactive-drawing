package se.miun.dt176g.ebni2100.reactive;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import se.miun.dt176g.ebni2100.reactive.Shapes.*;
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
    private Observable<MouseEvent> mouseEventObservable;
    private Disposable mouseEventDisposable;

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

    public DrawingPanel(Menu menu) {

        initializeProperties(menu);
        initializeMouseEvents();
        
        /*mouseMotionDisposable = createMouseMotionObservable()
                .map(event -> new Point(event.getX(), event.getY()))
                .doOnNext(newPoint -> {
                    currentPoint.x(newPoint.x()); // Update the x-coordinate of Point
                    currentPoint.y(newPoint.y()); // Update the y-coordinate of Point
                    repaint(); // Trigger a repaint to update the mouse point on the panel
                })
                .subscribe();*/

    }

    private void initializeProperties(Menu menu) {
        currentColor = Constants.COLOR_RED;
        currentThickness = Constants.MEDIUM;
        setShape(lastSelectedShape);
        drawing = new Drawing();
        startPoint = new Point(0, 0);
        currentPoint = new Point(0, 0);

        // Subscribe to color, thickness, and shape observables
        colorDisposable = menu.getColorObservable().subscribe(this::updateColor);
        thicknessDisposable = menu.getThicknessObservable().subscribe(this::updateThickness);
        shapeDisposable = menu.getShapeObservable().subscribe(this::updateShape);
    }

    private void initializeMouseEvents() {
        mouseMotionDisposable = createMouseMotionObservable().subscribe(this::handleMouseMotion);
        mousePressDisposable = createMousePressObservable().subscribe(this::handleMousePress);
        mouseDragDisposable = createMouseDragObservable().subscribe(this::handleMouseDrag);
    }

    private void updateColor(Color selectedColor) {
        currentColor = selectedColor;
        repaint();
    }

    private void updateThickness(int selectedThickness) {
        currentThickness = selectedThickness;
        repaint();
    }

    private void updateShape(ShapeType selectedShape) {
        lastSelectedShape = selectedShape;
        setShape(selectedShape);
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

    private void handleMousePress(MouseEvent event) {
        int startX = event.getX();
        int startY = event.getY();
        updateStartPoint(new Point(startX, startY));
        setShape(lastSelectedShape);
        currentShape.setPosition(startX, startY);
        drawing.addShape(currentShape);
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

    private void setShape(ShapeType shapeType) {
        switch (shapeType) {
            case RECTANGLE:
                currentShape = new Rectangle(currentColor, currentThickness);
                break;
            case OVAL:
                currentShape = new Oval(currentColor, currentThickness);
                break;
            case STRAIGHT_LINE:
                currentShape = new StraightLine(currentColor, currentThickness);
                break;
            case FREEHAND:
                currentShape = new Freehand(currentColor, currentThickness);
                break;
        }
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





    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        drawing.draw(g);

        // Draw a dot at the current mouse position
        /*g.setColor(currentColor);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(currentThickness));
        int x = currentPoint.x() - currentThickness / 2;
        int y = currentPoint.y() - currentThickness / 2;
        g2d.fillOval(x, y, currentThickness, currentThickness);*/

    }

}

