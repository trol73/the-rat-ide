package org.fife.rtext.actions;

import org.fife.rtext.RText;
import org.fife.rtext.plugins.project.ProjectWindow;
import org.fife.rtext.plugins.project.tree.WorkspaceRootTreeNode;
import org.fife.ui.app.AppAction;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

public class NewProjectAction extends AppAction<RText> {
    public NewProjectAction(RText app, ResourceBundle msg) {
        super(app, msg, "NewProjectAction");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RText rtext = getApplication();
        ProjectWindow projectWindow = rtext.getProjectPlugin().getDockableWindow();
        var root = (WorkspaceRootTreeNode)projectWindow.getTree().getModel().getRoot();
        root.createNewProject(e);
    }
}
