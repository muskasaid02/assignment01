import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
    private final JLabel label;

    public StatusBar() {
        super(new BorderLayout());
        label = new JLabel("Ready");
        label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(label, BorderLayout.CENTER);
    }

    public void setMessage(String message) {
        label.setText(message);
    }
}
