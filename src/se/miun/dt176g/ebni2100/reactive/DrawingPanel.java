package se.miun.dt176g.ebni2100.reactive;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import se.miun.dt176g.ebni2100.reactive.Shapes.Freehand;
import se.miun.dt176g.ebni2100.reactive.Shapes.Oval;
import se.miun.dt176g.ebni2100.reactive.Shapes.Rectangle;
import se.miun.dt176g.ebni2100.reactive.Shapes.StraightLine;

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
    private Menu menu;
    private Point startPoint;
    private Point currentPoint;
    private Point endPoint;
    private Observable<MouseEvent> mouseEventObservable;
    private Disposable mouseEventDisposable;

    private Shape currentShape; // Store the shape being drawn
    private Color currentColor; // Store the selected color
    private int currentThickness; // Store the selected thickness
    private String lastSelectedShape = Constants.RECTANGLE; // Default shape


    private Disposable mouseMotionDisposable;
    private Disposable mousePressDisposable;
    private Disposable mouseDragDisposable;
    private Disposable mouseReleaseDisposable;
    private Disposable colorDisposable;
    private Disposable thicknessDisposable;
    private Disposable shapeDisposable;

    public DrawingPanel(Menu menu) {

        // Default values
        currentColor = Constants.COLOR_RED;
        currentThickness = Constants.MEDIUM;
        currentShape = new Rectangle(currentColor, currentThickness);

        drawing = new Drawing();
        this.menu = menu;
        startPoint = new Point(0, 0);  // Initialize start-point.
        currentPoint = new Point(0, 0); // Initialize current point

        // Subscribe to the color observable to update the current color
        colorDisposable = menu.getColorObservable().subscribe(selectedColor -> {
            currentColor = selectedColor;
            repaint(); // Redraw the panel with the updated color
        });

        thicknessDisposable = menu.getThicknessObservable().subscribe(selectedThickness -> {
            currentThickness = selectedThickness;
            repaint();
        });

        shapeDisposable = menu.getShapeObservable().subscribe(selectedShape -> {
            lastSelectedShape = selectedShape; // Update the last selected shape type
            switch (selectedShape) {
                case Constants.RECTANGLE: currentShape = new Rectangle(currentColor, currentThickness); break;
                case Constants.OVAL: currentShape = new Oval(currentColor, currentThickness); break;
                case Constants.STRAIGHT_LINE: currentShape = new StraightLine(currentColor, currentThickness); break;
                case Constants.FREEHAND: currentShape = new Freehand(currentColor, currentThickness); break;
            }
            repaint();
        });


        mouseMotionDisposable = createMouseMotionObservable()
                .map(event -> new Point(event.getX(), event.getY()))
                .doOnNext(newPoint -> {
                    currentPoint.x(newPoint.x()); // Update the x-coordinate of Point
                    currentPoint.y(newPoint.y()); // Update the y-coordinate of Point
                    repaint(); // Trigger a repaint to update the mouse point on the panel
                })
                .subscribe();

        mousePressDisposable = createMousePressObservable().subscribe(event -> {
            // Handle the mouse press event here
            int startX = event.getX();
            int startY = event.getY();
            startPoint.x(startX);
            startPoint.y(startY);

            // Create an instance of the last selected shape type
            switch (lastSelectedShape) {
                case Constants.RECTANGLE: currentShape = new Rectangle(currentColor, currentThickness); break;
                case Constants.OVAL: currentShape = new Oval(currentColor, currentThickness); break;
                case Constants.STRAIGHT_LINE: currentShape = new StraightLine(currentColor, currentThickness); break;
                case Constants.FREEHAND: currentShape = new Freehand(currentColor, currentThickness); break;
            }
            currentShape.setPosition(startX, startY);
            drawing.addShape(currentShape);
        });


        mouseDragDisposable = createMouseDragObservable().subscribe(event -> {
            // Update the current line's position and size using the provided MouseEvent
            if (currentShape != null) {
                if (currentShape instanceof Freehand) {
                    Freehand freehand = (Freehand) currentShape;
                    freehand.addPoint(new Point(event.getX(), event.getY()));
                    repaint(); // Redraw the panel with the updated shape
                } else {
                    int newX = Math.min(event.getX(), startPoint.x());
                    int newY = Math.min(event.getY(), startPoint.y());
                    int newWidth = Math.abs(event.getX() - startPoint.x());
                    int newHeight = Math.abs(event.getY() - startPoint.y());

                    currentShape.setPosition(newX, newY);
                    currentShape.setSize(newWidth, newHeight);

                    repaint(); // Redraw the panel with the updated line position and size
                }

            }
        });

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


    private Observable<MouseEvent> createMouseReleaseObservable(){
        return Observable.create(emitter -> {
            MouseAdapter listener = new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    // Emit the mouse drag event
                    emitter.onNext(e);
                }
            };

            addMouseListener(listener);

            // Cleanup when the observable is disposed (e.g., panel removal)
            emitter.setCancellable(() -> removeMouseListener(listener));
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

        // Draw a dot at the current mouse position
        g.setColor(currentColor);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(currentThickness));
        int x = currentPoint.x() - currentThickness / 2;
        int y = currentPoint.y() - currentThickness / 2;
        g2d.fillOval(x, y, currentThickness, currentThickness);

    }

}

