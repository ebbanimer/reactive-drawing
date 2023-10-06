package se.miun.dt176g.ebni2100.reactive.Client;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;

/**
 * MainFrame containing menu, drawing-panel, and handling connection to the server.
 * @author 	Ebba Nimér
 */

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private static final String HEADER = "Reactive Paint";
    private DrawingPanel drawingPanel;
    private Socket serverSocket;
    private ObjectInputStream objectInputStream;
    private final ExecutorService inputStreamExecutor = Executors.newSingleThreadExecutor();

    /**
     * Initialize the frame-layout and server-connection.
     */
    public MainFrame() {
        initializeFrame();
        connectToServer();
    }

    /**
     * Initialize layout.
     */
    private void initializeFrame() {
        this.setSize(1000, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(HEADER);
        this.setLayout(new BorderLayout());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }

    /**
     * Display connect-panel with connect-button, with event-listener.
     */
    private void connectToServer() {
        JButton connectButton = new JButton("Connect to Server");
        JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        connectPanel.add(connectButton);

        connectButton.addActionListener(e -> handleConnectButtonClick(connectPanel));

        this.add(connectPanel, BorderLayout.CENTER);
    }

    /**
     * If socket is yet not initialized, establish a connection to the server.
     * @param connectPanel connect-panel.
     */
    private void handleConnectButtonClick(JPanel connectPanel) {
        if (serverSocket == null || serverSocket.isClosed()) {
            try {
                establishConnection(connectPanel);
            } catch (IOException ex) {
                handleConnectionFailure(ex); // If fail, handle failure.
            }
        } else {
            JOptionPane.showMessageDialog(this, "Already connected to the server.");
        }
    }

    /**
     * Establish the connection to the server.
     * @param connectPanel connect-panel.
     * @throws IOException IO-exception.
     */
    private void establishConnection(JPanel connectPanel) throws IOException {
        try {
            // Start a new socket.
            serverSocket = new Socket("localhost", 12345);
            JOptionPane.showMessageDialog(this, "Connected to the server!");

            // Start a new thread for network operations
            new Thread(() -> {
                initializeObjectInputStream();
                try {
                    subscribeToServerShapes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Invoke GUI components on the EDT.
            SwingUtilities.invokeLater(() -> {
                this.getContentPane().remove(connectPanel);  // remove connect-panel
                try {
                    initializeDrawingPanelAndMenu();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                repaintFrame();
            });
        } catch (IOException e) {
            e.printStackTrace();
            throw e; // rethrow the exception to handle it in the calling method
        }
    }

    /**
     * Initialize the object-input-stream for socket, in order to receive shapes from server.
     */
    private void initializeObjectInputStream() {
        try {
            objectInputStream = new ObjectInputStream(serverSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to the server in order to listen for shapes.
     * @throws IOException IO-exception.
     */
    private void subscribeToServerShapes() throws IOException {
        Disposable serverShapesSubscription = createServerShapesObservable()
                .observeOn(Schedulers.io())
                .subscribe(
                        this::handleReceivedShape,  // gets called when a shape is received.
                        this::handleSubscriptionError,   // if error during subscription
                        this::handleServerDisconnect   // when observable is completed (socket closed)
                );
    }

    /**
     * Initialize menu and drawing-panel.
     * @throws IOException IO-exception.
     */
    private void initializeDrawingPanelAndMenu() throws IOException {
        Menu menu = new Menu();
        drawingPanel = new DrawingPanel(menu);
        drawingPanel.setBounds(0, 0, getWidth(), getHeight());

        initializeObjectOutputStream();  // initialize output-stream.

        this.getContentPane().add(drawingPanel, BorderLayout.CENTER);
        this.setJMenuBar(menu);
    }

    /**
     * Initialize object-output-stream to drawing-panel, in order to send shapes to the server.
     * @throws IOException IO-exception.
     */
    private void initializeObjectOutputStream() throws IOException {
        drawingPanel.setObjectOutputStream(new ObjectOutputStream(serverSocket.getOutputStream()));
    }

    /**
     * Create a custom-observable that emits shapes received from the server. Uses ExecutorService to
     * continuously read shapes in a non-blocking way.
     * @return observable emitting shapes.
     */
    private Observable<Shape> createServerShapesObservable() {

        // Open a new thread for continuously reading shapes from the server.
        return Observable.create(emitter -> inputStreamExecutor.submit(() -> {
            while (true) {
                try {
                    if (serverSocket.isClosed()) {
                        // Socket is closed, complete the observable.
                        emitter.onComplete();
                        break;
                    }

                    // Read shape from input-stream.
                    Shape receivedShape = (Shape) objectInputStream.readObject();
                    emitter.onNext(receivedShape);  // emit the received shape.
                } catch (SocketException | EOFException e) {
                    // Handle SocketException when the socket is closed
                    emitter.onComplete();
                    break;
                } // End of stream or if class was not found.
                catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    emitter.onError(e);
                    break;
                }
            }
        }));
    }

    /**
     * Handle the received shape by adding it to the drawing-panel (or clear if it was a clear-command).
     * @param shape shape from server.
     */
    private void handleReceivedShape(Shape shape) {
        if (shape instanceof Clear){
            drawingPanel.clearShapes();
        } else {
            drawingPanel.addShape(shape);
            drawingPanel.repaint();
        }
    }

    /**
     * If error in subscribing to shape-observable.
     */
    private void handleSubscriptionError(Throwable throwable) {
        System.err.println("Error in subscribeToServerShapes: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    /**
     * If the connection to the server failed.
     */
    private void handleConnectionFailure(IOException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to connect to the server.");
    }

    /**
     * Clean up any resources related to the server connection
     */
    private void handleServerDisconnect() {
        try {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * If client exists application, close input- and output-streams and socket-connection.
     */
    private void handleExit() {
        try {
            if (drawingPanel.getObjectOutputStream() != null) {
                drawingPanel.getObjectOutputStream().close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Repaint the frame.
     */
    private void repaintFrame() {
        this.validate();
        this.repaint();
    }
}
