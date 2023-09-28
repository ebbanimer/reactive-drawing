package se.miun.dt176g.ebni2100.reactive.Client;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.*;

/**
 * <h1>MainFrame</h1>
 * JFrame to contain the rest
 *
 * @author 	Ebba Nimér
 * @version 1.0
 * @since 	2022-09-08
 */

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private String header;
    private DrawingPanel drawingPanel;
    private Menu menu;
    private Socket serverSocket; // Added to hold the server connection


    public MainFrame() {

        // default window-size.
        this.setSize(1200, 900);
        // application closes when the "x" in the upper-right corner is clicked.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.header = "Reactive Paint";
        this.setTitle(header);

        // Changes layout from default to BorderLayout
        this.setLayout(new BorderLayout());

        connectAndDrawing();

    }

    private void connectAndDrawing(){
        // TODO get past drawings from server
        JButton connectButton = new JButton("Connect to Server");
        // Add the connect button to the frame
        this.add(connectButton, BorderLayout.CENTER);

        JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        connectPanel.add(connectButton);

        // Add the connect panel to the frame
        this.add(connectPanel, BorderLayout.CENTER);

        connectButton.addActionListener(e -> {
            if (serverSocket == null || serverSocket.isClosed()) {
                try {
                    // Establish a connection to the server (replace with your server's IP and port)
                    serverSocket = new Socket("localhost", 12345);
                    JOptionPane.showMessageDialog(MainFrame.this, "Connected to the server!");


                    // Remove the connect panel
                    this.getContentPane().remove(connectPanel);

                    // Create the drawing panel and menu
                    menu = new Menu(this);
                    drawingPanel = new DrawingPanel(menu);
                    drawingPanel.setBounds(0, 0, getWidth(), getHeight());


                    // Initialize the ObjectOutputStream for sending shapes to the server
                    drawingPanel.setObjectOutputStream(new ObjectOutputStream(serverSocket.getOutputStream()));

                    // Receive previously stored shapes from the server
                    /*ObjectInputStream objectInputStream = new ObjectInputStream(serverSocket.getInputStream());
                    Object receivedObject;
                    while ((receivedObject = objectInputStream.readObject()) != null) {
                        if (receivedObject instanceof Shape) {
                            Shape receivedShape = (Shape) receivedObject;
                            // Add the received shape to the local drawing
                            drawingPanel.addShape(receivedShape);
                        }
                    }*/

                    // Add the drawing panel to the frame's CENTER region
                    this.getContentPane().add(drawingPanel, BorderLayout.CENTER);

                    // Set the menu
                    this.setJMenuBar(menu);

                    // Repaint the frame
                    this.validate();
                    this.repaint();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "Failed to connect to the server.");
                }
            } else {
                JOptionPane.showMessageDialog(MainFrame.this, "Already connected to the server.");
            }
        });
    }
}
