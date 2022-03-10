package org.fife.rtext.plugins.project.tree;

import org.fife.rtext.RTextUtilities;
import ru.trolsoft.ide.config.state.TreeStateSaver;

import javax.swing.tree.TreePath;
import java.io.File;

public class WorkspaceTreeStateSaver extends TreeStateSaver {
    public WorkspaceTreeStateSaver() {
        super(getFilePath());
    }

    private static String getFilePath() {
        return new File(RTextUtilities.getPreferencesDirectory(),"workspaceTree.state").getAbsolutePath();
    }

    @Override
    protected String pathToStr(TreePath path) {
        if (path == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int cnt = path.getPathCount();
        for (int i = 0; i < cnt; i++) {
            sb.append(path.getPathComponent(i));
            if (i != cnt-1) {
                sb.append('/');
            }
        }
        return sb.toString();
    }
}
