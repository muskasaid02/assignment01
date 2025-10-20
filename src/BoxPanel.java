import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class BoxPanel extends JPanel {
    private int value = 0;
    private final String label;

    public BoxPanel(String label) {
        this.label = label;
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), label, TitledBorder.LEFT, TitledBorder.TOP));
        setPreferredSize(new Dimension(400, 120));
    }

    public void setValue(int value) {
        this.value = Math.max(0, value);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = MetricsPanel.computeWidth(value);
        int h = 50;
        int x = 20;
        int y = (getHeight() - h) / 2;

        // rectangle
        g2.setColor(new Color(0x4A90E2));
        g2.fillRect(x, y, w, h);

        // outline
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(x, y, w, h);

        // value text
        g2.setColor(Color.BLACK);
        g2.drawString(label + " value: " + value + " (width " + w + "px)", x, y - 8);

        g2.dispose();
    }
}
