/*
 * 08/28/2012
 *
 * AbstractWorkspaceTreeNode.java - Base class for workspace tree nodes.
 * Copyright (C) 2012 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.project.tree;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.fife.rsta.ac.java.DecoratableIcon;
import org.fife.rtext.RText;
import org.fife.rtext.RTextUtilities;
import org.fife.rtext.plugins.project.BaseAction;
import org.fife.rtext.plugins.project.LogicalFolderNameDialog;
import org.fife.rtext.plugins.project.Messages;
import org.fife.rtext.plugins.project.NewExistingFolderDialog;
import org.fife.rtext.plugins.project.PopupContent;
import org.fife.rtext.plugins.project.ProjectPlugin;
import org.fife.rtext.plugins.project.model.FileProjectEntry;
import org.fife.rtext.plugins.project.model.FolderProjectEntry;
import org.fife.rtext.plugins.project.model.LogicalFolderProjectEntry;
import org.fife.rtext.plugins.project.model.ProjectEntry;
import org.fife.rtext.plugins.project.model.ProjectEntryParent;
import org.fife.ui.rtextfilechooser.Actions;
import org.fife.ui.rtextfilechooser.RTextFileChooser;


/**
 * All nodes in a workspace tree extend this class.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class AbstractWorkspaceTreeNode extends DefaultMutableTreeNode {

	final ProjectPlugin plugin;
	private static RTextFileChooser chooser;


	AbstractWorkspaceTreeNode(ProjectPlugin plugin) {
		this.plugin = plugin;
	}


	/**
	 * Adds menu items to open in the system's default editor and viewer
	 * applications.
	 *
	 * @param actions The action list to add to.
	 */
	void addOpenInActions(List<PopupContent> actions) {
		WorkspaceTree tree = plugin.getTree();
		PopupContent.PopupSubMenu openInMenu = new PopupContent.PopupSubMenu(
				Messages.getString("Action.OpenIn"));
		openInMenu.add(new Actions.SystemOpenAction(tree,
				Actions.SystemOpenAction.OpenMethod.EDIT));
		openInMenu.add(new Actions.SystemOpenAction(tree,
				Actions.SystemOpenAction.OpenMethod.OPEN));
		actions.add(openInMenu);
		actions.add(null);
	}


	String escapeForHtml(String text) {
		return RTextUtilities.escapeForHTML(text, null);
	}


	/**
	 * Returns the name to display for this node in the workspace tree.
	 *
	 * @return The display name.
	 */
	protected abstract String getDisplayName();


	/**
	 * Returns the file chooser to use when adding files to a project.
	 *
	 * @return The file chooser.
	 */
	private static RTextFileChooser getFileChooser() {
		if (chooser==null) {
			chooser = new RTextFileChooser();
			chooser.setShowHiddenFiles(true);
			chooser.setMultiSelectionEnabled(true);
		}
		return chooser;
	}


	/**
	 * Returns the icon for this tree node.
	 *
	 * @return The icon for this tree node.
	 */
	public abstract Icon getIcon();


	public ProjectPlugin getPlugin() {
		return plugin;
	}


	/**
	 * Returns the popup menu actions for this tree node.
	 *
	 * @return The popup menu actions.
	 */
	public abstract List<PopupContent> getPopupActions();


	/**
	 * Returns the tool tip text for this tree node.
	 *
	 * @return The tool tip text.
	 */
	public abstract String getToolTipText();


	protected abstract void handleDelete();


	protected abstract void handleProperties();


	protected abstract void handleRename();


	/**
	 * Moves the project model entity this tree node represents "down" in its
	 * parent, if it makes logical sense to do so.  The default implementation
	 * returns <code>false</code>, since for most entities this operation
	 * does not make sense.
	 *
	 * @param toBottom Whether to move it all the way to the bottom, as opposed
	 *        to just down one.
	 * @return Whether the entity was moved down.
	 * @see #moveProjectEntityUp(boolean)
	 */
	public boolean moveProjectEntityDown(boolean toBottom) {
		return false;
	}


	/**
	 * Moves the project model entity this tree node represents "up" in its
	 * parent, if it makes logical sense to do so.  The default implementation
	 * returns <code>false</code>, since for most entities this operation
	 * does not make sense.
	 *
	 * @param toTop Whether to move it all the way to the top, as opposed to
	 *        just up one.
	 * @return Whether the entity was moved up.
	 * @see #moveProjectEntityDown(boolean)
	 */
	public boolean moveProjectEntityUp(boolean toTop) {
		return false;
	}


	@Override
	public final String toString() {
		return getDisplayName();
	}


	/**
	 * Action for a menu item that adds a file to this project.
	 */
	class AddFileAction extends BaseAction {

		private final ProjectEntryParent parent;
		private final MutableTreeNode node;

		AddFileAction(ProjectEntryParent parent, MutableTreeNode node) {
			super("Action.NewFiles",
				plugin.getApplication().getIconGroup().getIcon("add_file"));
			this.parent = parent;
			this.node = node;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			RTextFileChooser chooser = getFileChooser();
			int rc = chooser.showOpenDialog(plugin.getApplication());
			if (rc==RTextFileChooser.APPROVE_OPTION) {
				File[] toAdd = chooser.getSelectedFiles();
				for (File file : toAdd) {
					ProjectEntry entry = new FileProjectEntry(parent, file);
					parent.addEntry(entry);
					FileProjectEntryTreeNode childNode =
						new FileProjectEntryTreeNode(plugin, entry);
					plugin.insertTreeNodeInto(childNode, node);
				}
			}
		}

	}


	/**
	 * Action for a menu item that adds a folder to this project.
	 */
	class AddFolderAction extends BaseAction {

		private final ProjectEntryParent parent;
		private final MutableTreeNode node;

		AddFolderAction(ProjectEntryParent parent, MutableTreeNode node) {
			super("Action.AddFolder",
				plugin.getApplication().getIconGroup().getIcon("add_folder"));
			this.parent = parent;
			this.node = node;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			RText rtext = plugin.getApplication();
			NewExistingFolderDialog chooser = new NewExistingFolderDialog(rtext);
			chooser.setVisible(true);
			String dir = chooser.getChosenDirectory();
			if (dir!=null) {
				File dirFile = new File(dir);
				FolderProjectEntry entry = new FolderProjectEntry(
						parent, dirFile);
				parent.addEntry(entry);
				FolderProjectEntryTreeNode childNode =
						new FolderProjectEntryTreeNode(plugin, entry);
				childNode.setFilterInfo(chooser.getFilterInfo());
				plugin.insertTreeNodeInto(childNode, node);
			}
		}

	}


	/**
	 * Action for a menu item that adds a logical folder to this project.
	 */
	class AddLogicalFolderAction extends BaseAction {

		private final ProjectEntryParent parent;
		private final MutableTreeNode node;

		AddLogicalFolderAction(ProjectEntryParent parent,
				MutableTreeNode node) {

			super("Action.NewLogicalFolder",
				plugin.getApplication().getIconGroup().getIcon("add_folder"));
			this.parent = parent;
			this.node = node;

			// Add the "logical folder" decoration to our icon.
			Icon icon = (Icon)getValue(SMALL_ICON);
			DecoratableIcon di = new DecoratableIcon(16, icon);
			URL decorationRes = RText.class.
					getResource("/org/fife/rsta/ui/search/lightbulb.png");
			di.addDecorationIcon(new ImageIcon(decorationRes));
			setIcon(di);

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Frame parent = plugin.getApplication();
			LogicalFolderNameDialog dialog = new LogicalFolderNameDialog(
					parent, null, new LogicalFolderNameChecker());
			dialog.setVisible(true);
			String name = dialog.getLogicalFolderName();
			if (name!=null) {
				LogicalFolderProjectEntry entry =
						new LogicalFolderProjectEntry(this.parent, name);
				this.parent.addEntry(entry);
				MutableTreeNode child =
						new LogicalFolderProjectEntryTreeNode(plugin, entry);
				plugin.insertTreeNodeInto(child, this.node);
			}
		}

	}


	/**
	 * Copies the full path of this file or folder to the clipboard.  If this
	 * tree node does not represent a file or folder, an error beep is played.
	 */
	class CopyFullPathAction extends BaseAction {

		CopyFullPathAction() {
			super("Action.CopyFullPath");
			int mods = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() |
					InputEvent.SHIFT_DOWN_MASK;
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mods));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			getPlugin().getTree().copySelectedFilePathToClipboard();
		}

	}


	/**
	 * Action for deleting a tree node.
	 */
	class DeleteAction extends BaseAction {

		DeleteAction() {
			this(true);
		}

		DeleteAction(boolean enabled) {
			super("Action.Delete");
			setEnabled(enabled);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Run later to allow popup to hide
			SwingUtilities.invokeLater(AbstractWorkspaceTreeNode.this::handleDelete);
		}

	}


	/**
	 * Opens the "Find in Files" dialog, starting in this folder.  If this
	 * tree node does not represent a folder, an error beep is played.
	 */
	class FindInFilesFromHereAction extends BaseAction {

		FindInFilesFromHereAction() {
			super("Action.FindInFilesFromHere");
			int mods = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() |
					InputEvent.SHIFT_DOWN_MASK;
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, mods));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			getPlugin().getTree().findInFilesFromSelectedDir();
		}

	}


	/**
	 * Moves this tree node "down" in the list of its parent's children.
	 */
	class MoveDownAction extends BaseAction {

		MoveDownAction() {
			super("Action.MoveDown");
			int index = getParent().getIndex(AbstractWorkspaceTreeNode.this);
			setEnabled(index<getParent().getChildCount()-1);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			plugin.moveTreeNodeDown(AbstractWorkspaceTreeNode.this);
		}

	}


	/**
	 * Moves this tree node to the "bottom" of the list of its parent's children.
	 */
	class MoveToBottomAction extends BaseAction {

		MoveToBottomAction() {
			super("Action.MoveToBottom");
			setEnabled(getParent().getIndex(AbstractWorkspaceTreeNode.this) <
					getParent().getChildCount() - 1);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			plugin.moveTreeNodeToBottom(AbstractWorkspaceTreeNode.this);
		}

	}


	/**
	 * Moves this tree node to the "top" of the list of its parent's children.
	 */
	class MoveToTopAction extends BaseAction {

		MoveToTopAction() {
			super("Action.MoveToTop");
			setEnabled(getParent().getIndex(AbstractWorkspaceTreeNode.this)>0);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			plugin.moveTreeNodeToTop(AbstractWorkspaceTreeNode.this);
		}

	}


	/**
	 * Moves this tree node "up" in the list of its parent's children.
	 */
	class MoveUpAction extends BaseAction {

		MoveUpAction() {
			super("Action.MoveUp");
			setEnabled(getParent().getIndex(AbstractWorkspaceTreeNode.this)>0);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			plugin.moveTreeNodeUp(AbstractWorkspaceTreeNode.this);
		}

	}


	/**
	 * Opens the selected file in RText.
	 */
	class OpenAction extends BaseAction {

		OpenAction() {
			super("Action.Open");
			int enter = KeyEvent.VK_ENTER;
			setAccelerator(KeyStroke.getKeyStroke(enter, 0));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			WorkspaceTree tree = plugin.getTree();
			Object selected = tree.getLastSelectedPathComponent();
			File file = null;
			if (selected instanceof FileTreeNode) {
				file = ((FileTreeNode)selected).getFile();
			}
			else if (selected instanceof FileProjectEntryTreeNode) {
				FileProjectEntryTreeNode node = (FileProjectEntryTreeNode)selected;
				file = node.getFile();
				if (!file.isFile()) {
					tree.promptForRemoval(node);
					return;
				}
			}
			plugin.getApplication().openFile(file);
		}

	}


	/**
	 * Action for getting the properties of a tree node.
	 */
	class PropertiesAction extends BaseAction {

		PropertiesAction(boolean enabled) {
			this(false, enabled);
		}

		PropertiesAction(boolean root, boolean enabled) {
			super(root ? "Action.Properties.Workspace" : "Action.Properties");
			int alt = InputEvent.ALT_DOWN_MASK;
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, alt));
			setEnabled(enabled);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			handleProperties();
		}

	}


	/**
	 * Refreshes the selected tree node.
	 */
	class RefreshAction extends BaseAction {

		RefreshAction() {
			super("Action.Refresh");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			WorkspaceTree tree = plugin.getTree();
			Object obj = tree.getLastSelectedPathComponent();
			if (obj instanceof PhysicalLocationTreeNode) {
				((PhysicalLocationTreeNode)obj).handleRefresh();
			}
			else {
				UIManager.getLookAndFeel().provideErrorFeedback(tree);
			}
		}

	}


	/**
	 * Action for renaming a tree node.
	 */
	class RenameAction extends BaseAction {

		RenameAction() {
			this(false);
		}

		RenameAction(boolean root) {
			super(root ? "Action.Rename.Workspace" : "Action.Rename");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			handleRename();
		}

	}


}
