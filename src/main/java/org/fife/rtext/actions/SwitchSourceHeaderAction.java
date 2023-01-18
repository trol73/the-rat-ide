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
            File cFile = new File(fileWithoutExt + "c");
            if (cFile.exists()) {
                getApplication().openFile(cFile);
                return;
            }
            File cppFile = new File(fileWithoutExt + "cpp");
            if (cppFile.exists()) {
                getApplication().openFile(cppFile);
            }
        } else if (currentFilePath.endsWith(".c")) {
            File hFile = new File(fileWithoutExt + "h");
            if (hFile.exists()) {
                getApplication().openFile(hFile);
            }
        } else if (currentFilePath.endsWith(".cpp")) {
            File hFile = new File(fileWithoutExt + "h");
            if (hFile.exists()) {
                getApplication().openFile(hFile);
                return;
            }
            File hppFile = new File(fileWithoutExt + "hpp");
            if (hppFile.exists()) {
                getApplication().openFile(hppFile);
            }
        }
    }

}
