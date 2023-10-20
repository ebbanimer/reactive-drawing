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

    /**
     * Initialize the frame layout and server connection.
     */
    public MainFrame() {
        initializeFrame();
        connectToServer();
    }

    /**
     * Initialize the layout of the frame.
     */
    private void initializeFrame() {
        this.setSize(1000, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(HEADER);
        this.setLayout(new BorderLayout());

        // Add an exit-window listener to shut down application.
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }

    /**
     * Display a connect panel with a connect button and an event listener.
     */
    private void connectToServer() {
        JButton connectButton = new JButton("Connect to Server");
        JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        connectPanel.add(connectButton);

        connectButton.addActionListener(e -> handleConnectButtonClick(connectPanel));

        this.add(connectPanel, BorderLayout.CENTER);
    }

    /**
     * If the socket is not initialized, establish a connection to the server.
     * @param connectPanel The connect panel.
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
     * @param connectPanel The connect panel.
     * @throws IOException IO-exception.
     */
    private void establishConnection(JPanel connectPanel) throws IOException {
        Disposable dp = Observable.fromCallable(() -> {
                    // Start a new socket.
                    serverSocket = new Socket("localhost", 12345);
                    JOptionPane.showMessageDialog(this, "Connected to the server!");

                    // Initialize streams and subscribe to incoming shapes.
                    initializeObjectInputStream();

                    // Invoke GUI components on the EDT.
                    SwingUtilities.invokeLater(() -> {
                        this.getContentPane().remove(connectPanel);  // remove connect-panel
                        try {
                            initializeDrawingPanelAndMenu();
                            subscribeToServerShapes();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        repaintFrame();
                    });

                    return true; // Return a result to onComplete.
                })
                .subscribeOn(Schedulers.io()) // Perform the work on a background thread.
                .observeOn(Schedulers.single()) // Observe the result on a single thread (UI)
                .subscribe(
                        result -> {},
                        Throwable::printStackTrace,
                        () -> {}
                );
    }

    /**
     * Initialize the object input stream for the socket to receive shapes from the server.
     */
    private void initializeObjectInputStream() {
        try {
            objectInputStream = new ObjectInputStream(serverSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to the observable that emits shapes received from the server.
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
     * Initialize the menu and drawing panel.
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
     * Initialize the object output stream to the drawing panel to send shapes to the server.
     * @throws IOException IO-exception.
     */
    private void initializeObjectOutputStream() throws IOException {
        drawingPanel.setObjectOutputStream(new ObjectOutputStream(serverSocket.getOutputStream()));
    }

    /**
     * Create a custom observable that emits shapes received from the server.
     * @return Observable emitting shapes.
     */
    private Observable<Shape> createServerShapesObservable() {
        return Observable.create(emitter -> {
            // Use subscribeOn to read shapes on a separate thread.
            Disposable disposable = Observable.fromCallable(() -> {
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
                        return true;
                    })
                    .subscribeOn(Schedulers.io()) // Read on IO thread.
                    .observeOn(Schedulers.single()) // Emit on a single thread (UI)
                    .subscribe(
                            result -> {}, // onNext not used here.
                            emitter::onError, // Pass errors to emitter.
                            emitter::onComplete // Notify completion.
                    );

            emitter.setCancellable(disposable::dispose); // Dispose the subscription on cancellation.
        });
    }

    /**
     * Handle the received shape by adding it to the drawing panel (or clear if it was a clear command).
     * @param shape Shape from the server.
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
     * If there is an error in subscribing to the shape observable.
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
     * Clean up any resources related to the server connection.
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
     * If the client exits the application, close input and output streams and socket connection.
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
