import java.io.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileAnalyzer {
    private static final Logger LOG = Logger.getLogger(FileAnalyzer.class.getName());
    private static final Pattern CONTROL_PATTERN = Pattern.compile("\\b(if|switch|for|while)\\b");

    public static class AnalysisResult {
        public enum Mood { HAPPY, NEUTRAL, SAD }

        private final int lineCount;
        private final int controlCount;
        private final boolean hasAuthor;
        private final boolean hasVersion;

        public AnalysisResult(int lineCount, int controlCount, boolean hasAuthor, boolean hasVersion) {
            this.lineCount = lineCount;
            this.controlCount = controlCount;
            this.hasAuthor = hasAuthor;
            this.hasVersion = hasVersion;
        }

        public int getLineCount() { return lineCount; }
        public int getControlCount() { return controlCount; }
        public boolean hasAuthor() { return hasAuthor; }
        public boolean hasVersion() { return hasVersion; }

        public Mood getMood() {
            if (hasAuthor && hasVersion) return Mood.HAPPY;
            if (hasAuthor ^ hasVersion) return Mood.NEUTRAL;
            return Mood.SAD;
        }
    }

    public AnalysisResult analyze(File file) throws IOException {
        LOG.info("Analyzing file: " + file.getAbsolutePath());
        int lines = 0;
        int control = 0;
        boolean hasAuthor = false;
        boolean hasVersion = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines++;
                Matcher m = CONTROL_PATTERN.matcher(line);
                while (m.find()) {
                    control++;
                }
                if (!hasAuthor && line.contains("@author")) {
                    hasAuthor = true;
                }
                if (!hasVersion && line.contains("@version")) {
                    hasVersion = true;
                }
            }
        }

        LOG.info(String.format("Lines=%d, Control=%d, @author=%s, @version=%s",
                lines, control, hasAuthor, hasVersion));
        return new AnalysisResult(lines, control, hasAuthor, hasVersion);
    }
}