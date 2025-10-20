import javax.swing.*;
import java.awt.*;

public class MetricsPanel extends JPanel {

    public static final int SCALE_PX_PER_UNIT = 3; // same scale for both boxes

    private final BoxPanel complexityPanel = new BoxPanel("Complexity");
    private final BoxPanel sizePanel = new BoxPanel("Size");
    private final OverallPanel overallPanel = new OverallPanel();

    public MetricsPanel() {
        super(new GridLayout(3, 1, 8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(complexityPanel);
        add(sizePanel);
        add(overallPanel);
    }

    public void updateFrom(AnalysisResult result) {
        complexityPanel.setValue(result.getControlCount());
        sizePanel.setValue(result.getLineCount());
        overallPanel.setMood(result.getMood());
        revalidate();
        repaint();
    }

    public void clear() {
        complexityPanel.setValue(0);
        sizePanel.setValue(0);
        overallPanel.setMood(null);
    }

    public static int computeWidth(int value) {
        int width = value * SCALE_PX_PER_UNIT;
        return Math.max(10, Math.min(width, 600));
    }
}
