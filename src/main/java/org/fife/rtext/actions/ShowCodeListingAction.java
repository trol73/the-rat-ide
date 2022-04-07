package org.fife.rtext.actions;

import org.fife.rtext.RText;
import org.fife.ui.app.AppAction;
import ru.trolsoft.ide.utils.ProjectUtils;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

public class ShowCodeListingAction extends AppAction<RText> {

    public ShowCodeListingAction(RText app, ResourceBundle msg) {
        super(app, msg, "CodeListingAction");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RText rtext = getApplication();

        var window = rtext.getCodeListingWindow();
        var project = ProjectUtils.getProjectForCurrentFile(rtext);
        if (project == null) {
            return;
        }
        rtext.getCodeListingPlugin().setCodeListingWindowVisible(true);
//        if (!window.isActive()) {
            window.focusInDockableWindowGroup(true);
//        }
//        window.load();
    }
}
