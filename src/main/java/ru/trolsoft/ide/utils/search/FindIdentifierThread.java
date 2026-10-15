package ru.trolsoft.ide.utils.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

import org.fife.io.UnicodeReader;

public class FindIdentifierThread extends Thread {

    private final File rootDirectory;
    private final String identifier;
    private final Pattern[] fileMasks;
    private final boolean caseSensitive;
    private final boolean skipSubfolders;
    private final OccurrenceFilter filter;

    private final List<IdentifierOccurrence> results = new ArrayList<>();
    private volatile boolean cancelled;

    public FindIdentifierThread(File rootDirectory,
                                String identifier,
                                List<String> wildcardMasks,
                                boolean caseSensitive,
                                boolean skipSubfolders) {
        this(rootDirectory, identifier, wildcardMasks,
                caseSensitive, skipSubfolders, null);
    }

    public FindIdentifierThread(File rootDirectory,
                                String identifier,
                                List<String> wildcardMasks,
                                boolean caseSensitive,
                                boolean skipSubfolders,
                                OccurrenceFilter filter) {
        this.rootDirectory = rootDirectory;
        this.identifier = identifier;
        this.caseSensitive = caseSensitive;
        this.skipSubfolders = skipSubfolders;
        this.filter = filter;
        this.fileMasks = compileMasks(wildcardMasks, caseSensitive);
    }

    public List<IdentifierOccurrence> getResults() {
        return results;
    }

    public void cancel() {
        cancelled = true;
        interrupt();
    }

    @Override
    public void run() {
        Deque<File> queue = new ArrayDeque<>();
        queue.add(rootDirectory);

        while (!queue.isEmpty() && !cancelled) {
            File current = queue.poll();
            File[] children = current.listFiles();
            if (children == null) {
                continue;
            }
            for (File child : children) {
                if (cancelled) {
                    return;
                }
                if (child.isDirectory()) {
                    if (!skipSubfolders) {
                        queue.add(child);
                    }
                } else if (child.isFile() && matchesMask(child.getName())) {
                    searchFile(child);
                }
            }
        }
    }

    // --- Поиск в одном файле ---

    private void searchFile(File file) {
        String extension = getExtension(file.getName());
        try (BufferedReader br = new BufferedReader(new UnicodeReader(file))) {
            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null && !cancelled) {
                searchInLine(file, extension, line, lineNumber);
                lineNumber++;
            }
        } catch (IOException ex) {
            // файл не прочитался — пропускаем
        }
    }

    private void searchInLine(File file, String extension, String line, int lineNumber) {
        String haystack = caseSensitive ? line : line.toLowerCase();
        String needle   = caseSensitive ? identifier : identifier.toLowerCase();

        int from = 0;
        while (from <= haystack.length() - needle.length()) {
            int idx = haystack.indexOf(needle, from);
            if (idx < 0) {
                break;
            }
            if (isWholeWord(haystack, idx, needle.length())) {
                if (filter == null || filter.test(file, extension, line)) {
                    results.add(new IdentifierOccurrence(
                            file, lineNumber, idx + 1, line));
                }
            }
            from = idx + 1;
        }
    }

    private static boolean isWholeWord(String s, int start, int length) {
        if (start > 0 && isIdentifierChar(s.charAt(start - 1))) {
            return false;
        }
        int end = start + length;
        if (end < s.length() && isIdentifierChar(s.charAt(end))) {
            return false;
        }
        return true;
    }

    private static boolean isIdentifierChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    // --- Маски ---

    private boolean matchesMask(String fileName) {
        for (Pattern p : fileMasks) {
            if (p.matcher(fileName).matches()) {
                return true;
            }
        }
        return false;
    }

    private static Pattern[] compileMasks(List<String> wildcards, boolean caseSensitive) {
        int flags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
        Pattern[] result = new Pattern[wildcards.size()];
        for (int i = 0; i < wildcards.size(); i++) {
            result[i] = Pattern.compile(wildcardToRegex(wildcards.get(i)), flags);
        }
        return result;
    }

    private static String wildcardToRegex(String wildcard) {
        String s = wildcard.replace(".", "\\.");
        s = s.replace("*", ".*");
        s = s.replace("?", ".");
        return "^" + s + "$";
    }

    private static String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot >= 0) ? fileName.substring(dot + 1) : "";
    }
}