package org.fife.rsta.ac.c;

import java.io.File;
import java.lang.ref.WeakReference;

import org.fife.rtext.RText;
import org.fife.ui.rsyntaxtextarea.*;
import ru.trolsoft.rsyntaxtextarea.FileLinkGeneratorResult;

public class CLinkGenerator implements LinkGenerator {

    private final CDeclarationFinder finder;
    private final RText rtext;

    // Кэш последнего токена
    private WeakReference<RSyntaxTextArea> lastTextAreaRef = new WeakReference<>(null);
    private int lastTokenStart = -1;
    private int lastTokenEnd   = -1;

    private boolean lastIsLink = false;
    private File lastDeclFile;
    private int lastDeclLine;
    private int lastDeclColumn;

    public CLinkGenerator(RText rtext) {
        this.finder = new CDeclarationFinder(rtext);
        this.rtext = rtext;
    }

    @Override
    public LinkGeneratorResult isLinkAtOffset(RSyntaxTextArea textArea, int offs) {
        Token token = findIdentifierTokenAt(textArea, offs);
        if (token == null) {
            resetCache();
            return null;
        }

        int tokenStart = token.getOffset();
        int tokenEnd   = tokenStart + token.length();

        if (isCached(textArea, tokenStart, tokenEnd)) {
            return buildCachedResult();
        }

        return lookupAndCache(textArea, token, tokenStart, tokenEnd);
    }

    // --- Кэш ---

    private boolean isCached(RSyntaxTextArea textArea, int tokenStart, int tokenEnd) {
        return textArea == lastTextAreaRef.get()
                && tokenStart == lastTokenStart
                && tokenEnd == lastTokenEnd;
    }

    private LinkGeneratorResult buildCachedResult() {
        if (!lastIsLink) {
            return null;
        }
        return new FileLinkGeneratorResult(
                rtext,
                lastDeclFile,
                lastDeclLine,
                lastDeclColumn);
    }

    private void updateCache(RSyntaxTextArea textArea, int tokenStart, int tokenEnd,
                             boolean isLink, File declFile,
                             int declLine, int declColumn) {
        lastTextAreaRef = new WeakReference<>(textArea);
        lastTokenStart = tokenStart;
        lastTokenEnd   = tokenEnd;
        lastIsLink     = isLink;
        lastDeclFile   = declFile;
        lastDeclLine   = declLine;
        lastDeclColumn = declColumn;
    }

    private void resetCache() {
        lastTextAreaRef = new WeakReference<>(null);
        lastTokenStart = -1;
        lastTokenEnd   = -1;
        lastIsLink     = false;
        lastDeclFile   = null;
        lastDeclLine   = -1;
        lastDeclColumn = -1;
    }

    // --- Поиск ---

    private LinkGeneratorResult lookupAndCache(RSyntaxTextArea textArea, Token token,
                                               int tokenStart, int tokenEnd) {
        updateCache(textArea, tokenStart, tokenEnd, false, null, -1, -1);

        String identifier = token.getLexeme();
        Declaration decl = finder.find(identifier);
        if (decl == null) {
            return null;
        }

        updateCache(textArea, tokenStart, tokenEnd, true, decl.getFile(), decl.getLine(), decl.getColumn());

        return new FileLinkGeneratorResult(
                rtext,
                decl.getFile(),
                decl.getLine(),
                decl.getColumn());
    }

    // --- Токены ---

    private Token findIdentifierTokenAt(RSyntaxTextArea textArea, int offs) {
        int docLength = textArea.getDocument().getLength();
        if (offs < 0 || offs >= docLength) {
            return null;
        }
        try {
            int line = textArea.getLineOfOffset(offs);
            for (Token t = textArea.getTokenListForLine(line);
                 t != null && t.getType() != TokenTypes.NULL;
                 t = t.getNextToken()) {

                if (t.getType() != TokenTypes.IDENTIFIER) {
                    continue;
                }
                int start = t.getOffset();
                int end   = start + t.length();
                if (offs >= start && offs < end) {
                    return t;
                }
            }
        } catch (Exception ex) {
            // isLinkAtOffset вызывается очень часто — не даём ему падать
        }
        return null;
    }
}