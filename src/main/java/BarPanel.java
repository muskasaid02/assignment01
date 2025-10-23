import javax.swing.*;
import java.awt.*;

public class BarPanel extends JPanel {
    private int value = 0;
    private final Color barColor;
    private final String title;

    public BarPanel(String title, Color barColor) {
        this.title = title;
        this.barColor = barColor;
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
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

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2.drawString(title, (getWidth() - titleWidth) / 2, 25);

        int barWidth = 80;
        int maxBarHeight = getHeight() - 80;
        int barHeight = Math.min(value, maxBarHeight);
        
        int x = (getWidth() - barWidth) / 2;
        int y = getHeight() - barHeight - 40;

        g2.setColor(barColor);
        g2.fillRect(x, y, barWidth, barHeight);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(x, y, barWidth, barHeight);

        g2.dispose();
    }
}
