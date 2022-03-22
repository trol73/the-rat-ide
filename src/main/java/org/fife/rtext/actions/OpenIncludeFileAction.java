package org.fife.rtext.actions;

import org.fife.rtext.RText;
import org.fife.ui.app.AppAction;

import java.awt.event.ActionEvent;
import java.io.File;

public class OpenIncludeFileAction extends AppAction<RText>  {
    public OpenIncludeFileAction(RText rtext) {
        super(rtext, rtext.getResourceBundle(), "OpenIncludeFile");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File file = getApplication().getSelectedIncludedFile();
        if (file != null) {
            getApplication().openFile(file);
        }
    }
}
