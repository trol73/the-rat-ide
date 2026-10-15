package org.fife.rsta.ac.c;

import org.fife.rtext.RText;
import ru.trolsoft.ide.utils.ProjectUtils;
import ru.trolsoft.ide.utils.search.OccurrenceFilter;
import ru.trolsoft.ide.utils.search.ParallelSearch;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CDeclarationFinder {
    private final RText rtext;
    private final ParallelSearch searcher = new ParallelSearch();
    private static final List<String> FILE_MASKS = List.of("*.c", "*.h", "*.art");

    OccurrenceFilter filter = (file, ext, line) -> {
        if ("c".equalsIgnoreCase(ext)) {
            return !line.trim().startsWith("//");
        }
        return true;
    };

    public CDeclarationFinder(RText rtext) {
        this.rtext = rtext;
    }

    public Declaration find(String identifier) {
        var project = ProjectUtils.getProjectForCurrentFile(rtext);
//        project.getWorkspace().
//        var thread = new FindIdentifierThread()
        var folders = ProjectUtils.getActiveProjectFolders(rtext);
//        System.out.println(folders);
        synchronized (searcher) {
            try {
                var text = identifier;
                var results = searcher.search(folders, text, FILE_MASKS, true, false, filter);
//    System.err.println(identifier + " -> " + results.size());
                for (var r : results) {
                    var fn = r.getFile().getName();
                    if (fn.endsWith(".c") || fn.endsWith(".h")) {
                        r.setText(normalizeLine(r.getText(), "//"));
                    } else if (fn.endsWith(".art") || fn.endsWith(".arth")) {
                        r.setText(normalizeLine(r.getText(), ";"));
                    }
//System.out.println(r.getText());
                }

                for (var r : results) {
                    var s = r.getText();
                    if (
                            s.endsWith(" " + identifier + "() {") ||
                            s.startsWith("} "+ identifier + ";") ||
                            s.startsWith("#define "+ identifier)
                    ) {
//    System.err.println(s);
                        return new Declaration(r.getFile(), r.getLine(), r.getColumn());
                    }
                }

                for (var r : results) {
                    var s = r.getText();
                    if (
                            s.endsWith(") {") && s.contains(" " + identifier + "(")
                    ) {
//        System.err.println(s);
                        return new Declaration(r.getFile(), r.getLine(), r.getColumn());
                    }
                }


                // ends ' ' + identifier + "() {"
                // ends ' ' + identifier + "() {"
                // '}' identifier + ";"
                // "#define " + identifier + ...
//                uint16_t FioReadWord() {
            } catch (InterruptedException e) {
                return null;
            } catch (ExecutionException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Нормализует строку:
     * 1. Заменяет все последовательности пробелов и табов на один пробел.
     * 2. Удаляет хвостовой комментарий, начинающийся со строки commentFrom.
     * 3. Убирает начальные и конечные пробелы/табы.
     *
     * @param line       исходная строка
     * @param commentFrom строка, с которой начинается комментарий (например, "//" или "/*")
     * @return нормализованная строка
     */
    public static String normalizeLine(String line, String commentFrom) {
        if (line == null) {
            return null;
        }

        // 1. Удаляем хвостовой комментарий (ищем commentFrom вне строкового литерала)
        int commentIdx = indexOfOutsideStringLiteral(line, commentFrom);
        if (commentIdx >= 0) {
            line = line.substring(0, commentIdx);
        }

        // 2. Сжимаем все последовательности пробелов и табов в один пробел
        line = line.replaceAll("[ \\t]+", " ");

        // 3. Убираем начальные и конечные пробелы
        line = line.trim();

        return line;
    }

    /**
     * Ищет подстроку needle в строке, игнорируя вхождения внутри
     * строковых литералов ("..." и '...'). Возвращает индекс или -1.
     */
    private static int indexOfOutsideStringLiteral(String s, String needle) {
        if (needle == null || needle.isEmpty()) {
            return -1;
        }
        char quote = 0;
        boolean escaped = false;
        for (int i = 0; i <= s.length() - needle.length(); i++) {
            char c = s.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (quote != 0) {
                if (c == quote) {
                    quote = 0;
                }
                continue;
            }
            if (c == '"' || c == '\'') {
                quote = c;
                continue;
            }
            if (s.regionMatches(i, needle, 0, needle.length())) {
                return i;
            }
        }
        return -1;
    }
}


