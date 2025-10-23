/**
 * @author Muska Said Hasan Mustafa and Nick Gottwald
 * @version 1.0
 */

import javax.swing.*;
import java.util.logging.*;

public class App {
    public static void main(String[] args) {
        Logger root = LogManager.getLogManager().getLogger("");
        root.setLevel(Level.INFO);
        for (Handler h : root.getHandlers()) {
            h.setLevel(Level.INFO);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

