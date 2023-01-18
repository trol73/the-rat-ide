/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.c;

import org.fife.rsta.ac.c.completion.IncludeGlobalCompletionProvider;
import org.fife.rsta.ac.c.completion.IncludeLocalCompletionProvider;
import org.fife.rtext.RTextEditorPane;
import org.fife.rtext.plugins.project.model.Project;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import ru.trolsoft.ide.utils.ProjectUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A completion provider for the C programming language.  It provides
 * code completion support and parameter assistance for the C Standard Library.
 * This information is read from an XML file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CCompletionProvider extends LanguageAwareCompletionProvider {

    private final CompletionProvider preprocessorCompletionProvider;

    /**
     * Constructor.
     */
    public CCompletionProvider() {
        setDefaultCompletionProvider(createCodeCompletionProvider());
        setStringCompletionProvider(createStringCompletionProvider());
        setCommentCompletionProvider(createCommentCompletionProvider());
        preprocessorCompletionProvider = createPreprocessorCompletionProvider();
    }


    /**
     * Adds shorthand completions to the code completion provider.
     *
     * @param codeCP The code completion provider.
     */
    protected void addShorthandCompletions(DefaultCompletionProvider codeCP) {
        codeCP.addCompletion(new ShorthandCompletion(codeCP, "main", "int main(int argc, char **argv)"));
//for (int i=0; i<5000; i++) {
//	codeCP.addCompletion(new BasicCompletion(codeCP, "Number" + i));
//}
    }


    /**
     * Returns the provider to use when editing code.
     *
     * @return The provider.
     * @see #createCommentCompletionProvider()
     * @see #createStringCompletionProvider()
     * @see #loadCodeCompletionsFromXml(DefaultCompletionProvider)
     * @see #addShorthandCompletions(DefaultCompletionProvider)
     */
    protected CompletionProvider createCodeCompletionProvider() {
        DefaultCompletionProvider cp = new DefaultCompletionProvider();
        loadCodeCompletionsFromXml(cp);
        addShorthandCompletions(cp);
        return cp;
    }

    protected CompletionProvider createPreprocessorCompletionProvider() {
        DefaultCompletionProvider cp = new DefaultCompletionProvider();
        List<Completion> list = new ArrayList<>();
        list.add(new BasicCompletion(cp, "include"));
        list.add(new BasicCompletion(cp, "define"));
        list.add(new BasicCompletion(cp, "undef"));
        list.add(new BasicCompletion(cp, "ifdef"));
        list.add(new BasicCompletion(cp, "ifndef"));
        list.add(new BasicCompletion(cp, "if"));
        list.add(new BasicCompletion(cp, "else"));
        list.add(new BasicCompletion(cp, "elif"));
        list.add(new BasicCompletion(cp, "line"));
        list.add(new BasicCompletion(cp, "pragma"));
        cp.addCompletions(list);
        return cp;
    }


    /**
     * Returns the provider to use when in a comment.
     *
     * @return The provider.
     * @see #createCodeCompletionProvider()
     * @see #createStringCompletionProvider()
     */
    protected CompletionProvider createCommentCompletionProvider() {
        DefaultCompletionProvider cp = new DefaultCompletionProvider();
        cp.addCompletion(new BasicCompletion(cp, "TODO:", "A to-do reminder"));
        cp.addCompletion(new BasicCompletion(cp, "FIXME:", "A bug that needs to be fixed"));
        return cp;
    }


    /**
     * Returns the completion provider to use when the caret is in a string.
     *
     * @return The provider.
     * @see #createCodeCompletionProvider()
     * @see #createCommentCompletionProvider()
     */
    protected CompletionProvider createStringCompletionProvider() {
        DefaultCompletionProvider cp = new DefaultCompletionProvider();
        cp.addCompletion(new BasicCompletion(cp, "%c", "char", "Prints a character"));
        cp.addCompletion(new BasicCompletion(cp, "%i", "signed int", "Prints a signed integer"));
        cp.addCompletion(new BasicCompletion(cp, "%f", "float", "Prints a float"));
        cp.addCompletion(new BasicCompletion(cp, "%s", "string", "Prints a string"));
        cp.addCompletion(new BasicCompletion(cp, "%u", "unsigned int", "Prints an unsigned integer"));
        cp.addCompletion(new BasicCompletion(cp, "\\n", "Newline", "Prints a newline"));
        return cp;
    }

    @Override
    protected CompletionProvider getCompletionProvider(RTextEditorPane editor, Token firstLineToken,
                                                       Token lastPaintedToken) {
//System.out.println("} " +firstLineToken + " " + lastPaintedToken);
        if (isTokenStarted(firstLineToken, TokenTypes.ERROR_IDENTIFIER, "#")) {
            return preprocessorCompletionProvider;
        }  else if (isToken(firstLineToken, TokenTypes.PREPROCESSOR, "#include") && editor != null) {
            if (lastPaintedToken.getType() == TokenTypes.ERROR_STRING_DOUBLE) {
                return createIncludeLocalCompletionProvider(editor.getFileFullPath());
            } else if (isToken(lastPaintedToken, TokenTypes.OPERATOR, "<")) {
                return createIncludeGlobalCompletionProvider(editor.getProject());
            }
        }
        return null;
    }

    private CompletionProvider createIncludeGlobalCompletionProvider(Project project) {
        return new IncludeGlobalCompletionProvider(project);
    }

    private CompletionProvider createIncludeLocalCompletionProvider(String fileFullPath) {
        return new IncludeLocalCompletionProvider(fileFullPath);
    }

    private static boolean isToken(Token token, int type, String text) {
        return token.getType() == type && token.getLexeme().equals(text);
    }

    private static boolean isTokenStarted(Token token, int type, String text) {
        return token.getType() == type && token.getLexeme().startsWith(text);
    }


    /**
     * Returns the name of the XML resource to load (on classpath or a file).
     *
     * @return The resource to load.
     */
    protected String getXmlResource() {
        return "data/c.xml";
    }


    /**
     * Called from {@link #createCodeCompletionProvider()} to actually load
     * the completions from XML.  Subclasses that override that method will
     * want to call this one.
     *
     * @param cp The code completion provider.
     */
    protected void loadCodeCompletionsFromXml(DefaultCompletionProvider cp) {
        // First try loading resource (running from demo jar), then try
        // accessing file (debugging in Eclipse).
        ClassLoader cl = getClass().getClassLoader();
        String res = getXmlResource();
        if (res != null) { // Subclasses may specify a null value
            InputStream in = cl.getResourceAsStream(res);
            try {
                if (in != null) {
                    cp.loadFromXML(in);
                    in.close();
                } else {
                    cp.loadFromXML(new File(res));
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }


}