import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {
    private static final Logger LOG = Logger.getLogger(MainFrame.class.getName());
    
    // UI components
    private final JLabel statusLabel;
    private final DefaultListModel<String> listModel;
    private final JList<String> fileList;
    private final BarPanel complexityPanel;
    private final BarPanel sizePanel;
    private final OverallPanel overallPanel;
    
    // State (minimal)
    private File currentFolder;

    public MainFrame() {
        super("Assignment 01");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        statusLabel = new JLabel("Ready");
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        complexityPanel = new BarPanel("Complexity", new Color(0xE6B3FF));
        sizePanel = new BarPanel("Size", new Color(0xFFB3E6));
        overallPanel = new OverallPanel();

        // Build UI
        add(createFileListPanel(), BorderLayout.WEST);
        add(createMetricsPanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
        setJMenuBar(createMenuBar());
        
        clearMetrics();
    }

    private JPanel createFileListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(150, 600));
        panel.setBackground(new Color(0xFFFFCC));
        
        JLabel titleLabel = new JLabel("Files");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(0xCCFFCC));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        fileList.setVisibleRowCount(20);
        fileList.setBackground(new Color(0xFFFFCC));
        JScrollPane scroll = new JScrollPane(fileList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.getViewport().setBackground(new Color(0xFFFFCC));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMetricsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 0, 0));
        panel.add(complexityPanel);
        panel.add(sizePanel);
        panel.add(overallPanel);
        return panel;
    }

    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xCCCCFF));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(0xCCCCFF));
        panel.add(statusLabel, BorderLayout.CENTER);
        return panel;
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open");
        open.addActionListener(e -> handleOpen());
        file.add(open);

        JMenu action = new JMenu("Action");
        JMenuItem analyzeFirst = new JMenuItem("Analyze First File");
        analyzeFirst.addActionListener(e -> analyzeFirstFileInList());
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

    private void handleOpen() {
        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView());
        chooser.setDialogTitle("Select a folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFolder = chooser.getSelectedFile();
            statusLabel.setText("Selected folder: " + currentFolder.getAbsolutePath());
            LOG.info(() -> "Folder selected: " + currentFolder.getAbsolutePath());
            loadFilesFromFolder();
        } else {
            statusLabel.setText("Open canceled");
        }
    }

    private void loadFilesFromFolder() {
        if (currentFolder == null) return;
        SwingWorker<List<String>, String> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                publish("Reading files...");
                List<String> names = Files.list(currentFolder.toPath())
                        .filter(p -> p.toFile().isFile())
                        .map(p -> p.getFileName().toString())
                        .sorted(String::compareToIgnoreCase)
                        .collect(Collectors.toList());
                publish("Loaded " + names.size() + " files");
                return names;
            }

            @Override
            protected void process(List<String> chunks) {
                if (!chunks.isEmpty()) {
                    statusLabel.setText(chunks.get(chunks.size() - 1));
                }
            }

            @Override
            protected void done() {
                try {
                    List<String> names = get();
                    listModel.clear();
                    for (String f : names) {
                        listModel.addElement(f);
                    }
                    if (!names.isEmpty()) {
                        fileList.setSelectedIndex(0);
                    }
                    analyzeFirstFileInList();
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Failed to list files", e);
                    statusLabel.setText("Error listing files: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void analyzeFirstFileInList() {
        if (listModel.isEmpty()) {
            statusLabel.setText("No files to analyze");
            return;
        }
        String first = listModel.get(0);
        File file = new File(currentFolder, first);
        analyzeFile(file);
    }

    private void analyzeFile(File file) {
        statusLabel.setText("Analyzing: " + file.getName());
        SwingWorker<FileAnalyzer.AnalysisResult, String> worker = new SwingWorker<>() {
            @Override
            protected FileAnalyzer.AnalysisResult doInBackground() throws Exception {
                publish("Counting lines...");
                FileAnalyzer analyzer = new FileAnalyzer();
                FileAnalyzer.AnalysisResult res = analyzer.analyze(file);
                publish("Updating visuals...");
                return res;
            }

            @Override
            protected void process(List<String> chunks) {
                if (!chunks.isEmpty()) {
                    statusLabel.setText(chunks.get(chunks.size() - 1));
                }
            }

            @Override
            protected void done() {
                try {
                    FileAnalyzer.AnalysisResult res = get();
                    updateMetrics(res);
                    statusLabel.setText("Done. Lines=" + res.getLineCount() + 
                            ", Controls=" + res.getControlCount() + ", Mood=" + res.getMood());
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Analysis failed", e);
                    statusLabel.setText("Analysis failed: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void updateMetrics(FileAnalyzer.AnalysisResult result) {
        complexityPanel.setValue(result.getControlCount());
        sizePanel.setValue(result.getLineCount());
        overallPanel.setMood(result.getMood());
        revalidate();
        repaint();
    }

    private void clearMetrics() {
        complexityPanel.setValue(0);
        sizePanel.setValue(0);
        overallPanel.setMood(null);
    }

    // Inner class for bar charts
    private static class BarPanel extends JPanel {
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

            // Draw title
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            g2.drawString(title, (getWidth() - titleWidth) / 2, 25);

            // Calculate bar dimensions
            int barWidth = 80;
            int maxBarHeight = getHeight() - 80;
            int barHeight = Math.min((int)(value * 3), maxBarHeight);
            
            int x = (getWidth() - barWidth) / 2;
            int y = getHeight() - barHeight - 40;

            // Draw bar
            g2.setColor(barColor);
            g2.fillRect(x, y, barWidth, barHeight);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(x, y, barWidth, barHeight);

            g2.dispose();
        }
    }

    // Inner class for mood display
    private static class OverallPanel extends JPanel {
        private FileAnalyzer.AnalysisResult.Mood mood = null;

        public OverallPanel() {
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }

        public void setMood(FileAnalyzer.AnalysisResult.Mood mood) {
            this.mood = mood;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Draw title
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

            // face circle
            Color faceColor;
            if (mood == FileAnalyzer.AnalysisResult.Mood.HAPPY) {
                faceColor = new Color(0xCCFFCC);
            } else if (mood == FileAnalyzer.AnalysisResult.Mood.NEUTRAL) {
                faceColor = new Color(0xCCFFFF);
            } else {
                faceColor = new Color(0xFFFFCC);
            }
            
            g2.setColor(faceColor);
            g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(cx - r, cy - r, 2 * r, 2 * r);

            // eyes
            g2.fillOval(cx - 20, cy - 15, 10, 10);
            g2.fillOval(cx + 10, cy - 15, 10, 10);

            // mouth
            g2.setStroke(new BasicStroke(3f));
            int mw = 40;
            int mh = 20;
            if (mood == FileAnalyzer.AnalysisResult.Mood.HAPPY) {
                g2.drawArc(cx - mw/2, cy - 5, mw, mh, 200, 140);
            } else if (mood == FileAnalyzer.AnalysisResult.Mood.NEUTRAL) {
                g2.drawLine(cx - mw/2, cy + 10, cx + mw/2, cy + 10);
            } else { // SAD
                g2.drawArc(cx - mw/2, cy + 10, mw, mh, 20, 140);
            }

            g2.dispose();
        }
    }
}