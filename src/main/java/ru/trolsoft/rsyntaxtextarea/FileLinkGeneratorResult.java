package ru.trolsoft.rsyntaxtextarea;

import org.fife.rtext.RText;
import org.fife.rtext.RTextEditorPane;
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.File;

public class FileLinkGeneratorResult implements LinkGeneratorResult {

    private final RText rtext;
    private final File file;
    private final int line;      // 1-based
    private final int column;    // 1-based

    public FileLinkGeneratorResult(RText rtext, File file,
                                   int line, int column) {
        this.rtext = rtext;
        this.file = file;
        this.line = line;
        this.column = column;
    }

    @Override
    public HyperlinkEvent execute() {
        rtext.openFile(file, () -> {
            final RTextEditorPane editor = rtext.getMainView().getCurrentTextArea();
            editor.gotoLine(line, column);
            SwingUtilities.invokeLater(() -> {
                editor.gotoLine(line, column);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                SwingUtilities.invokeLater(() -> {
                    editor.gotoLine(line, column);
                });
            });
        });
        return null;
    }

    @Override
    public int getSourceOffset() {
        return 0;
    }
}