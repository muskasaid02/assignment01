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
        SwingWorker<AnalysisResult, String> worker = new SwingWorker<>() {
            @Override
            protected AnalysisResult doInBackground() throws Exception {
                publish("Counting lines...");
                FileAnalyzer analyzer = new FileAnalyzer();
                AnalysisResult res = analyzer.analyze(file);
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
                    AnalysisResult res = get();
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

    private void updateMetrics(AnalysisResult result) {
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
}