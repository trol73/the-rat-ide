package org.fife.rtext.actions;

import org.fife.rtext.AbstractMainView;
import org.fife.rtext.RText;
import org.fife.rtext.RTextEditorPane;
import org.fife.rtext.plugins.buildoutput.BuildOutputWindow;
import org.fife.rtext.plugins.project.model.Project;
import org.fife.ui.app.AppAction;
import ru.trolsoft.ide.builder.ProjectBuilder;
import ru.trolsoft.ide.utils.ProjectUtils;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

public class BuildAction extends AppAction<RText>  {

    public BuildAction(RText app, ResourceBundle msg) {
        super(app, msg, "BuildAction");
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
//System.out.println("BUILD " + path);
        BuildOutputWindow window = rtext.getBuildOutputWindow();
        Project project = ProjectUtils.getProjectForCurrentFile(rtext);
        ProjectBuilder builder = new ProjectBuilder(rtext, project, window);
        rtext.getBuildOutputPlugin().setBuildOutputWindowVisible(true);
        if (!window.isActive()) {
            window.focusInDockableWindowGroup(true);
        }
        builder.build(path);
    }
}
