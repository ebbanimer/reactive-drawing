package se.miun.dt176g.ebni2100.reactive.Server;

import se.miun.dt176g.ebni2100.reactive.Client.Shape;

import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the main frame for the server-side drawing application.
 * Displays incoming shapes received from connected clients.
 */
public class ServerDrawingFrame extends JFrame {

    // List to store incoming shapes from connected clients
    private List<Shape> incomingShapes = new ArrayList<>();
    private final JPanel shapeDisplayPanel;

    /**
     * Initializes the server drawing frame.
     * Sets up the frame properties and creates a custom panel for shape display.
     */
    public ServerDrawingFrame() {
        setTitle("Server Drawing");
        this.setSize(1000, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a custom panel to display shapes
        shapeDisplayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Shape shape : incomingShapes){
                    shape.draw(g);
                }
            }
        };
        shapeDisplayPanel.setBounds(0, 0, getWidth(), getHeight());
        // Add the drawing panel to the frame's CENTER region
        this.getContentPane().add(shapeDisplayPanel, BorderLayout.CENTER);
    }

    /**
     * Updates the list of incoming shapes and triggers a repaint of the shape display panel.
     * @param shapes List of incoming shapes received from connected clients.
     */
    public void updateIncomingShapes(List<Shape> shapes) {
        incomingShapes = shapes;
        shapeDisplayPanel.repaint();
        shapeDisplayPanel.revalidate();
    }

}
