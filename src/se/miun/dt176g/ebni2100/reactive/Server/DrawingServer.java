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
 * Class representing the server.
 * @author Ebba Nimér
 */
public class DrawingServer {

    private static final List<Shape> shapes = new ArrayList<>();

    // Use ReplaySubject to buffer emitted shapes.
    private static final ReplaySubject<Shape> shapesSubject = ReplaySubject.create();
    private static final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static ServerDrawingFrame serverMainFrame;

    // Container holding output-streams to each client.
    private static final ConcurrentMap<Socket, ObjectOutputStream> outputStreams = new ConcurrentHashMap<>();

    /**
     * Initialize the socket, server-frame, and set up client connections.
     * @param args args
     * @throws IOException IO-exception.
     */
    public static void main(String[] args) throws IOException {
        int portNumber = 12345;
        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("Server is running. Waiting for client connections...");

        serverMainFrame = new ServerDrawingFrame();
        serverMainFrame.setVisible(true);

        handleClientConnections(serverSocket);

    }

    /**
     * Open connection to clients.
     * @param serverSocket server-socket
     */
    private static void handleClientConnections(ServerSocket serverSocket) {
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

        compositeDisposable.add(
                clientConnectionsObservable
                        .subscribeOn(Schedulers.io())
                        .flatMap(DrawingServer::handleClient)
                        .subscribe(
                                innerSocket -> System.out.println("Subscription completed for socket: " + innerSocket),
                                throwable -> {
                                    System.err.println("Error in main subscription: " + throwable.getMessage());
                                    throwable.printStackTrace();
                                }
                        )
        );

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Open an output-stream to client to send objects.
     * @param clientSocket client-socket
     * @return observable that emits the client-socket.
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
                                outputStreams.remove(clientSocket);
                                // Close the ObjectOutputStream here if needed
                                // objectOutputStream.close();
                            });

            // Add the disposable to the compositeDisposable container.
            compositeDisposable.add(dp);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Observable.just(clientSocket)
                .observeOn(Schedulers.io())
                .doOnNext(DrawingServer::listenForShapes); // execute logic for listening for shape for socket.
    }

    /**
     * Set up input-stream to listen for shapes from client.
     * @param socket client-socket.
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
     * Send the shape to client.
     * @param clientSocket client-socket.
     * @param shape shape object.
     */
    private static void sendShapeToClient(Socket clientSocket, Shape shape) {
        // Get the relevant output-stream for client.
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

    public static Observable<Shape> getShapesObservable() {
        return shapesSubject;
    }


    /**
     * Remove client from output-stream and close.
     * @param clientSocket client-socket.
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