package org.fife.rtext.actions;

import org.fife.rtext.RText;
import org.fife.ui.app.AppAction;
import ru.trolsoft.ide.dialogs.GoToProjectFileDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

public class GotoProjectFileAction extends AppAction<RText> {
    GotoProjectFileAction(RText owner, ResourceBundle msg, Icon icon) {
        super(owner, msg, "GotoProjectFileAction");
        setIcon(icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RText owner = getApplication();
        var dialog = new GoToProjectFileDialog(owner);
        dialog.setVisible(true);
    }

    @Override
    public boolean accept(Object sender) {
        return super.accept(sender);
    }
}
