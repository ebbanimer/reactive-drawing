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

public class DrawingServer {

    private static List<Shape> shapes = new ArrayList<>();
    private static final ReplaySubject<Shape> shapesSubject = ReplaySubject.create();
    private static final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static ServerDrawingFrame serverMainFrame;
    private static final ConcurrentMap<Socket, ObjectOutputStream> outputStreams = new ConcurrentHashMap<>();


    public static void main(String[] args) throws IOException {
        int portNumber = 12345;
        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("Server is running. Waiting for client connections...");

        serverMainFrame = new ServerDrawingFrame();
        serverMainFrame.setVisible(true);

        handleClientConnections(serverSocket);

    }

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

    private static Observable<Socket> handleClient(Socket clientSocket) {
        System.out.println("Client connected: " + clientSocket.getInetAddress());

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStreams.put(clientSocket, objectOutputStream);

            Disposable dp = shapesSubject
                    .observeOn(Schedulers.io())
                    .subscribe(shape -> sendShapeToClient(clientSocket, shape),
                            throwable -> {
                                System.err.println("Error sending shapes to the client: " + throwable.getMessage());
                                throwable.printStackTrace();
                            },
                            () -> {
                                // Cleanup when the observable is completed (client disconnected)
                                outputStreams.remove(clientSocket);
                                // Close the ObjectOutputStream here if needed
                                // objectOutputStream.close();
                            });

            // Add the disposable to the compositeDisposable
            compositeDisposable.add(dp);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Observable.just(clientSocket)
                .observeOn(Schedulers.io())
                .map(DrawingServer::listenForShapes);
    }

    private static Socket listenForShapes(Socket socket) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            while (true) {
                try {
                    Shape receivedShape = (Shape) objectInputStream.readObject();

                    if (receivedShape instanceof Clear) {
                        shapes.clear();
                    } else {
                        shapes.add(receivedShape);
                    }
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
        return socket;
    }

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

    private static void sendShapeToClient(Socket clientSocket, Shape shape) {
        ObjectOutputStream objectOutputStream = outputStreams.get(clientSocket);
        if (objectOutputStream != null) {
            try {
                objectOutputStream.writeObject(shape);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}