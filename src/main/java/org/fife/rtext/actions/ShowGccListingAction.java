package org.fife.rtext.actions;

import org.fife.rtext.AbstractMainView;
import org.fife.rtext.RText;
import org.fife.rtext.RTextEditorPane;
import org.fife.ui.app.AppAction;
import ru.trolsoft.ide.gcc.AvrGccMapUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

public class ShowGccListingAction extends AppAction<RText> {
    public ShowGccListingAction(RText owner, ResourceBundle msg, Icon icon) {
        super(owner, msg, "ShowGccListingAction");
        setIcon(icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RText rtext = getApplication();
        AbstractMainView mainView = getApplication().getMainView();
        RTextEditorPane editor = mainView.getCurrentTextArea();
        AvrGccMapUtils.showGccListing(editor, rtext);
    }
}
