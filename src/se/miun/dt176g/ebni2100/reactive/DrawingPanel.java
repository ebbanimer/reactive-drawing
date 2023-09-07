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
    private final Color currentColor; // Store the selected color
    private final int currentThickness; // Store the selected thickness

    private Disposable mouseMotionDisposable;
    private Disposable mousePressDisposable;
    private Disposable mouseDragDisposable;
    private Disposable mouseReleaseDisposable;

    public DrawingPanel() {

        // Default values
        currentColor = Constants.COLOR_RED;
        currentThickness = Constants.MEDIUM;

        drawing = new Drawing();
        currentPoint = new Point(0, 0); // Initialize current point
        currentShape = null; // Initialize the current shape to null

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
            currentShape = new Rectangle(startX, startY, 0, 0, currentColor, currentThickness);
            drawing.addShape(currentShape);
        });

        mouseDragDisposable = createMouseDragObservable().subscribe(event -> {
            // Update the current shape's size or position using the provided MouseEvent
            if (currentShape != null) {
                int newWidth = Math.abs(event.getX() - currentShape.getX());
                int newHeight = Math.abs(event.getY() - currentShape.getY());
                currentShape.setSize(newWidth, newHeight);
                repaint(); // Redraw the panel with the updated shape size
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


    private void createMouseReleaseObservable(){

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

