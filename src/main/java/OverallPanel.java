/**
 * @author Muska Said Hasan Mustafa and Nick Gottwald
 * @version 1.0
 */

import javax.swing.*;
import java.awt.*;

public class OverallPanel extends JPanel {
    private Mood mood = null;

    public OverallPanel() {
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    public void setMood(Mood mood) {
        this.mood = mood;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        String title = "Overall";
        int titleWidth = fm.stringWidth(title);
        g.drawString(title, (getWidth() - titleWidth) / 2, 25);
        
        if (mood == null) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2 + 20;
        int r = 60;

        Color faceColor;
        if (mood == Mood.HAPPY) {
            faceColor = new Color(0xCCFFCC);
        } else if (mood == Mood.NEUTRAL) {
            faceColor = new Color(0xCCFFFF);
        } else {
            faceColor = new Color(0xFFFFCC);
        }
        
        g2.setColor(faceColor);
        g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(cx - r, cy - r, 2 * r, 2 * r);

        g2.fillOval(cx - 20, cy - 15, 10, 10);
        g2.fillOval(cx + 10, cy - 15, 10, 10);

        g2.setStroke(new BasicStroke(3f));
        int mw = 40;
        int mh = 20;
        if (mood == Mood.HAPPY) {
            g2.drawArc(cx - mw/2, cy - 5, mw, mh, 200, 140);
        } else if (mood == Mood.NEUTRAL) {
            g2.drawLine(cx - mw/2, cy + 10, cx + mw/2, cy + 10);
        } else {
            g2.drawArc(cx - mw/2, cy + 10, mw, mh, 20, 140);
        }

        g2.dispose();
    }
}
