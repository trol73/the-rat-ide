package ru.trolsoft.ide.utils;

import org.fife.rtext.AbstractMainView;
import org.fife.rtext.RText;
import org.fife.rtext.RTextEditorPane;
import org.fife.rtext.plugins.project.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
            if (prj.getMainFile().equalsIgnoreCase(filePath)) {
                return prj;
            }
        }
        it = workspace.getProjectIterator();
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
            } else if (entry instanceof FileProjectEntry e) {
                if (filePath.startsWith(e.getFile().getParent())) {
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

    public static List<File> getActiveProjectFolders(RText rtext) {
        var project = ProjectUtils.getProjectForCurrentFile(rtext);
        var result = new ArrayList<File>();
        if (project != null) {
            var it = project.getEntryIterator();
            while (it.hasNext()) {
                var entry = it.next();
                if (ProjectEntry.DIR_PROJECT_ENTRY.equals(entry.getType())) {
                    //result.add(entry.getFile().getParentFile().getAbsolutePath() + File.separatorChar);
                    result.add(entry.getFile());
//                } else if (ProjectEntry.FILE_PROJECT_ENTRY.equals(entry.getType())) {
//                    addFilesRecursive(entry.getFile());
                }
            }
        }
        return result;
    }

}
