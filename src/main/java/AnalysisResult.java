public class AnalysisResult {
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
