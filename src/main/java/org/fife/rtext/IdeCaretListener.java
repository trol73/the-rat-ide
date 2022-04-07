package org.fife.rtext;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.io.File;

public class IdeCaretListener implements CaretListener {
    private final RTextEditorPane textArea;
    private final RText rText;

    public IdeCaretListener(RTextEditorPane textArea, RText rText) {
        this.textArea = textArea;
        this.rText = rText;
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        int line = textArea.getLine();
        int col = textArea.getColumn();

        String str = getLineStr(line);
        if (str == null || str.isEmpty()) {
//            statusBar.setColor(-1);
//            textEditor.selectIncludeFile(null);
            return;
        }
        navigateInCodeListingWindow(line);
//        checkAssemblerInstruction(str);
//        checkColorOnCursor(str, col);
        checkIncludeInstruction(str, col);
    }

    private void navigateInCodeListingWindow(int line) {
        var window = rText.getCodeListingWindow();
        if (window != null && window.isShowing()) {
            window.activateLineFor(textArea, line);
        }
    }

    private String getLineStr(int line) {
        try {
            int posStart = textArea.getLineStartOffset(line - 1);
            int posEnd = textArea.getLineEndOffset(line - 1);
            int len = posEnd - posStart;
            if (len > 2048) {
                return null;
            }
            return textArea.getDocument().getText(posStart, len);
        } catch (Exception ignore) {
            return null;
        }
    }

    private void checkIncludeInstruction(String str, int col) {
        if (!str.toLowerCase().contains("include")) {
            rText.setSelectedIncludedFile(null);
            setStatusMessage("");
            return;
        }
        if (col > 0) {
            col--;
        }
        try {
            int lastQuote2 = str.indexOf('"', col);
            int lastQuote1 = str.indexOf('\'', col);
            int lastBracket = str.indexOf(">", col);
            String quotedName = null;
            if (lastQuote2 > 0) {
                int firstQuote2 = str.lastIndexOf('"', col);
                if (firstQuote2 > 0) {
                    quotedName = str.substring(firstQuote2 + 1, lastQuote2);
                    if (quotedName.trim().isEmpty()) {
                        quotedName = null;
                    }
                }
            }
            if (quotedName == null && lastBracket > 0) {
                int firstBracket = str.lastIndexOf('<', col);
                if (firstBracket > 0) {
                    quotedName = str.substring(firstBracket + 1, lastBracket);
                    if (quotedName.trim().isEmpty()) {
                        quotedName = null;
                    }
                }
            }
            if (quotedName == null && lastQuote1 > 0) {
                int firstQuote1 = str.lastIndexOf('\'', col);
                if (firstQuote1 > 0) {
                    quotedName = str.substring(firstQuote1 + 1, lastBracket);
                    if (quotedName.trim().isEmpty()) {
                        quotedName = null;
                    }
                }
            }
            File includeFile = getIncludeFile(quotedName);
            if (includeFile != null) {
                setStatusMessage("<html>Press Alt+Enter to open file " + " <b>" + quotedName + "</b>");
                rText.setSelectedIncludedFile(includeFile);
                return;
            }
        } catch (StringIndexOutOfBoundsException ignore) {}
        setStatusMessage("");
    }

//    private void checkAssemblerInstruction(String str) {
//        if (!isAvrAssembler()) {
//            return;
//        }
//        StringTokenizer tokenizer = new StringTokenizer(str, " \t\n\r");
//        boolean found = false;
//        while (tokenizer.hasMoreElements()) {
//            String instruction = tokenizer.nextToken();
//            if (instruction.endsWith(":") || instruction.startsWith(";") || instruction.startsWith("//")) {
//                continue;
//            }
//            String description = AvrAssemblerCommandsHelper.getCommandDescription(instruction);
//            if (description != null) {
//                setStatusMessage(description);
//                found = true;
//                break;
//            }
//        }
//        if (!found) {
//            setStatusMessage("");
//        }
//    }


    private void checkColorOnCursor(String str, int col) {
        if (str.length() < 6 || col >= str.length()) {
            clearStatusColor();
            return;
        }
        char ch = str.charAt(col);
        if (isHexDigit(ch)) {
            String word = "" + ch;
            for (int pos = col-1; pos >= 0; pos--) {
                char c = str.charAt(pos);
                if (isHexDigit(c)) {
                    word = c + word;
                } else {
                    break;
                }
            }
            for (int pos = col+1; pos < str.length(); pos++) {
                char c = str.charAt(pos);
                if (isHexDigit(c)) {
                    word = word + c;
                } else {
                    break;
                }
            }
            if (word.length() == 8) {
                word = word.substring(2);
            }
            if (word.length() == 6) {
                try {
                    setStatusColor(Integer.parseInt(word, 16));
                    return;
                } catch (Exception ignore) { }
            }
        }
        clearStatusColor();
    }


    private static boolean isHexDigit(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f');
    }

    private void setStatusMessage(String msg) {
        org.fife.ui.StatusBar statusBar = rText.getStatusBar();
        if (statusBar != null) {
            statusBar.setStatusMessage(msg);
        }
    }

    private void setStatusColor(int color) {
//        StatusBar statusBar = textEditor.getStatusBar();
//        if (statusBar != null) {
//            statusBar.setColor(color);
//        }
    }

    private void clearStatusColor() {
        setStatusColor(-1);
    }

//    private boolean isAvrAssembler() {
//        return textEditor.getTextArea().getFileType() == FileType.ASSEMBLER_AVR;
//    }

    private File getIncludeFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        File selectedFile = new File(getCurrentFile().getParent() + File.separator + fileName);
        if (selectedFile.exists()) {
            return selectedFile;
        }
        return null;
    }

    private File getCurrentFile() {
        return new File(textArea.getFileFullPath());
    }
}
