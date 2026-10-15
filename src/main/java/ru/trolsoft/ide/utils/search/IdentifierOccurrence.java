package ru.trolsoft.ide.utils.search;

import java.io.File;

/**
 * Одно вхождение идентификатора в файле.
 * line и column — 1-based, как в редакторах.
 * text — полная строка файла, в которой найдено вхождение.
 */
public final class IdentifierOccurrence {

    private final File file;
    private final int line;      // 1-based
    private final int column;    // 1-based
    private String text;   // полная строка файла

    public IdentifierOccurrence(File file, int line, int column, String text) {
        this.file = file;
        this.line = line;
        this.column = column;
        this.text = text;
    }

    public File getFile()   { return file; }
    public int getLine()    { return line; }
    public int getColumn()  { return column; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    @Override
    public String toString() {
        return file.getAbsolutePath() + ":" + line + ":" + column
                + "  " + text;
    }
}