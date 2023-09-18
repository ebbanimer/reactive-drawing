package se.miun.dt176g.ebni2100.reactive.Client;

import io.reactivex.rxjava3.disposables.Disposable;
import se.miun.dt176g.ebni2100.reactive.Server.DrawingServer;

import javax.swing.SwingUtilities;
import java.io.IOException;


/**
 * <h1>DrawingClient</h1>
 *
 * @author  Ebba Nimér
 * @version 1.0
 * @since   2022-09-08
 */
public class DrawingClient {

    public static void main(String[] args) {


        // Make sure GUI is created on the event dispatching thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
}
