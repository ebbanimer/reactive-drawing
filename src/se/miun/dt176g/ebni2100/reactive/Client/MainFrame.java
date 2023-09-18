package se.miun.dt176g.ebni2100.reactive.Client;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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

        // Creates all necessary objects and adds them to the MainFrame (just one object right now)
        menu = new Menu(this);
        drawingPanel = new DrawingPanel(menu);
        drawingPanel.setBounds(0, 0, getWidth(), getHeight());
        this.getContentPane().add(drawingPanel, BorderLayout.CENTER);

        this.setJMenuBar(menu);

        JButton connectButton = new JButton("Connect to Server");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverSocket == null || serverSocket.isClosed()) {
                    try {
                        // Establish a connection to the server (replace with your server's IP and port)
                        serverSocket = new Socket("localhost", 12345);
                        JOptionPane.showMessageDialog(MainFrame.this, "Connected to the server!");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(MainFrame.this, "Failed to connect to the server.");
                    }
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "Already connected to the server.");
                }
            }
        });

        // Add the connect button to the frame
        this.add(connectButton, BorderLayout.NORTH);

    }
}
