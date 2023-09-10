/*
 * 12/17/2010
 *
 * ConsoleTextArea.java - Text component for the console.
 * Copyright (C) 2010 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.buildoutput;

import org.fife.io.ProcessRunner;
import org.fife.io.ProcessRunnerOutputListener;
import org.fife.rtext.AbstractMainView;
import org.fife.rtext.RTextEditorPane;
import org.fife.ui.app.console.AbstractConsoleTextArea;
import org.fife.ui.options.OptionsDialog;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.utils.OS;
import org.fife.ui.utils.UIUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Text component that displays a console of some type.  This component tries
 * to mimic jEdit's "Console" behavior, since that seemed to work pretty well.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class BuildOutputTextArea extends AbstractConsoleTextArea {

    public static final Color DEFAULT_DARK_FILES_FG = new Color(0x8888ff);
    public static final Color DEFAULT_DARK_POS_FG = new Color(0x55ffff);
    public static final Color DEFAULT_DARK_WARNINGS_FG = new Color(0xff55ff);
    public static final Color DEFAULT_DARK_ERRORS_FG = new Color(0xff5555);

    public static final Color DEFAULT_LIGHT_FILES_FG = Color.BLUE;
    public static final Color DEFAULT_LIGHT_POS_FG = Color.YELLOW ;
    public static final Color DEFAULT_LIGHT_WARNINGS_FG = Color.MAGENTA;
    public static final Color DEFAULT_LIGHT_ERRORS_FG = Color.RED;

    static final String STYLE_FILE = "File";
    static final String STYLE_FILE_POS = "FilePos";
    static final String STYLE_WARNINGS = "Warnings";
    static final String STYLE_ERRORS = "Errors";

    private static final Color COLOR_ERROR_LINE_HIGHLIGHT = new Color(0xaaff0000, true);
    private static final Color COLOR_WARNING_LINE_HIGHLIGHT = new Color(0xaaffff00, true);

    private final List<int[]> filesPositions = new ArrayList<>();
    private final List<String> filesNames = new ArrayList<>();

    private final Map<RTextEditorPane, List<Object>> errorLineHighlighters = new HashMap<>();

    private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    private static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

    /**
     * Property change event fired whenever a process is launched or completes.
     */
    public static final String PROPERTY_PROCESS_RUNNING = "ProcessRunning";

    final Plugin plugin;
    private int inputMinOffs;

    private File pwd;
    private transient Thread activeProcessThread;
    private transient Runnable finishHandler;


    /**
     * Used to syntax highlight the current text being entered by the user.
     */
    private RSyntaxDocument shDoc;

    /**
     * The maximum number of lines to display in the console.
     */
    private static final int MAX_LINE_COUNT = 1500;


    /**
     * Constructor.
     */
    BuildOutputTextArea(Plugin plugin) {
        this.plugin = plugin;
        installDefaultStyles(false);
        fixKeyboardShortcuts();
        Listener listener = new Listener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
        getDocument().addDocumentListener(listener);

        resetPwd();
    }


    /**
     * Appends text in the given style.  This method is thread-safe.
     *
     * @param text  The text to append.
     * @param style The style to use.
     */
    void append(String text, String style) {
        if (text == null) {
            return;
        }
        if (!text.endsWith("\n")) {
            text += "\n";
        }
        appendImpl(text, style);
    }


    /**
     * Handles updating of the text component.  This method is thread-safe.
     *
     * @param text  The text to append.
     * @param style The style to apply to the appended text.
     */
    void appendImpl(final String text, final String style) {
        appendImpl(text, style, false);
    }


    /**
     * Handles updating of the text component.  This method is thread-safe.
     *
     * @param text             The text to append.
     * @param style            The style to apply to the appended text.
     * @param treatAsUserInput Whether to treat the text as user input.  This
     *                         determines whether the user can use backspace to remove this text or not.
     */
    void appendImpl(final String text, final String style, final boolean treatAsUserInput) {
        // Ensure the meat of this method is done on the EDT, to prevent concurrency errors.
        if (SwingUtilities.isEventDispatchThread()) {
            appendText(text, style, treatAsUserInput);
            // Don't let the console's text get too long
            fixMaxTextLength(treatAsUserInput);
        } else {
            SwingUtilities.invokeLater(() -> appendImpl(text, style, treatAsUserInput));
        }

    }

    private void appendText(String text, String style, boolean treatAsUserInput) {
        if (!treatAsUserInput) {
            Document doc = getDocument();
            int start = doc.getLength();
            var parser = new OutputLineParser(plugin.getApplication(), text, this::insertStyledStr).parse(style);
            if (parser.getFilePath() != null) {
                filesPositions.add(new int[] {start, doc.getLength(), parser.getFileLine(), parser.getFileColumn()});
                filesNames.add(parser.getFilePath());
                var editor = getMainView().getTextAreaForFile(parser.getFilePath());
                if (editor != null) {
                    try {
                        Color higlightColor = parser.isError() ? COLOR_ERROR_LINE_HIGHLIGHT : COLOR_WARNING_LINE_HIGHLIGHT;
                        var hl = editor.addLineHighlight(parser.getFileLine() - 1, higlightColor);
                        var list = errorLineHighlighters.computeIfAbsent(editor, k -> new ArrayList<>());
                        list.add(hl);
                    } catch (BadLocationException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else {
            insertStyledStr(text, style);
        }
        if (!treatAsUserInput) {
            inputMinOffs = getCaretPosition();
        }
    }

    private AbstractMainView getMainView() {
        return plugin.getApplication().getMainView();
    }

    private void insertStyledStr(String text, String style) {
        Document doc = getDocument();
        int end = doc.getLength();
//        if (STYLE_FILE.equals(style)) {
//            filesPositions.add(new int[] {end, end + text.length()-1, 0, 0});
//        }
        try {
            doc.insertString(end, text, getStyle(style));
        } catch (BadLocationException ble) { // Never happens
            ble.printStackTrace();
        }
        setCaretPosition(doc.getLength());
    }


    private void fixMaxTextLength(boolean treatAsUserInput) {
        Document doc = getDocument();
        Element root = doc.getDefaultRootElement();
        int lineCount = root.getElementCount();
        if (lineCount > MAX_LINE_COUNT) {
            int toDelete = lineCount - MAX_LINE_COUNT;
            int endOffs = root.getElement(toDelete - 1).getEndOffset();
            try {
                doc.remove(0, endOffs);
                if (!treatAsUserInput) {
                    inputMinOffs -= endOffs;
                }
            } catch (BadLocationException ble) { // Never happens
                ble.printStackTrace();
            }
        }
    }


    /**
     * Appends the prompt to the console, and resets the starting location
     * at which the user can input text.  This method is thread-safe.
     */
    public void appendPrompt(String prompt) {
//        String prompt = pwd.getName();
//        if (prompt.isEmpty()) { // Root directory
//            prompt = pwd.getAbsolutePath();
//        }
//        prompt += File.separatorChar == '/' ? "$ " : "> ";
        appendImpl(prompt, STYLE_PROMPT);
    }



    /**
     * Clears this console.  This should only be called on the EDT.
     */
    public void clear() {
        setText("");
        filesPositions.clear();
        filesNames.clear();
        for (var editor : errorLineHighlighters.keySet()) {
            for (var hl : errorLineHighlighters.get(editor)) {
                editor.removeLineHighlight(hl);
            }
        }
        errorLineHighlighters.clear();
    }


    @Override
    protected JPopupMenu createPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem(new CopyAllAction()));
        popup.addSeparator();
        popup.add(new JMenuItem(new ClearAllAction()));
        popup.addSeparator();
        popup.add(new JMenuItem(new ConfigureAction()));
        return popup;
    }


    /**
     * Fixes the keyboard shortcuts for this text component so the user cannot
     * accidentally delete any stdout or stderr, only stdin.
     */
    void fixKeyboardShortcuts() {
        InputMap im = getInputMap();
        ActionMap am = getActionMap();
        int ctrl = getToolkit().getMenuShortcutKeyMaskEx();

        // backspace
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspace");
        Action delegate = am.get(DefaultEditorKit.deletePrevCharAction);
        am.put("backspace", new BackspaceAction(delegate));

        // Just remove "delete previous word" for now, since DefaultEditorKit
        // doesn't expose the delegate for us to call into.
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, ctrl),"deletePreviousWord");
        am.put("deletePreviousWord", new DeletePreviousWordAction());

        // delete
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        delegate = am.get(DefaultEditorKit.deleteNextCharAction);
        am.put("delete", new DeleteAction(delegate));

        // Just remove "delete next word" for now, since DefaultEditorKit
        // doesn't expose the delegate for us to call into.
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ctrl), "invalid");

        // Home - go to start of input area (right after prompt)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), "home");
        delegate = am.get(DefaultEditorKit.beginLineAction);
        am.put("home", new HomeAction(delegate, false));

        // Shift+Home - Select to start of input area (right after prompt)
        int shift = InputEvent.SHIFT_DOWN_MASK;
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, shift), "shiftHome");
        delegate = am.get(DefaultEditorKit.selectionBeginLineAction);
        am.put("shiftHome", new HomeAction(delegate, true));

        // Ctrl+A - Select all text entered after the prompt
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, ctrl), "ctrlA");
        delegate = am.get(DefaultEditorKit.selectAllAction);
        am.put("ctrlA", new SelectAllAction(delegate));

        // Enter - submit command entered
//        int mod = 0;
//        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, mod);
//        im.put(ks, "Submit");
//        am.put("Submit", new SubmitAction());

    }


    /**
     * Returns the currently entered text.
     *
     * @return The currently entered text.
     */
    String getCurrentInput() {
        int startOffs = inputMinOffs;
        Document doc = getDocument();
        int len = doc.getLength() - startOffs;
        if (len < 0) {
            // They've run a command that prints > MAX_LINE_COUNT lines of
            // output.  The input line has scrolled out of view anyway, so
            // nothing to print
            return null;
        }
        try {
            return doc.getText(startOffs, len);
        } catch (BadLocationException ble) {
            ble.printStackTrace();
            return null;
        }
    }


    @Override
    protected Font getDefaultFont() {
        return RTextArea.getDefaultFont();
    }


    /**
     * Returns the syntax style that should be used for syntax highlighting
     * input in this text area.
     *
     * @return The syntax style.
     */
    protected String getSyntaxStyle() {
        return plugin.getApplication().getOS() == OS.WINDOWS ?
                SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH :
                SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL;
    }


    /**
     * Handles the submit of text entered by the user.
     *
     * @param text The text entered by the user.
     */
    void handleSubmit(String text, Runnable onFinish) {
        // Ensure our directory wasn't deleted out from under us.
        if (!pwd.isDirectory()) {
            append(plugin.getString("Error.CurrentDirectoryDNE", pwd.getAbsolutePath()), STYLE_STDERR);
//            appendPrompt();
            return;
        }

        List<String> cmdList = new ArrayList<>();
        if (File.separatorChar == '/') {
            cmdList.add("/bin/sh");
            cmdList.add("-c");
        } else {
            cmdList.add("cmd.exe");
            cmdList.add("/c");
        }
        text = "cd " + pwd.getAbsolutePath() + " && " + text;

        cmdList.add(text);
        final String[] cmd = cmdList.toArray(new String[0]);

        setEditable(false);
        finishHandler = onFinish;
        startProcess(cmd);
    }

    void handleSubmit(String text) {
        handleSubmit(text, null);
    }


    private void startProcess(String[] cmd) {
        activeProcessThread = new Thread(() -> {
            ProcessRunner pr = new ProcessRunner(cmd);
            pr.setDirectory(pwd);
            pr.setOutputListener(new ProcessOutputListener());
            pr.run();
            if (finishHandler != null) {
                SwingUtilities.invokeLater(finishHandler);
                finishHandler = null;
            }
        });
        firePropertyChange(PROPERTY_PROCESS_RUNNING, false, true);
        activeProcessThread.start();
    }


    void startTask(BuildTask task) {
        final var listener = new ProcessOutputListener();
        task.setListener(listener);
        activeProcessThread = new Thread(() -> {
            try {
                task.run();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            if (!task.isFinished()) {
                task.exit(0);
            }
        });
        firePropertyChange(PROPERTY_PROCESS_RUNNING, false, true);
        activeProcessThread.start();
    }



    /**
     * Allows constructors to do stuff before the initial {@link #clear()}
     * call is made.  The default implementation does nothing.
     */
    void resetPwd() {
        pwd = new File(System.getProperty("user.home"));
    }

    public void setPwd(File pwd) {
        this.pwd = pwd;
    }


    /**
     * Overridden to only allow the user to edit text they have entered (i.e.
     * they can only edit "stdin").
     *
     * @param text The text to replace the selection with.
     */
    @Override
    public void replaceSelection(String text) {
        int start = getSelectionStart();
        StyledDocument doc = (StyledDocument) getDocument();

        // Don't let the user remove any text they haven't typed (stdin).
        if (start < inputMinOffs) {
            setCaretPosition(doc.getLength());
        }

        // JUST IN CASE we aren't an AbstractDocument (paranoid), use remove()
        // and insertString() separately.
        try {
            start = getSelectionStart();
            doc.remove(start, getSelectionEnd() - start);
            doc.insertString(start, text, getStyle(STYLE_STDIN));
        } catch (BadLocationException ble) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            ble.printStackTrace();
        }
    }


    /**
     * Called when the user toggles whether to syntax highlight user
     * input in the options dialog.  This method changes the style of
     * <b>only</b> the current user input to match the new preference.  Any
     * previously submitted commands are not re-highlighted.
     */
    void refreshUserInputStyles() {
        // If there's no partial user input, bail early.
        int start = inputMinOffs;
        StyledDocument doc = getStyledDocument();
        int end = doc.getLength();
        if (end == start) {
            return;
        }

        // If we're syntax highlighting now, do that
        if (plugin.getSyntaxHighlightInput()) {
            syntaxHighlightInput();
            return;
        }

        // Otherwise, change all current input to default "input" color.
        Style style = getStyle(STYLE_STDIN);
        doc.setCharacterAttributes(start, end, style, true);
    }


    /**
     * Syntax highlights the current input being entered by the user.
     */
    private void syntaxHighlightInput() {
        if (shDoc == null) {
            shDoc = new RSyntaxDocument(getSyntaxStyle());
        }
        StyledDocument doc = getStyledDocument();

        try {
            SyntaxScheme scheme = plugin.getApplication().getSyntaxScheme();
            shDoc.replace(0, shDoc.getLength(), getCurrentInput(), null);
            Token t = shDoc.getTokenListForLine(0);
            int offs = inputMinOffs;

            while (t != null && t.isPaintable()) {
                int type = t.getType();
                org.fife.ui.rsyntaxtextarea.Style style = scheme.getStyle(type);
                Style attrs = doc.addStyle(null, getStyle(STYLE_STDIN));

                Color fg = style.foreground;
                if (fg != null) StyleConstants.setForeground(attrs, fg);
                Color bg = style.background;
                if (bg != null) StyleConstants.setBackground(attrs, bg);

                Font font = style.font;
                if (font != null) {
                    StyleConstants.setBold(attrs, font.isBold());
                    StyleConstants.setItalic(attrs, font.isItalic());
                }
                StyleConstants.setUnderline(attrs, style.underline);

                doc.setCharacterAttributes(offs, t.length(), attrs, true);
                offs += t.length();
                t = t.getNextToken();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restoreDefaultColors() {
        super.restoreDefaultColors();

        boolean isDark = UIUtil.isDarkLookAndFeel();
        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);

        Style files = addStyle(STYLE_FILE, defaultStyle);
        Color filesColor = isDark ? DEFAULT_DARK_FILES_FG : DEFAULT_LIGHT_FILES_FG;
        StyleConstants.setForeground(files, filesColor);

        Style filePos = addStyle(STYLE_FILE_POS, defaultStyle);
        Color filePosColor = isDark ? DEFAULT_DARK_POS_FG : DEFAULT_LIGHT_POS_FG;
        StyleConstants.setForeground(filePos, filePosColor);

        Style warnings = addStyle(STYLE_WARNINGS, defaultStyle);
        Color warningsColor = isDark ? DEFAULT_DARK_WARNINGS_FG : DEFAULT_LIGHT_WARNINGS_FG;
        StyleConstants.setForeground(warnings, warningsColor);

        Style errors = addStyle(STYLE_ERRORS, defaultStyle);
        Color errorsColor = isDark ? DEFAULT_DARK_ERRORS_FG : DEFAULT_LIGHT_ERRORS_FG;
        StyleConstants.setForeground(errors, errorsColor);
    }

    /**
     * Clears all text from this text area.
     */
    private class ClearAllAction extends AbstractAction {

        ClearAllAction() {
            putValue(NAME, plugin.getString("Action.ClearAll"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            clear();
        }
    }


    /**
     * Brings up the options dialog panel for this plugin.
     */
    private class ConfigureAction extends AbstractAction {

        ConfigureAction() {
            putValue(NAME, plugin.getString("Action.Configure"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            OptionsDialog od = plugin.getApplication().getOptionsDialog();
            od.initialize();
            od.setSelectedOptionsPanel(plugin.getString("Plugin.Name"));
            od.setVisible(true);
        }

    }


    /**
     * Copies all text from this text area.
     */
    private class CopyAllAction extends AbstractAction {

        CopyAllAction() {
            putValue(NAME, plugin.getString("Action.CopyAll"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int dot = getSelectionStart();
            int mark = getSelectionEnd();
            setSelectionStart(0);
            setSelectionEnd(getDocument().getLength());
            copy();
            setSelectionStart(dot);
            setSelectionEnd(mark);
        }
    }


    /**
     * Action performed when backspace is pressed.
     */
    private class BackspaceAction extends TextAction {

        /**
         * DefaultEditorKit's DeletePrevCharAction.
         */
        private final Action delegate;

        BackspaceAction(Action delegate) {
            super("backspace");
            this.delegate = delegate;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int start = getSelectionStart();
            int end = getSelectionEnd();
            if (start >= inputMinOffs && end != start) {
                replaceSelection(null);
            } else if (start <= inputMinOffs) {
                UIManager.getLookAndFeel().provideErrorFeedback(BuildOutputTextArea.this);
                if (start < inputMinOffs) {
                    // Don't jump to the end of input if we were at the start
                    setCaretPosition(getDocument().getLength());
                }
            } else {
                delegate.actionPerformed(e);
            }
        }

    }


    /**
     * Action performed when delete is pressed.
     */
    private class DeleteAction extends TextAction {

        /**
         * DefaultEditorKit's DeleteNextCharAction.
         */
        private final Action delegate;

        DeleteAction(Action delegate) {
            super("delete");
            this.delegate = delegate;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int start = getSelectionStart();
            if (start < inputMinOffs) {
                UIManager.getLookAndFeel().provideErrorFeedback(BuildOutputTextArea.this);
            } else {
                delegate.actionPerformed(e);
            }
        }

    }


    /**
     * Deletes the previous word.
     */
    private class DeletePreviousWordAction extends TextAction {

        DeletePreviousWordAction() {
            super("deletePreviousWord");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int start = getSelectionStart();
            int end = getSelectionEnd();
            if (start >= inputMinOffs && end != start) {
                replaceSelection(null);
                return;
            } else if (start <= inputMinOffs) {
                UIManager.getLookAndFeel().provideErrorFeedback(BuildOutputTextArea.this);
                return;
            }
            try {
                end = Utilities.getPreviousWord(BuildOutputTextArea.this, start);
                if (end >= inputMinOffs) { // Should always be true
                    int offs = Math.min(start, end);
                    int len = Math.abs(end - start);
                    getDocument().remove(offs, len);
                }
            } catch (BadLocationException ble) {
                ble.printStackTrace(); // Never happens
            }
        }

    }


    /**
     * Moves the caret to the beginning of the input area.
     */
    private class HomeAction extends AbstractAction {

        private final Action delegate;
        private final boolean select;

        HomeAction(Action delegate, boolean select) {
            this.delegate = delegate;
            this.select = select;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (select) {
                int dot = getCaretPosition();
                if (dot >= inputMinOffs) {
                    moveCaretPosition(inputMinOffs);
                } else { // Gotta do something - just do default
                    delegate.actionPerformed(e);
                }
            } else {
                setCaretPosition(inputMinOffs);
            }
        }

    }


    /**
     * Listens for events in this text area.
     */
    private class Listener implements MouseListener, MouseMotionListener, DocumentListener {
        private boolean handCursor;
        private String handFilePath;
        private int fileLine, fileColumn;

        @Override
        public void mouseMoved(MouseEvent e) {
            int offset = viewToModel(e.getPoint());
            for (int i = 0; i < filesPositions.size(); i++) {
                int[] interval = filesPositions.get(i);
                if (offset >= interval[0] && offset <= interval[1]) {
                    setCursor(HAND_CURSOR);
                    handCursor = true;
                    handFilePath = filesNames.get(i);
                    fileLine = interval[2];
                    fileColumn = interval[3];
                    return;
                }
            }
            if (handCursor) {
                setCursor(DEFAULT_CURSOR);
            }
            fileLine = -1;
            fileColumn = 0;
            handCursor = false;
        }


        private void handleDocumentEvent() {
            if (plugin.getSyntaxHighlightInput()) {
                // Can't update Document in DocumentListener directly
                SwingUtilities.invokeLater(BuildOutputTextArea.this::syntaxHighlightInput);
            }
        }

        private void handleMouseEvent(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showPopupMenu(e);
            } else if (handFilePath != null && e.getID() == MouseEvent.MOUSE_CLICKED) {
                var app = plugin.getApplication();
                app.openFile(handFilePath, fileLine, fileColumn);
//                app.openFile(new File(handFilePath), () -> {
//                    var editor = getMainView().getCurrentTextArea();
//                    if (fileLine > 0) {
//                        try {
//                            if (fileColumn <= 0) {
//                                fileColumn++;
//                            }
//                            int pos = editor.getLineStartOffset(fileLine - 1) + fileColumn - 1;
//                            SwingUtilities.invokeLater(() -> {
//                                editor.setCaretPosition(pos);
//                                SwingUtilities.invokeLater(() -> editor.setCaretPosition(pos));
//                            });
//                        } catch (BadLocationException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                });
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            handleDocumentEvent();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            handleMouseEvent(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            handleMouseEvent(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            handleMouseEvent(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            handleDocumentEvent();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }

    }


    /**
     * Called when the user presses Ctrl+A.
     */
    private class SelectAllAction extends TextAction {

        /**
         * DefaultEditorKit's SelectAllAction.
         */
        private final Action delegate;

        SelectAllAction(Action delegate) {
            super("SelectAll");
            this.delegate = delegate;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int start = getSelectionStart();
            if (start >= inputMinOffs) { // Select current command only
                setSelectionStart(inputMinOffs);
                setSelectionEnd(getDocument().getLength());
            } else { // Not after the prompt - just select everything
                delegate.actionPerformed(e);
            }
        }

    }


//    /**
//     * Called when the user presses Enter.  Submits the command they entered.
//     */
    /*
    private class SubmitAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            Document doc = getDocument();
            int dot = getCaretPosition();
            if (dot < inputMinOffs) {
                setCaretPosition(doc.getLength());
                UIManager.getLookAndFeel().provideErrorFeedback(BuildOutputTextArea.this);
                return;
            }

            int startOffs = inputMinOffs;
            int len = doc.getLength() - startOffs;
            setCaretPosition(doc.getLength()); // Might be in middle of line
            BuildOutputTextArea.super.replaceSelection("\n");

            // If they didn't enter any text, don't launch a process
            if (len == 0) {
                appendPrompt();
                return;
            }
            String text;
            try {
                text = getText(startOffs, len).trim();
            } catch (BadLocationException ble) { // Never happens
                ble.printStackTrace();
                return;
            }
            if (text.isEmpty()) {
                appendPrompt();
                return;
            }

            handleSubmit(text);
        }
    }
         */

    /**
     * Stops the currently running process, if any.
     */
    public void stopCurrentProcess() {
        if (activeProcessThread != null && activeProcessThread.isAlive()) {
            activeProcessThread.interrupt();
            activeProcessThread = null;
        }
    }

    /**
     * Listens for output from the currently active process and appends it
     * to the console.
     */
    private class ProcessOutputListener implements ProcessRunnerOutputListener {

        @Override
        public void outputWritten(Process p, String output, boolean stdout) {
            append(output, stdout ? STYLE_STDOUT : STYLE_STDERR);
        }

        @Override
        public void processCompleted(Process p, int rc, final Throwable e) {
            // Required because of other Swing calls we make inside
            SwingUtilities.invokeLater(() -> {
                if (e != null) {
                    String text;
                    if (e instanceof InterruptedException) {
                        text = plugin.getString("ProcessForciblyTerminated");
                    } else {
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        text = sw.toString();
                    }
                    append(text, STYLE_EXCEPTION);
                } else if (rc == 0) {
                    append("Done", STYLE_PROMPT);
                } else {
                    append("Exit code: " + rc, STYLE_PROMPT);
                }
                // Not really necessary, should allow GC of Process resources
                activeProcessThread = null;
//                appendPrompt();
//                setEditable(true);
                firePropertyChange(PROPERTY_PROCESS_RUNNING, true, false);
            });
        }

    }

}
