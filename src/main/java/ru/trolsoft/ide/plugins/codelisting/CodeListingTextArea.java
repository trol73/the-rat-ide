package ru.trolsoft.ide.plugins.codelisting;

import org.fife.io.UnicodeReader;
import org.fife.rtext.plugins.project.model.Project;
import org.fife.rtext.plugins.project.model.ProjectType;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import ru.trolsoft.ide.plugins.codelisting.compilers.CodeListFile;
import ru.trolsoft.ide.plugins.codelisting.compilers.GccCodeListFile;
import ru.trolsoft.ide.utils.ProjectUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CodeListingTextArea extends RSyntaxTextArea {

    private final CodeListingPlugin plugin;
    private Project project;
    private CodeListFile codeListFile;

    public CodeListingTextArea(CodeListingPlugin plugin) {
        super();
        this.plugin = plugin;
        setupTheme();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewToModel = viewToModel(e.getPoint());
                if (viewToModel >= 0) {
                    try {
                        int line = getLineOfOffset(viewToModel);
                        onMouseClicked(line);
                    } catch (BadLocationException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }


    private void setupTheme() {
        //boolean isDark = RTextUtilities.isDarkLookAndFeel();
        //setBackground(isDark ? Color.BLACK : Color.WHITE);
        Theme theme;
        try {
            theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/trol_dark.xml"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        setBackground(theme.bgColor);
        setSyntaxScheme(theme.scheme);
        setCaretColor(theme.caretColor);
        setSelectionColor(theme.selectionBG);
        setUseSelectedTextColor(theme.useSelectionFG);
        //setCurrentLineHighlightColor(theme.currentLineHighlight);
        setCurrentLineHighlightColor(new Color(0x5555ff));
        setMatchedBracketBGColor(theme.matchedBracketBG);
        setMatchedBracketBorderColor(theme.matchedBracketFG);
        setMarginLineColor(theme.marginLineColor);
    }

    public void loadFile() {
        var currentProject = ProjectUtils.getProjectForCurrentFile(plugin.getApplication());
        if (project != currentProject) {
            project = currentProject;
            if (project == null) {
                codeListFile = null;
                return;
            }
            codeListFile = createCodeListFile();
        }
        if (codeListFile == null) {
            return;
        }
        File f = codeListFile.getListFile();
        if (f == null) {
            return;
        }
        try {
            load(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CodeListFile createCodeListFile() {
        if (project.getType() == ProjectType.BUILDER) {
            return new GccCodeListFile(project);
        }
        return null;
    }

    private void load(File f) throws IOException {
        UnicodeReader ur = new UnicodeReader(new FileInputStream(f), project.getEncoding());

        try (BufferedReader r = new BufferedReader(ur)) {
            read(r, null);
        }
    }

    public boolean gotoLine(int line, int column) {
        try {
            int pos = getLineStartOffset(line - 1) + column - 1;
            setCaretPosition(pos);
            SwingUtilities.invokeLater(() -> {
                setCaretPosition(pos);
                forceCurrentLineHighlightRepaint();
            });
            return true;
        } catch (IllegalArgumentException | BadLocationException e) {
            //System.out.println("Invalid line: " + line + ":" + column + " (" + e.getMessage() + ")");
            return false;
        }
    }

    private void onMouseClicked(int line) {
        var srcPos = plugin.window.getListingInfo().getSourceLineForListingLine(line);
        if (srcPos != null) {
            plugin.getApplication().openFile(srcPos.filePath(), srcPos.line(), 1);
        }
    }

}
