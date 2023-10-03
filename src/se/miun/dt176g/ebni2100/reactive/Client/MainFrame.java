package se.miun.dt176g.ebni2100.reactive.Client;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private static final String HEADER = "Reactive Paint";
    private DrawingPanel drawingPanel;
    private Menu menu;
    private Socket serverSocket;
    private Disposable serverShapesSubscription;
    private ObjectInputStream objectInputStream;
    // Add this field to your class
    private final ExecutorService inputStreamExecutor = Executors.newSingleThreadExecutor();


    public MainFrame() {
        initializeFrame();
        connectAndDraw();
    }

    private void initializeFrame() {
        this.setSize(1000, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(HEADER);
        this.setLayout(new BorderLayout());
    }

    private void connectAndDraw() {
        JButton connectButton = new JButton("Connect to Server");
        JPanel connectPanel = createConnectPanel(connectButton);

        connectButton.addActionListener(e -> handleConnectButtonClick(connectButton, connectPanel));

        this.add(connectPanel, BorderLayout.CENTER);
    }

    private JPanel createConnectPanel(JButton connectButton) {
        JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        connectPanel.add(connectButton);
        return connectPanel;
    }

    private void handleConnectButtonClick(JButton connectButton, JPanel connectPanel) {
        if (serverSocket == null || serverSocket.isClosed()) {
            try {
                System.out.println("Attempting to establish connection...");
                establishConnection(connectButton, connectPanel);
            } catch (IOException ex) {
                handleConnectionFailure(ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Already connected to the server.");
        }
    }

    private void establishConnection(JButton connectButton, JPanel connectPanel) throws IOException {
        try {
            System.out.println("Connecting to the server...");
            serverSocket = new Socket("localhost", 12345);
            JOptionPane.showMessageDialog(this, "Connected to the server!");

            System.out.println("Connected successfully. Starting initialization...");

            // Start a new thread for network operations
            new Thread(() -> {
                initializeObjectInputStream();
                try {
                    subscribeToServerShapes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            SwingUtilities.invokeLater(() -> {
                removeConnectPanel(connectPanel);
                try {
                    initializeDrawingPanelAndMenu();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                repaintFrame();
            });
        } catch (IOException e) {
            System.err.println("Error establishing connection: " + e.getMessage());
            e.printStackTrace();
            throw e; // rethrow the exception to handle it in the calling method
        }
    }


    private void initializeObjectInputStream() {
        try {
            System.out.println("Initializing ObjectInputStream...");
            objectInputStream = new ObjectInputStream(serverSocket.getInputStream());
            System.out.println("ObjectInputStream initialized successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void subscribeToServerShapes() throws IOException {
        serverShapesSubscription = createServerShapesObservable()
                .observeOn(Schedulers.io())
                .subscribe(
                        this::handleReceivedShape,
                        this::handleSubscriptionError,
                        () -> System.out.println("subscribeToServerShapes completed")
                );
    }

    private void removeConnectPanel(JPanel connectPanel) {
        System.out.println("removing");
        this.getContentPane().remove(connectPanel);
    }

    private void initializeDrawingPanelAndMenu() throws IOException {
        System.out.println("Initializing drawing panel and menu");
        menu = new Menu(this);
        drawingPanel = new DrawingPanel(menu);
        drawingPanel.setBounds(0, 0, getWidth(), getHeight());

        initializeObjectOutputStream();

        this.getContentPane().add(drawingPanel, BorderLayout.CENTER);
        this.setJMenuBar(menu);
    }

    private void initializeObjectOutputStream() throws IOException {
        drawingPanel.setObjectOutputStream(new ObjectOutputStream(serverSocket.getOutputStream()));
    }

    // Create an observable for receiving shapes from the server
    private Observable<Shape> createServerShapesObservable() {
        return Observable.create(emitter -> {
            inputStreamExecutor.submit(() -> {
                while (true) {
                    try {
                        Shape receivedShape = (Shape) objectInputStream.readObject();
                        emitter.onNext(receivedShape);
                    } catch (EOFException e) {
                        break;
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            });
        });
    }

    private void handleReceivedShape(Shape shape) {
        System.out.println("Received shape: " + shape);
        drawingPanel.addShape(shape);
        drawingPanel.repaint();
    }

    private void handleSubscriptionError(Throwable throwable) {
        System.err.println("Error in subscribeToServerShapes: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    private void handleConnectionFailure(IOException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to connect to the server.");
    }

    private void repaintFrame() {
        this.validate();
        this.repaint();
    }
}
