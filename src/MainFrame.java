import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final StatusBar statusBar;
    private final FileListPanel fileListPanel;
    private final MetricsPanel metricsPanel;
    private final AppController controller;

    public MainFrame() {
        super("Assignment 01");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        fileListPanel = new FileListPanel();
        metricsPanel = new MetricsPanel();
        statusBar = new StatusBar();

        add(fileListPanel, BorderLayout.WEST);
        add(metricsPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        setJMenuBar(buildMenuBar());

        controller = new AppController(this);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open...");
        open.addActionListener(e -> controller.handleOpen());
        file.add(open);

        JMenu action = new JMenu("Action");
        JMenuItem analyzeFirst = new JMenuItem("Analyze First File");
        analyzeFirst.addActionListener(e -> controller.analyzeFirstFileInList());
        action.add(analyzeFirst);

        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Assignment 01\nGUI to Build\n\nOpen a folder, see files, and analyze the first file.",
                "About", JOptionPane.INFORMATION_MESSAGE));
        help.add(about);

        bar.add(file);
        bar.add(action);
        bar.add(help);
        return bar;
    }

    public FileListPanel getFileListPanel() { return fileListPanel; }
    public MetricsPanel getMetricsPanel() { return metricsPanel; }
    public StatusBar getStatusBar() { return statusBar; }
}
