package org.fife.rtext.actions;

import org.fife.rtext.AbstractMainView;
import org.fife.rtext.RText;
import org.fife.rtext.RTextEditorPane;
import org.fife.ui.app.AppAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ResourceBundle;

public class SwitchSourceHeaderAction extends AppAction<RText> {
    public SwitchSourceHeaderAction(RText owner, ResourceBundle msg, Icon icon) {
        super(owner, msg, "SwitchSourceHeaderAction");
        setIcon(icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AbstractMainView mainView = getApplication().getMainView();
        RTextEditorPane editor = mainView.getCurrentTextArea();
        if (editor == null) {
            return;
        }
        String currentFilePath = editor.getFileFullPath();
        int lastDot = currentFilePath.lastIndexOf('.');
        if (lastDot < 0) {
            return;
        }
        String fileWithoutExt = currentFilePath.substring(0, lastDot+1);
        if (currentFilePath.endsWith(".h")) {
            if (switchTo(new File(fileWithoutExt + "c")))
                return;
            else if (switchTo(new File(fileWithoutExt + "cpp")))
                return;
        } else if (currentFilePath.endsWith(".c")) {
            if (switchTo(new File(fileWithoutExt + "h")))
                return;
        } else if (currentFilePath.endsWith(".cpp")) {
            if (switchTo(new File(fileWithoutExt + "h")))
                return;
            else if (switchTo(new File(fileWithoutExt + "hpp")))
                return;
        }
    }


    private boolean switchTo(File file) {
        if (!file.exists()) {
            return false;
        }
        if (!getApplication().isFileOpen(file)) {
            var current = getApplication().getMainView().getCurrentTextArea();
            if (!current.isDirty()) {
                getApplication().getMainView().closeCurrentDocument();
            }
        }
        getApplication().openFile(file);
        return true;
    }


}
