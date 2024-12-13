package ru.trolsoft.ide.config.state;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class TreeStateSaver {
    protected abstract String pathToStr(TreePath path);

    private final String filePath;

    public TreeStateSaver(String filePath) {
        this.filePath = filePath;
    }

    public void saveState(JTree tree) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(pathToStr(tree.getSelectionPath()));
            writer.write('\n');
            for (int i = 0; i < tree.getRowCount(); i++) {
                TreePath path = tree.getPathForRow(i);
                if (tree.isExpanded(i)) {
                    writer.write(pathToStr(path));
                    writer.write('\n');
                }
            }
        }
    }


    public void restoreState(JTree tree) throws IOException {
        List<String> expanded = load(filePath);
        if (expanded.isEmpty()) {
            return;
        }
        String selected = expanded.get(0);
        expanded.remove(0);
        boolean selectionComplete = false;
        for (int i = 0; i < tree.getRowCount(); i++) {
            TreePath path = tree.getPathForRow(i);
            String pathStr = pathToStr(path);
            if (!selectionComplete && pathStr.equals(selected)) {
                tree.setSelectionPath(path);
                selectionComplete = true;
            }
            if (removePathIfInList(pathStr, expanded)) {
                tree.expandRow(i);
            }
        }
    }

    private List<String> load(String filePath) throws IOException {
        List<String> list = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get(filePath), Charset.defaultCharset())) {
            lines.forEachOrdered(list::add);
        }
        return list;
    }

    private boolean removePathIfInList(String pathStr, List<String> list) {
        for (String s : list) {
            if (s.equals(pathStr)) {
                list.remove(s);
                return true;
            }
        }
        return false;
    }
}
