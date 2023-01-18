package org.fife.rsta.ac.c.completion;

import org.fife.rtext.plugins.project.model.Project;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

import java.util.ArrayList;
import java.util.List;

public class IncludeGlobalCompletionProvider extends DefaultCompletionProvider {
    private final List<Completion> completionList = new ArrayList<>();
    public IncludeGlobalCompletionProvider(Project project) {
        if (project == null || project.getDevice().equals("<none>")) {
            initDefaultIncludes();
        }
        addCompletions(completionList);
    }
// /usr/local/include/
// /opt/homebrew/Cellar/avr-gcc@9/9.3.0_3/
// /opt/homebrew/Cellar/mingw-w64/10.0.0_3/toolchain-i686/i686-w64-mingw32/include/
// /opt/homebrew/Cellar/mingw-w64/10.0.0_3/toolchain-x86_64/x86_64-w64-mingw32/include/
// /Library/Developer/CommandLineTools/usr/include/c++/v1/
    private void initDefaultIncludes() {
        completion("assert.h");
        completion("stddef.h");
        completion("stdint.h");
        completion("stdbool.h");
        completion("stdlib.h");
        completion("stdio.h");
        completion("strings.h");
        //libserialport.h
        //zip.h
    }

    private void completion(String file) {
        completionList.add(new BasicCompletion(this, file + ">"));
    }
}
