package se.miun.dt176g.ebni2100.reactive.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import io.reactivex.rxjava3.core.Observable;

public class DrawingServer {

    /*private final ServerSocket serverSocket;
    private Observable<Socket> clientConnectionsObservable;

    public DrawingServer(int portNumber) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        setupClientConnectionsObservable();
    }

    private void setupClientConnectionsObservable() {
        clientConnectionsObservable = Observable.create(emitter -> {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                emitter.onNext(clientSocket);
            }
        });
    }

    public Observable<Socket> getClientConnectionsObservable() {
        return clientConnectionsObservable;
    }

    public void closeServer() throws IOException {
        serverSocket.close();
    }*/
}

