/*
 * 08/28/2012
 *
 * WorkspaceTreeRenderer.java - Renderer for workspace tree nodes.
 * Copyright (C) 2012 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.project.tree;

import org.fife.ui.utils.UIUtil;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;


/**
 * The renderer for workspace tree views.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class WorkspaceTreeRenderer extends DefaultTreeCellRenderer {

    private static final Border BORDER = BorderFactory.createEmptyBorder(1, 0, 1, 0);

    private Font defaultFont;
    private Font projectsFont;
    private Font foldersFont;
    private Color defaultForegroundColor;

    private static final Color FOLDERS_COLOR;

    // default source makefile output
    private static final Color[] FILE_TYPES_DARK_COLORS = {null, new Color(0x9999FF), new Color(0xFF33FF), new Color(0x99FF66)};
    private static final Color[] FILE_TYPES_LIGHT_COLORS = {null, new Color(0x5555FF), new Color(0xAA22AA), new Color(0x55AA33)};
    private static final Color[] FILE_TYPES_COLORS = new Color[4];

    static {
        if (UIUtil.isDarkLookAndFeel()) {
            FOLDERS_COLOR = Color.WHITE;
        } else {
            FOLDERS_COLOR = Color.BLACK;
        }
    }

    /**
     * Returns a tree cell renderer for workspace trees.
     * This may not be an instance of this class (or a subclass).
     * Some Look and Feels unfortunately require inheritance to work properly...
     *
     * @return The renderer.
     */
    public static TreeCellRenderer create() {
        return new WorkspaceTreeRenderer();
    }


    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row,
                                                  boolean focused) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focused);
        if (defaultFont == null) {
            init();
        }
        setBorder(BORDER);
        if (value instanceof ProjectTreeNode) {
            setFont(projectsFont);
            setForeground(FOLDERS_COLOR);
        } else if (value instanceof FolderProjectEntryTreeNode) {
            setFont(foldersFont);
            setForeground(FOLDERS_COLOR);
        } else if (value instanceof FileTreeNode node) {
            setFont(defaultFont);
            setForeground(FILE_TYPES_COLORS[node.getFileType()]);
        } else if (value instanceof AbstractWorkspaceTreeNode node) {  // Not true the first time through!
            setIcon(node.getIcon());
            setFont(defaultFont);
            setForeground(defaultForegroundColor);
        }
        return this;
    }

    private void init() {
        defaultFont = getFont();
        projectsFont = new Font(defaultFont.getName(), defaultFont.getStyle() | Font.BOLD, defaultFont.getSize() + 1);
        foldersFont = new Font(defaultFont.getName(), defaultFont.getStyle() | Font.BOLD, defaultFont.getSize());
        defaultForegroundColor = getForeground();
        FILE_TYPES_COLORS[0] = defaultForegroundColor;
        for (int i = 1; i < FILE_TYPES_COLORS.length; i++) {
            FILE_TYPES_COLORS[i] = UIUtil.isDarkLookAndFeel() ? FILE_TYPES_DARK_COLORS[i] : FILE_TYPES_LIGHT_COLORS[i];
        }
    }


}
