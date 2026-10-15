package org.fife.rsta.ac.c;

import java.io.File;
import java.util.Objects;

public class Declaration {
    private final File file;
    private final int line;      // 1-based
    private final int column;    // 1-based


    public Declaration(File file, int line, int column) {
        this.file = Objects.requireNonNull(file, "file");
        if (line < 1) {
            throw new IllegalArgumentException("line must be >= 1: " + line);
        }
        if (column < 1) {
            throw new IllegalArgumentException("column must be >= 1: " + column);
        }
        this.line = line;
        this.column = column;
    }

    public File getFile() {
        return file;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }


    // --- 0-based представление для RSyntaxTextArea ---

    public int getLine0() {
        return line - 1;
    }

    public int getColumn0() {
        return column - 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Declaration that)) return false;
        return line == that.line
                && column == that.column
                && file.equals(that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, line, column);
    }

    @Override
    public String toString() {
        return "Declaration{" + file.getName() + ":" + line + ":" + column + "}";
    }
}
