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

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import se.miun.dt176g.ebni2100.reactive.Client.Shape;

public class DrawingServer {

    private static List<Shape> shapes = new ArrayList<>();
    private static final ReplaySubject<Shape> shapesSubject = ReplaySubject.create();
    private static final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static ServerDrawingFrame serverMainFrame;

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

        // Send all shapes to the new client
        Disposable dp = shapesSubject
                .observeOn(Schedulers.io())
                .subscribe(shape -> sendShapeToClient(clientSocket, shape));

        // Add the disposable to the compositeDisposable
        compositeDisposable.add(dp);

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
                    shapes.add(receivedShape);
                    System.out.println(receivedShape + " added to shapes, current size; " + shapes.size());
                    shapesSubject.onNext(receivedShape);
                    serverMainFrame.updateIncomingShapes(shapes);
                } catch (EOFException e) {
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
    }

    private static void sendShapeToClient(Socket clientSocket, Shape shape) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutputStream.writeObject(shape);
            objectOutputStream.flush(); // Flush the stream
            System.out.println("Sent " + shape + " to " + clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendShapesToNewClient(Socket clientSocket) {
        System.out.println("Sending shapes to the new client...");

        Disposable dp = shapesSubject
                .observeOn(Schedulers.io())
                .subscribe(shape -> sendShapeToClient(clientSocket, shape),
                        throwable -> {
                            System.err.println("Error sending shapes to the new client: " + throwable.getMessage());
                            throwable.printStackTrace();
                        });

        // Add the disposable to the compositeDisposable
        compositeDisposable.add(dp);
    }


    public static Observable<Shape> getShapesObservable() {
        System.out.println("Get the shapes observable");
        return shapesSubject
                .doOnNext(shape -> System.out.println("Received shape in shapesSubject: " + shape))
                .doOnSubscribe(disposable -> System.out.println("Subscribed to shapesSubject"))
                .doOnDispose(() -> System.out.println("Disposed of shapesSubject"));
    }


/*Observable<Socket> clientConnectionsObservable = Observable.create(emitter -> {
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

        Disposable disposable = clientConnectionsObservable
                .subscribeOn(Schedulers.io())
                .flatMap(clientSocket -> {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Send all shapes to the new client
                    Disposable dp = shapesSubject
                            .observeOn(Schedulers.io())
                            .subscribe(shape -> sendShapeToClient(clientSocket, shape));

                    // Add the disposable to the compositeDisposable
                    compositeDisposable.add(dp);

                    return Observable.just(clientSocket)
                            .observeOn(Schedulers.io())
                            .map(socket -> {
                                try {
                                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                                    while (true) {
                                        try {
                                            Shape receivedShape = (Shape) objectInputStream.readObject();
                                            shapes.add(receivedShape);
                                            System.out.println(receivedShape + " added to shapes, current size; " + shapes.size());
                                            shapesSubject.onNext(receivedShape);
                                            serverMainFrame.updateIncomingShapes(shapes);
                                        } catch (EOFException e) {
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
                .subscribe(
                        innerSocket -> System.out.println("Subscription completed for socket: " + innerSocket),
                        throwable -> {
                            System.err.println("Error in main subscription: " + throwable.getMessage());
                            throwable.printStackTrace();
                        });

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        disposable.dispose();*/



/*


    private static void sendShapeToClient(Socket clientSocket, Shape shape) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutputStream.writeObject(shape);
            objectOutputStream.flush();
            System.out.println("Sent " + shape + " to " + clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Observable<Shape> getShapesObservable() {
        System.out.println("Get the shapes observable");
        return shapesSubject
                .doOnNext(shape -> System.out.println("Received shape in shapesSubject: " + shape))
                .doOnSubscribe(disposable -> System.out.println("Subscribed to shapesSubject"))
                .doOnDispose(() -> System.out.println("Disposed of shapesSubject"));
    }*/

}