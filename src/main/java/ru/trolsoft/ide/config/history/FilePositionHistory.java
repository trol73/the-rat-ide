package ru.trolsoft.ide.config.history;

import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FilePositionHistory {
    private static final int MAX_NUMBER_OF_RECORDS = 1000;

    private final List<FileRecord> records = new ArrayList<>();

    public static class FileRecord {
        private final String fileName;
        private int scrollPosition;
        private int line, column;
        private String fileType;
        private String encoding;

        FileRecord(String fileName, int firstLine, int row, int column, String fileType, String encoding) {
            this.fileName = fileName;
            update(firstLine, row, column, fileType, encoding);
        }

        FileRecord(String fileName) {
            this.fileName = fileName;
        }

        public void update(int firstLine, int line, int column, String fileType, String encoding) {
            setScrollPosition(firstLine);
            setLine(line);
            setColumn(column);
            setFileType(fileType);
            setEncoding(encoding);
        }

        public void update(FileRecord source) {
            this.scrollPosition = source.scrollPosition;
            this.line = source.line;
            this.column = source.column;
            this.fileType = source.fileType;
            this.encoding = source.encoding;
        }

        public int getLine() {
            return line <= 0 ? 1 : line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public int getScrollPosition() {
            return scrollPosition;
        }

        public void setScrollPosition(int scrollPosition) {
            this.scrollPosition = scrollPosition;
        }

        public int getColumn() {
            return column > 0 ? column : 1;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = "null".equals(encoding) ? null : encoding;
        }

        @Override
        public String toString() {
            return fileName + '=' + scrollPosition + ',' + getLine() + ',' + getColumn() + ',' + getFileType() + ',' + getEncoding();
        }
    }

    public void load(String filePath) {
        records.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                FileRecord rec = parseRecord(line);
                if (rec != null) {
                    records.add(rec);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static FileRecord parseRecord(String s) {
        int index = s.indexOf('=');
        if (index < 0) {
            return null;
        }
        String fileName = s.substring(0, index);
        String[] props = s.substring(index + 1).split(",");
        try {
            for (int i = 0; i < props.length; i++) {
                props[i] = props[i].trim();
            }
            String type = props[3];
            String encoding = props[4];
            int firstLine = Integer.parseInt(props[0]);
            int row = Integer.parseInt(props[1]);
            int column = Integer.parseInt(props[2]);
            return new FileRecord(fileName, firstLine, row, column, type, encoding);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void save(String path) {
        try {
            save(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            for (FileRecord rec : records) {
                writer.write(rec.toString());
                writer.write('\n');
            }
        }
    }

    private FileRecord get(String fileName) {
        int index = findRecord(fileName);
        return index >= 0 ? records.get(index) : new FileRecord(fileName);
    }

    public String restoreEncoding(String filePath, String defaultEncoding) {
        int index = findRecord(filePath);
        return index >= 0 ? records.get(index).getEncoding() : defaultEncoding;
    }

    public void restoreTextArea(TextEditorPane editor) {
        //System.out.println("LOAD " + editor.getFileFullPath());
        FileRecord record = get(editor.getFileFullPath());
        Component parent = editor.getParent();
        if (parent instanceof JViewport) {
            ((JViewport)parent).setViewPosition(new java.awt.Point(0, record.getScrollPosition()));
        }
        editor.gotoLine(record.getLine(), record.getColumn());
    }

    public void saveTextArea(TextEditorPane editor, RTextScrollPane scrollPane) {
        //System.out.println("SAVE " + editor.getFileFullPath());
        FileRecord record = get(editor.getFileFullPath());
        record.setLine(editor.getLine());
        record.setColumn(editor.getColumn());
        if (scrollPane != null) {
            record.setScrollPosition(scrollPane.getVerticalScrollBar().getValue());
        }
        record.setFileType(editor.getSyntaxEditingStyle());
        record.setEncoding(editor.getEncoding());
//System.out.println(editor.getParent());
        updateRecord(record);
    }

    private int findRecord(String fileName) {
        for (int i = 0; i < records.size(); i++) {
            FileRecord rec = records.get(i);
            if (rec.fileName.equals(fileName)) {
                return i;
            }
        }
        return -1;
    }


    private void updateRecord(FileRecord record) {
        int index = findRecord(record.fileName);
        if (index >= 0) {
            records.remove(index);
        }
        records.add(0, record);
        while (records.size() > MAX_NUMBER_OF_RECORDS) {
            records.remove(records.size()-1);
        }
    }


    public List<String> getLastList(int maxCount) {
        List<String> result = new ArrayList<>();
        for (FileRecord rec : records) {
            result.add(rec.fileName);
            if (result.size() >= maxCount) {
                break;
            }
        }
        return result;
    }
}
