package org.fife.rtext.plugins.filesystemtree;

import org.fife.rtext.RTextUtilities;
import org.fife.ui.rtextfilechooser.FileSystemTree;
import ru.trolsoft.ide.config.state.TreeStateSaver;

import javax.swing.tree.TreePath;
import java.io.File;

public class FileSystemTreeStateSaver extends TreeStateSaver {

    public FileSystemTreeStateSaver() {
        super(getFilePath());
    }

    private static String getFilePath() {
        return new File(RTextUtilities.getPreferencesDirectory(),"fileSystemTree.state").getAbsolutePath();
    }

    @Override
    protected String pathToStr(TreePath path) {
        if (path == null) {
            return "";
        }
        Object last = path.getPathComponent(path.getPathCount()-1);
        if (last instanceof FileSystemTree.FileSystemTreeNode) {
            FileSystemTree.FileSystemTreeNode node = (FileSystemTree.FileSystemTreeNode)last;
            return node.getFile().getAbsolutePath();
        } else {
            return last.toString();
        }
    }

}
