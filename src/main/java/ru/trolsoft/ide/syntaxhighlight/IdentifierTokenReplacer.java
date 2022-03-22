package ru.trolsoft.ide.syntaxhighlight;

import org.fife.ui.rsyntaxtextarea.Token;
import ru.trolsoft.ide.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class IdentifierTokenReplacer {
    private final String[] names;
    private final int[] hashes;
    private final int[] tokenTypes;

    IdentifierTokenReplacer(String[] names, int[] hashes, int[] tokenTypes) {
        this.names = names;
        this.hashes = hashes;
        this.tokenTypes = tokenTypes;
    }

    public int getTokenType(char[] array, int start, int end) {
        int hash = StringUtils.hash(array, start, end);
        int pos = Arrays.binarySearch(hashes, hash);
        if (pos >= 0) {
            return tokenTypes[pos];
        } else {
            return Token.IDENTIFIER;
        }
    }



    public static class Builder {
        private final List<Record> records = new ArrayList<>();

        public void add(String name, int tokenType) {
            records.add(new Record(name, tokenType));
        }

        public IdentifierTokenReplacer build() {
            records.sort(Comparator.comparingInt(r -> r.hash));
            int n = records.size();
            String[] names = new String[n];
            int[] hashes = new int[n];
            int[] tokenTypes = new int[n];
            for (int i = 0; i < n; i++) {
                Record r = records.get(i);
                names[i] = r.name;
                hashes[i] = r.hash;
                tokenTypes[i] = r.tokenType;
            }
            return new IdentifierTokenReplacer(names, hashes, tokenTypes);
        }

        private static class Record {
            final String name;
            final int hash;
            final int tokenType;

            Record(String name, int tokenType) {
                this.name = name;
                this.hash = StringUtils.hash(name);
                this.tokenType = tokenType;
            }
        }


    }
}
