package ru.trolsoft.ide.plugins.codelisting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.fife.rtext.RText;

import java.util.HashMap;
import java.util.Map;

public class ListingInfo {
    private final RText rtext;
    private final Map<String, IntArrayList> codeListing = new HashMap<>();
    private final StringBuilder text = new StringBuilder();
    private int textLine = 0;

    public ListingInfo(RText rtext) {
        this.rtext = rtext;
    }

    public void clear() {
        codeListing.clear();
        if (!text.isEmpty()) {
            text.delete(0, text.length() - 1);
        }
        textLine = 0;
    }

    public void add(String sourceFile, int sourceLine, String listData) {
        text.append(listData).append('\n');
        var fileData = codeListing.get(sourceFile);
        if (fileData == null) {
            fileData = new IntArrayList();
            codeListing.put(sourceFile, fileData);
        }
        while (fileData.size() < sourceLine) {
            fileData.add(-1);
        }
        fileData.set(sourceLine-1, textLine);
        textLine++;
    }

    public String getText() {
        return text.toString();
    }

    public int getListLineFor(String sourceFile, int sourceLine) {
        var fileData = codeListing.get(sourceFile);
        if (fileData == null || fileData.size() <= sourceLine-1) {
            return -1;
        }
        return fileData.getInt(sourceLine-1) + 1;
    }



}
