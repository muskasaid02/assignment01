import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class OverallPanel extends JPanel {
    private AnalysisResult.Mood mood = null;

    public OverallPanel() {
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Overall", TitledBorder.LEFT, TitledBorder.TOP));
        setPreferredSize(new Dimension(400, 140));
    }

    public void setMood(AnalysisResult.Mood mood) {
        this.mood = mood;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mood == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2 + 10;
        int r = 45;

        // face circle
        g2.setColor(new Color(0xFFF59D));
        g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(cx - r, cy - r, 2 * r, 2 * r);

        // eyes
        g2.fillOval(cx - 18, cy - 10, 8, 8);
        g2.fillOval(cx + 10, cy - 10, 8, 8);

        // mouth
        g2.setStroke(new BasicStroke(3f));
        int mw = 30;
        int mh = 15;
        if (mood == AnalysisResult.Mood.HAPPY) {
            g2.drawArc(cx - mw/2, cy - 2, mw, mh, 200, 140);
        } else if (mood == AnalysisResult.Mood.NEUTRAL) {
            g2.drawLine(cx - mw/2, cy + 6, cx + mw/2, cy + 6);
        } else { // SAD
            g2.drawArc(cx - mw/2, cy + 8, mw, mh, 20, 140);
        }

        // caption
        g2.drawString("Mood: " + mood.name(), 20, 25);

        g2.dispose();
    }
}
