package org.fife.rtext.actions;

import org.fife.rtext.AbstractMainView;
import org.fife.rtext.RText;
import org.fife.rtext.RTextEditorPane;
import org.fife.ui.app.AppAction;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

public class UploadAction extends AppAction<RText> {

    public UploadAction(RText app, ResourceBundle msg) {
        super(app, msg, "UploadAction");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RText rtext = getApplication();
        AbstractMainView mainView = rtext.getMainView();
        RTextEditorPane editor = mainView.getCurrentTextArea();
        if (editor == null) {
            return;
        }
        String path = editor.getFileFullPath();
        System.out.println("UPLOAD " + path);
    }
}
