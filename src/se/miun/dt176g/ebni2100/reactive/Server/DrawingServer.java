package se.miun.dt176g.ebni2100.reactive.Server;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import io.reactivex.rxjava3.disposables.Disposable;

public class DrawingServer {

    public static void main(String[] args) throws IOException {
        int portNumber = 12345;
        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("Server is running. Waiting for client connections...");

        // Create an observable for incoming client connections
        Observable<Socket> clientConnectionsObservable = Observable.create(emitter -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept(); // Accept a client connection
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
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
}
