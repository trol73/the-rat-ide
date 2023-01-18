package org.fife.rtext.plugins.buildoutput;


import org.fife.rtext.RText;
import ru.trolsoft.ide.utils.ProjectUtils;
import ru.trolsoft.ide.utils.StringUtils;

import java.io.File;
import java.util.StringTokenizer;

public class OutputLineParser {
    public interface Handler {
        void handle(String text, String style);
    }

    private final String text;
    private final Handler handler;
    private final StringTokenizer tokenizer;
    private boolean error;
    private boolean warning;
    private boolean afterFile;
    private boolean afterFileLine;
    private int offset = 0;

    private String filePath;


    private int fileLine = -1;
    private int fileColumn = -1;

    private final RText rText;

    public OutputLineParser(RText rText, String text, Handler handler) {
        this.text = text;
        this.handler = handler;
        this.rText = rText;
        this.tokenizer = new StringTokenizer(text, ":", true);
    }

    public OutputLineParser parse(String style) {
        while (hasMoreTokens()) {
            String part = nextToken();

            if (isFilePath(part.trim())) {
                handle(part, BuildOutputTextArea.STYLE_FILE);
                filePath = part.trim();
                afterFile = true;
            } else if (isCurrentProjectSource(part.trim())) {
                handle(part, BuildOutputTextArea.STYLE_FILE);
                filePath = filePathWithCurrentProjectFolder(part.trim());
                afterFile = true;
            } else if (":".equals(part)) {
                String newStyle = style;
                if (afterFileLine) {
                    newStyle = BuildOutputTextArea.STYLE_FILE_POS;
                } else if (error) {
                    newStyle = BuildOutputTextArea.STYLE_ERRORS;
                } else if (warning) {
                    newStyle = BuildOutputTextArea.STYLE_WARNINGS;
                }
                handle(part, newStyle);
            } else if (afterFile && StringUtils.isValidInt(part)) {
                if (fileLine < 0) {
                    fileLine = Integer.parseInt(part);
                } else if (fileColumn < 0) {
                    fileColumn = Integer.parseInt(part);
                }
                handle(part, BuildOutputTextArea.STYLE_FILE_POS);
                afterFileLine = true;
            } else if ("warning".equalsIgnoreCase(part.trim())) {
                warning = true;
                afterFile = false;
                afterFileLine = false;
                handle(part, BuildOutputTextArea.STYLE_WARNINGS);
            } else if ("error".equalsIgnoreCase(part.trim())) {
                error = true;
                afterFile = false;
                afterFileLine = false;
                handle(part, BuildOutputTextArea.STYLE_ERRORS);
            } else {
                afterFile = false;
                afterFileLine = false;
                String newStyle = style;
                if (error) {
                    newStyle = BuildOutputTextArea.STYLE_ERRORS;
                } else if (warning) {
                    newStyle = BuildOutputTextArea.STYLE_WARNINGS;
                }
                handle(part, newStyle);
            }
        }
        return this;
    }

    private String filePathWithCurrentProjectFolder(String name) {
        var project = ProjectUtils.getProjectForCurrentFile(rText);
        if (project == null) {
            return null;
        }
        var folder = ProjectUtils.projectGetFirstFolder(project);
        if (folder.isEmpty()) {
            return null;
        }
        var fullPath = folder.get().getAbsolutePath() + File.separatorChar + name;
        if (new File(fullPath).exists()) {
            return fullPath;
        }
        fullPath = folder.get().getParent() + File.separatorChar + name;
        if (new File(fullPath).exists()) {
            return fullPath;
        }
        return null;
    }

    private void handle(String text, String style) {
        if (handler != null) {
            handler.handle(text, style);
        }
    }

    private String nextToken() {
        String res = tokenizer.nextToken();
        offset += res.length();
        return res;
    }

    private boolean hasMoreTokens() {
        return tokenizer.hasMoreTokens();
    }

    public String getFilePath() {
        return filePath;
    }

    public int getFileLine() {
        return fileLine;
    }

    public int getFileColumn() {
        return fileColumn;
    }


    private static boolean isFilePath(String s) {
        if (s.contains(" ") || !(s.contains("\\") || s.contains("/"))) {
            return false;
        }
        File file = new File(s);
        return file.exists() && file.isFile();
    }

    private boolean isCurrentProjectSource(String s) {
        return !s.contains(" ") && (s.endsWith(".c") || s.endsWith(".cpp") || s.endsWith(".h"));
    }

    public boolean isError() {
        return error;
    }

    public boolean isWarning() {
        return warning;
    }

}
