package se.miun.dt176g.ebni2100.reactive.Server;

import se.miun.dt176g.ebni2100.reactive.Client.Shape;

import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;
import java.util.List;

public class ServerDrawingFrame extends JFrame {

    private List<Shape> incomingShapes = new ArrayList<>();
    private final JPanel shapeDisplayPanel;

    public ServerDrawingFrame() {
        setTitle("Server Drawing");
        // default window-size.
        this.setSize(1000, 800);
        // application closes when the "x" in the upper-right corner is clicked.
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

    // Inside the updateIncomingShapes method
    public void updateIncomingShapes(List<Shape> shapes) {
        incomingShapes = shapes;
        shapeDisplayPanel.repaint();
        // Additionally, revalidate the panel to ensure proper resizing
        shapeDisplayPanel.revalidate();
    }

}
