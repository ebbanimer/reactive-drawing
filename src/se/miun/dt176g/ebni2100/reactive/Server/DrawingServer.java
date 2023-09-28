package se.miun.dt176g.ebni2100.reactive.Server;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
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

    public static void main(String[] args) throws IOException {
        int portNumber = 12345;
        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("Server is running. Waiting for client connections...");

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

        // Subscribe to the clientConnectionsObservable and store the Disposable
        Disposable disposable = clientConnectionsObservable
                .subscribeOn(Schedulers.io())
                .subscribe(clientSocket -> {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    // Receive and handle incoming shapes from the client
                    ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                    while (true) {
                        try {
                            Shape receivedShape = (Shape) objectInputStream.readObject();
                            incomingShapes.onNext(receivedShape); // Publish the received shape
                            System.out.println("Received shape: " + receivedShape);
                        } catch (EOFException e) {
                            // End of input stream, client has closed the connection
                            break;
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                    // Send the list of stored shapes to the new client
                    for (Shape shape : shapes) {
                        // Implement logic to send shape to the new client
                    }
                });

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

    /**
     * Subscribe to incoming shapes from client-side.
     * @return observable emitting Shape object.
     */
    public static Observable<Shape> getIncomingShapesObservable() {
        return incomingShapes;
    }

}
