package ru.trolsoft.ide.utils;

import org.fife.rtext.AbstractMainView;
import org.fife.rtext.RText;
import org.fife.rtext.RTextEditorPane;
import org.fife.rtext.plugins.project.model.FolderProjectEntry;
import org.fife.rtext.plugins.project.model.Project;
import org.fife.rtext.plugins.project.model.ProjectEntry;
import org.fife.rtext.plugins.project.model.Workspace;

import java.io.File;
import java.util.Iterator;
import java.util.Optional;

public class ProjectUtils {

    public static Project getProjectForCurrentFile(RText rtext) {
        AbstractMainView mainView = rtext.getMainView();
        RTextEditorPane editor = mainView.getCurrentTextArea();
        Workspace workspace = rtext.getWorkspace();
        if (editor == null || workspace == null) {
            return null;
        }
        String path = editor.getFileFullPath();
        return getProjectForFile(path, workspace);
    }

    public static Project getProjectForFile(String filePath, RText rtext) {
        Workspace workspace = rtext.getWorkspace();
        return workspace == null ? null : getProjectForFile(filePath, workspace);
    }

    public static Project getProjectForFile(String filePath, Workspace workspace) {
        Iterator<Project> it = workspace.getProjectIterator();
        while (it.hasNext()) {
            Project prj = it.next();
            if (projectHasFile(prj, filePath)) {
                return prj;
            }
        }
        return null;
    }

    public static boolean projectHasFile(Project prj, String filePath) {
        Iterator<ProjectEntry> it = prj.getEntryIterator();
        while (it.hasNext()) {
            ProjectEntry entry = it.next();
            if (entry instanceof FolderProjectEntry e) {
                if (filePath.startsWith(e.getFile().getAbsolutePath())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Optional<File> projectGetFirstFolder(Project prj) {
        Iterator<ProjectEntry> it = prj.getEntryIterator();
        while (it.hasNext()) {
            ProjectEntry entry = it.next();
            if (entry instanceof FolderProjectEntry e) {
                return Optional.of(entry.getFile());
            }
        }
        return Optional.empty();
    }
}
