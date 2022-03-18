package org.fife.rtext.plugins.project.tree.checkers;

import org.fife.rtext.plugins.project.tree.NameChecker;

/**
 * Ensures that proposed project names are valid.
 */
public class WorkspaceNameChecker implements NameChecker {

    @Override
    public String isValid(String text) {
        int length = text.length();
        if (length == 0) {
            return "empty";
        }
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            if (!(Character.isLetterOrDigit(ch) || ch == '_' || ch == '-' || ch == ' ' || ch == '.')) {
                return "invalidWorkspaceName";
            }
        }
        if (text.endsWith(".")) {
            return "workspaceCannotEndWithDot";
        }
        return null;
    }

}
