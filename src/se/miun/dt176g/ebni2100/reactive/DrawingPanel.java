package se.miun.dt176g.ebni2100.reactive;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

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
    private Point mousePoint;
    Observable<MouseEvent> mouseEventObservable;
    private Disposable mouseMotionDisposable; // Store the disposable here
    int pointMedium;


    public DrawingPanel() {
        drawing = new Drawing();

        mousePoint = new Point(0, 0); // Initialize with default values

        // Observable for mouse motion events
        mouseEventObservable = Observable.create(emitter -> {
            MouseMotionListener listener = new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    emitter.onNext(e); // Emit mouse motion events
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    emitter.onNext(e); // Emit mouse motion events
                }
            };

            addMouseMotionListener(listener);

            // Cleanup when the observable is disposed (e.g., panel removal)
            emitter.setCancellable(() -> removeMouseMotionListener(listener));
        });

        // Subscribe and store the disposable
        mouseMotionDisposable = mouseEventObservable.subscribe(event -> {
            pointMedium = 7; // Adjust the size of the point as needed
            mousePoint = new Point(event.getX(), event.getY()); // Update the mouse point
            repaint(); // Trigger a repaint to update the mouse point on the panel
        });


    }

    public Observable<MouseEvent> getMouseMotionObservable() {
        return mouseEventObservable;
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

        // Draw the mouse point using the most recent Graphics object
        //int pointSize = 7; // Adjust the size of the point as needed
        g.setColor(Color.RED); // Set the color of the point
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2)); // Set medium thickness
        int x = mousePoint.x() - pointMedium / 2;
        int y = mousePoint.y() - pointMedium  / 2;
        g2d.fillOval(x, y, pointMedium , pointMedium );

        // Subscribe to the mouse motion event
        /*mouseEventObservable.subscribe(event -> {
            int pointSize = 7; // Adjust the size of the point as needed
            mousePoint = new Point(event.getX(), event.getY()); // Update the mouse point
            repaint(); // Trigger a repaint to update the mouse point on the panel

            g.setColor(Color.RED); // Set the color of the point
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(2)); // Set medium thickness
            int x = mousePoint.x() - pointSize / 2;
            int y = mousePoint.y() - pointSize / 2;
            g2d.fillOval(x, y, pointSize, pointSize);
        });*/
    }
}

