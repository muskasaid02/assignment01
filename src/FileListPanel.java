import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FileListPanel extends JPanel {
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> list = new JList<>(listModel);

    public FileListPanel() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(250, 600));
        setBorder(BorderFactory.createTitledBorder("Files"));

        list.setVisibleRowCount(20);
        JScrollPane scroll = new JScrollPane(list,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scroll, BorderLayout.CENTER);
    }

    public void setFiles(List<String> filenames) {
        listModel.clear();
        for (String f : filenames) {
            listModel.addElement(f);
        }
        if (!filenames.isEmpty()) {
            list.setSelectedIndex(0);
        }
    }

    public String getFirstFileName() {
        return listModel.isEmpty() ? null : listModel.get(0);
    }
}
