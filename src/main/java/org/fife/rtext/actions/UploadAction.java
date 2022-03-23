package org.fife.rtext.actions;

import org.fife.rtext.AbstractMainView;
import org.fife.rtext.RText;
import org.fife.rtext.RTextEditorPane;
import org.fife.rtext.plugins.buildoutput.BuildOutputWindow;
import org.fife.rtext.plugins.project.model.Project;
import org.fife.ui.app.AppAction;
import ru.trolsoft.ide.builder.ProjectUploader;
import ru.trolsoft.ide.utils.ProjectUtils;

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
//System.out.println("BUILD " + path);
        BuildOutputWindow window = rtext.getBuildOutputWindow();
        Project project = ProjectUtils.getProjectForCurrentFile(rtext);
        var uploader = new ProjectUploader(rtext, project, window);
        uploader.upload();
    }
}
