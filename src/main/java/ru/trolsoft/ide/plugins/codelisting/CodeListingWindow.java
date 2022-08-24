package ru.trolsoft.ide.plugins.codelisting;

import org.fife.rtext.RText;
import org.fife.rtext.RTextEditorPane;
import org.fife.ui.dockablewindows.DockableWindow;
import org.fife.ui.dockablewindows.DockableWindowConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.utils.UIUtil;
import org.fife.ui.widgets.RScrollPane;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class CodeListingWindow extends DockableWindow implements PropertyChangeListener {
    private final CodeListingTextArea textArea;
    private final ListingInfo listingInfo;

    CodeListingWindow(RText app, CodeListingPlugin plugin) {
        this.listingInfo = new ListingInfo(app);
        setDockableWindowName(plugin.getString("DockableWindow.Title"));
        setIcon(plugin.getPluginIcon());
        setPosition(DockableWindowConstants.BOTTOM);
        setLayout(new BorderLayout());

        // Create the main panel, containing the shells.
        CardLayout cards = new CardLayout();
        JPanel mainPanel = new JPanel(cards);
        add(mainPanel);

        textArea = new CodeListingTextArea(plugin);
        textArea.setEditable(false);
        setPrimaryComponent(textArea);
        RScrollPane sp = new RScrollPane(textArea);
        UIUtil.removeTabbedPaneFocusTraversalKeyBindings(sp);
        mainPanel.add(sp, "Viewer");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

//    public void load() {
//        textArea.loadFile();
//    }

    public void clear() {
        textArea.setText("");
        listingInfo.clear();
    }

    public void add(String sourceFile, int sourceLine, String listData) {
        listingInfo.add(sourceFile, sourceLine, listData);
    }

    public void initText(String highlighterStyle) {
        textArea.setText(listingInfo.getText());
        textArea.setSyntaxEditingStyle(highlighterStyle);
    }

    public void activateLineFor(RTextEditorPane srcTextArea, int sourceLine) {
        if (this.textArea.getText().isEmpty()) {
            return;
        }
        int listingLine = listingInfo.getListLineFor(srcTextArea.getFileFullPath(), sourceLine);
        if (listingLine >= 0) {
            this.textArea.gotoLine(listingLine, 1);
        }
    }

    public void loadRatGccListing(String path) {
        try (var reader = new BufferedReader(new FileReader(path))) {
            while (true) {
                var line = reader.readLine();
                if (line == null) {
                    break;
                }
                var tokenizer = new StringTokenizer(line);
                var file = tokenizer.nextToken(":");
                var lineNum = tokenizer.nextToken("\t").substring(1);
                var dump = tokenizer.nextToken("\n").substring(1);
                if (dump.endsWith(":")) {
                    dump = dump.substring(10);  // remove offset
                }
                add(file, Integer.parseInt(lineNum), dump);
            }
            initText(SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_AVR);
        } catch (IOException ignore) {
        }
    }
}
