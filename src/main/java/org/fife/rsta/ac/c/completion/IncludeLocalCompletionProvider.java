package org.fife.rsta.ac.c.completion;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IncludeLocalCompletionProvider extends DefaultCompletionProvider {

    private final List<String> headers = new ArrayList<>();

    public IncludeLocalCompletionProvider(String path) {
        var folder = new File(path).getParentFile();
        var completions = new ArrayList<Completion>();
        scanHeaders(folder);
        var folderPath = folder.getAbsolutePath();
        for (String header : headers) {
            if (header.startsWith(folderPath)) {
                var name = header.substring(folderPath.length());
                if (name.startsWith("/") || name.startsWith("\\")) {
                    name = name.substring(1);
                }
                completions.add(new BasicCompletion(this, name + '"'));
            }
        }
        addCompletions(completions);
    }

    private void scanHeaders(File folder) {
        boolean stop = false;
        for (File f : folder.listFiles()) {
            var name = f.getName().toLowerCase();
            if (f.isDirectory()) {
                if (!stop) {
                    scanHeaders(f);
                }
            } else if (isHeader(name)) {
                headers.add(f.getAbsolutePath());
            } else if (isRoot(name)) {
                stop = true;
            }
        }
    }

    private static boolean isHeader(String fileName) {
        return fileName.endsWith(".h") || fileName.endsWith(".hpp");
    }

    private static boolean isRoot(String fileName) {
        return fileName.endsWith("Makefile") || fileName.endsWith("make.builder");
    }
}
