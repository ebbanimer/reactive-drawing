package se.miun.dt176g.ebni2100.reactive.Client;
import javax.swing.SwingUtilities;


/**
 * Client program for a drawing application.
 *
 * This program initializes and starts the graphical user interface (GUI) on the
 * event dispatching thread.
 *
 * @author Ebba Nimér
 */
public class DrawingClient {

    public static void main(String[] args) {

        // Start GUI on the event dispatching thread
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
