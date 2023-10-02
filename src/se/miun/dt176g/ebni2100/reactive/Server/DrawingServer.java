package se.miun.dt176g.ebni2100.reactive.Server;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import se.miun.dt176g.ebni2100.reactive.Client.Shape;

public class DrawingServer {

    private static List<Shape> shapes = new ArrayList<>();
    private static final PublishSubject<Shape> incomingShapes = PublishSubject.create();

    // Use a PublishSubject to multicast shapes to all clients
    private static final PublishSubject<Shape> shapeSubject = PublishSubject.create();

    public static void main(String[] args) throws IOException {
        int portNumber = 12345;
        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("Server is running. Waiting for client connections...");

        // Create an instance of ServerMainFrame and start it
        ServerDrawingFrame serverMainFrame = new ServerDrawingFrame();
        serverMainFrame.setVisible(true);


        // Create an observable for incoming client connections
        Observable<Socket> clientConnectionsObservable = Observable.create(emitter -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept(); // Accept a client connection
                    emitter.onNext(clientSocket); // Emit the client socket
                } catch (IOException e) {
                    emitter.onError(e); // Handle any errors
                    break; // Exit the loop on error
                }
            }
        });

        Disposable disposable = clientConnectionsObservable
                .subscribeOn(Schedulers.io())
                .flatMap(clientSocket -> {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    return Observable.just(clientSocket)
                            .observeOn(Schedulers.io())
                            .map(socket -> {
                                try {
                                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                                    while (true) {
                                        try {
                                            Shape receivedShape = (Shape) objectInputStream.readObject();
                                            incomingShapes.onNext(receivedShape);

                                            // Check if shapes contains a shape with the same properties
                                            boolean shapeExists = false;
                                            for (int i = 0; i < shapes.size(); i++) {
                                                Shape existingShape = shapes.get(i);
                                                if (existingShape.equals(receivedShape)) {
                                                    shapes.set(i, receivedShape);
                                                    shapeExists = true;
                                                    break;
                                                }
                                            }

                                            // If the shape does not exist in the list, add it
                                            if (!shapeExists) {
                                                shapes.add(receivedShape);
                                            }

                                            serverMainFrame.updateIncomingShapes(shapes);

                                        } catch (EOFException e) {
                                            // End of input stream, client has closed the connection
                                            break;
                                        } catch (IOException | ClassNotFoundException e) {
                                            e.printStackTrace();
                                            break;
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return socket;
                            });
                })
                .subscribe();


        // Subscribe to the clientConnectionsObservable and store the Disposable
        /*Disposable disposable = clientConnectionsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()) // Switch to the IO thread for processing
                .subscribe(clientSocket -> {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    try (clientSocket; ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream())) {
                        while (true) {
                            try {
                                Shape receivedShape = (Shape) objectInputStream.readObject();
                                incomingShapes.onNext(receivedShape); // Publish the received shape

                                // Check if shapes contains a shape with the same properties
                                boolean shapeExists = false;
                                for (int i = 0; i < shapes.size(); i++) {
                                    Shape existingShape = shapes.get(i);
                                    if (existingShape.equals(receivedShape)) {
                                        shapes.set(i, receivedShape);
                                        shapeExists = true;
                                        break;
                                    }
                                }

                                // If the shape does not exist in the list, add it
                                if (!shapeExists) {
                                    shapes.add(receivedShape);
                                }

                                // Call repaint to update the display with the new shapes
                                serverMainFrame.updateIncomingShapes(shapes);

                            } catch (EOFException e) {
                                // End of input stream, client has closed the connection
                                break;
                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                    }
                    // Clean up resources when the loop is terminated
                });*/

        // Block the main thread to keep the server running
        while (true) {
            try {
                Thread.sleep(1000); // Sleep for a while to avoid busy waiting
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Dispose of the subscription when the server is shutting down
        disposable.dispose();
    }

    private static void sendShapeToClient(Socket clientSocket, Shape shape) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutputStream.writeObject(shape);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to incoming shapes from client-side.
     * @return observable emitting Shape object.
     */
    public static Observable<Shape> getIncomingShapesObservable() {
        return incomingShapes;
    }

}
