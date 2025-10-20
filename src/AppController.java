import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AppController {
    private static final Logger LOG = Logger.getLogger(AppController.class.getName());

    private final MainFrame frame;
    private File currentFolder;

    public AppController(MainFrame frame) {
        this.frame = frame;
        frame.getMetricsPanel().clear();
    }

    public void handleOpen() {
        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView());
        chooser.setDialogTitle("Select a folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFolder = chooser.getSelectedFile();
            frame.getStatusBar().setMessage("Selected folder: " + currentFolder.getAbsolutePath());
            LOG.info(() -> "Folder selected: " + currentFolder.getAbsolutePath());
            loadFilesFromFolder();
        } else {
            frame.getStatusBar().setMessage("Open canceled");
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
                    frame.getStatusBar().setMessage(chunks.get(chunks.size() - 1));
                }
            }

            @Override
            protected void done() {
                try {
                    List<String> names = get();
                    frame.getFileListPanel().setFiles(names);
                    analyzeFirstFileInList();
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Failed to list files", e);
                    frame.getStatusBar().setMessage("Error listing files: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void analyzeFirstFileInList() {
        String first = frame.getFileListPanel().getFirstFileName();
        if (first == null) {
            frame.getStatusBar().setMessage("No files to analyze");
            return;
        }
        File file = new File(currentFolder, first);
        analyzeFile(file);
    }

    private void analyzeFile(File file) {
        frame.getStatusBar().setMessage("Analyzing: " + file.getName());
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
                    frame.getStatusBar().setMessage(chunks.get(chunks.size() - 1));
                }
            }

            @Override
            protected void done() {
                try {
                    AnalysisResult res = get();
                    frame.getMetricsPanel().updateFrom(res);
                    frame.getStatusBar().setMessage("Done. Lines=" + res.getLineCount() + ", Controls=" + res.getControlCount() + ", Mood=" + res.getMood());
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Analysis failed", e);
                    frame.getStatusBar().setMessage("Analysis failed: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
}
