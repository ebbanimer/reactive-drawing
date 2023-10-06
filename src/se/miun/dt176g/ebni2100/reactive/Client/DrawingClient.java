package se.miun.dt176g.ebni2100.reactive.Client;
import javax.swing.SwingUtilities;


/**
 * Client-program.
 * @author  Ebba Nimér
 */
public class DrawingClient {

    public static void main(String[] args) {

        // Start GUI on the event dispatching thread
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
