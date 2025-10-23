/**
 * @author Muska Said Hasan Mustafa and Nick Gottwald
 * @version 1.0
 */

import java.io.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileAnalyzer {
    private static final Logger LOG = Logger.getLogger(FileAnalyzer.class.getName());
    private static final Pattern CONTROL_PATTERN = Pattern.compile("\\b(if|switch|for|while)\\b");

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
