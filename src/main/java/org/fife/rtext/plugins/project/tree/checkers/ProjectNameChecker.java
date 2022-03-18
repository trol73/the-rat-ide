package org.fife.rtext.plugins.project.tree.checkers;

import org.fife.rtext.plugins.project.model.Workspace;
import org.fife.rtext.plugins.project.tree.NameChecker;

/**
 * Ensures that proposed project names are valid.
 */
public class ProjectNameChecker implements NameChecker {

    private final Workspace workspace;

    public ProjectNameChecker(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public String isValid(String text) {
        int length = text.length();
        if (length == 0) {
            return "empty";
        }
        if (workspace.containsProjectNamed(text)) {
            return "projectAlreadyExists";
        }
        return null;
    }

}
