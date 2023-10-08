package se.miun.dt176g.ebni2100.reactive.Server;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import se.miun.dt176g.ebni2100.reactive.Client.Clear;
import se.miun.dt176g.ebni2100.reactive.Client.Shape;

/**
 * Represents the server that facilitates communication between clients, allowing them to share and draw
 * shapes collaboratively.
 *
 * This class initializes a server socket, manages client connections, and handles the communication of
 * shapes between the server and connected clients.
 *
 * The server maintains a list of shapes received from clients and uses a ReplaySubject to buffer and emit
 * shapes to subscribers. Each connected client has its own output stream to receive shapes from the server.
 *
 * It also provides methods to send shapes to clients, observe the stream of
 * shapes, and perform cleanup when clients disconnect.
 *
 * @author Ebba Nimér
 */
public class DrawingServer {

    // List to store shapes received from clients
    private static final List<Shape> shapes = new ArrayList<>();

    // Use ReplaySubject to buffer emitted shapes.
    private static final ReplaySubject<Shape> shapesSubject = ReplaySubject.create();
    private static ServerDrawingFrame serverMainFrame;

    // Container holding output-streams to each client.
    private static final ConcurrentMap<Socket, ObjectOutputStream> outputStreams = new ConcurrentHashMap<>();

    /**
     * Handles the main logic for client connections, setting up the server socket,
     * and initiating the handling of client connections.
     *
     * @param args Command-line arguments.
     * @throws IOException If an I/O error occurs while setting up the server socket.
     */
    public static void main(String[] args) throws IOException {
        int portNumber = 12345;
        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("Server is running. Waiting for client connections...");

        // Initialize the server frame.
        serverMainFrame = new ServerDrawingFrame();
        serverMainFrame.setVisible(true);

        // Handle incoming client connections.
        handleClientConnections(serverSocket);

    }

    /**
     * Opens a server socket and continuously accepts incoming client connections.
     * For each connection, a new observable is created to handle the specific client's
     * operations asynchronously.
     *
     * @param serverSocket The server socket.
     */
    private static void handleClientConnections(ServerSocket serverSocket) {
        // Observable for accepting client connections.
        Observable<Socket> clientConnectionsObservable = Observable.create(emitter -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    emitter.onNext(clientSocket);
                } catch (IOException e) {
                    emitter.onError(e);
                    break;
                }
            }
        });

        // Subscribe to the observable and handle each client connection asynchronously.
        Disposable dp = clientConnectionsObservable
                .subscribeOn(Schedulers.io())
                .flatMap(DrawingServer::handleClient) // Use flatMap to handle asynchronous operations for each client.
                .subscribe(
                        innerSocket -> System.out.println("Subscription completed for socket: " + innerSocket),
                        throwable -> {
                            System.err.println("Error in main subscription: " + throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );

    }

    /**
     * Handles a specific client connection by setting up an object output stream,
     * subscribing to the shapeSubject, and initiating the listening process for
     * incoming shapes from the client.
     *
     * @param clientSocket The client socket.
     * @return An observable emitting the client socket.
     */
    private static Observable<Socket> handleClient(Socket clientSocket) {
        System.out.println("Client connected: " + clientSocket.getInetAddress());

        try {
            // Open object output-stream and add to map.
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStreams.put(clientSocket, objectOutputStream);

            // Subscribe to shapeSubject for the client and listen to emitted shapes.
            Disposable dp = shapesSubject
                    .observeOn(Schedulers.io())
                    .subscribe(shape -> sendShapeToClient(clientSocket, shape), // send emitted shape to client.
                            throwable -> {
                                System.err.println("Error sending shapes to the client: " + throwable.getMessage());
                                throwable.printStackTrace();
                            },
                            () -> {
                                // Cleanup when the observable is completed (client disconnected).
                                cleanupOnClientDisconnect(clientSocket);
                            });


        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return an observable emitting the client socket and execute logic for listening for shapes.
        return Observable.just(clientSocket)
                .observeOn(Schedulers.io())
                .doOnNext(DrawingServer::listenForShapes);
    }

    /**
     * Listens for incoming shapes from a specific client by setting up an object input stream.
     * This method runs on a separate thread for each client.
     *
     * @param socket The client socket.
     */
    private static void listenForShapes(Socket socket) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            while (true) {
                try {
                    // Get the shape sent from client.
                    Shape receivedShape = (Shape) objectInputStream.readObject();

                    // If it's a clear command, clear shapes.
                    if (receivedShape instanceof Clear) {
                        shapes.clear();
                    } else {
                        // Otherwise, add shape to list.
                        shapes.add(receivedShape);
                    }

                    // Emit the shape and update the server-frame.
                    shapesSubject.onNext(receivedShape);
                    serverMainFrame.updateIncomingShapes(shapes);
                } catch (EOFException e) {
                    // EOFException indicates that the client has closed the connection
                    System.out.println("Client disconnected: " + socket.getInetAddress());
                    cleanupOnClientDisconnect(socket);
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace(); // Log other exceptions
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Log exceptions related to socket creation
        }
    }

    /**
     * Sends a shape to a specific client using the corresponding object output stream.
     *
     * @param clientSocket The client socket.
     * @param shape The shape to be sent.
     */
    private static void sendShapeToClient(Socket clientSocket, Shape shape) {
        // Get the relevant output-stream for the client.
        ObjectOutputStream objectOutputStream = outputStreams.get(clientSocket);
        if (objectOutputStream != null) {
            try {
                objectOutputStream.writeObject(shape);  // send shape.
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Cleans up resources when a client disconnects by removing it from the output-stream map
     * and closing the associated object output stream.
     *
     * @param clientSocket The client socket.
     */
    private static void cleanupOnClientDisconnect(Socket clientSocket) {
        ObjectOutputStream objectOutputStream = outputStreams.remove(clientSocket);
        if (objectOutputStream != null) {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}