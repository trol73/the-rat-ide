/*
 * 08/28/2012
 *
 * FileProjectEntryTreeNode.java - Tree node for file project entries.
 * Copyright (C) 2012 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.project.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import org.fife.rtext.RText;
import org.fife.rtext.plugins.project.Messages;
import org.fife.rtext.plugins.project.PopupContent;
import org.fife.rtext.plugins.project.ProjectPlugin;
import org.fife.rtext.plugins.project.RenameDialog;
import org.fife.rtext.plugins.project.model.FolderProjectEntry;
import org.fife.rtext.plugins.project.model.ProjectEntry;
import org.fife.ui.UIUtil;
import org.fife.ui.rtextfilechooser.FileDisplayNames;
import org.fife.ui.rtextfilechooser.Utilities;


/**
 * The tree node used for file project entries.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class FileProjectEntryTreeNode extends ProjectEntryTreeNode {

	private final Icon icon;


	public FileProjectEntryTreeNode(ProjectPlugin plugin, ProjectEntry entry) {
		super(plugin, entry);
		icon = FileSystemView.getFileSystemView().getSystemIcon(entry.getFile());
	}


	NameChecker createNameChecker() {
		File file = getFile();
		return new FileTreeNode.FileNameChecker(file.getParentFile(),
				file.isDirectory());
	}


	@Override
	public String getDisplayName() {
		return FileDisplayNames.get().getName(getFile());
	}


	/**
	 * Returns the file or directory represented by this tree node's project
	 * entry.
	 *
	 * @return The file or directory.
	 */
	public File getFile() {
		return entry.getFile();
	}


	@Override
	public Icon getIcon() {
		return icon;
	}


	@Override
	public List<PopupContent> getPopupActions() {
		List<PopupContent> actions = new ArrayList<>();
		boolean dir = getFile().isDirectory();
		if (!dir) {
			actions.add(new OpenAction());
		}
		addOpenInActions(actions);
		actions.add(new MoveToTopAction());
		actions.add(new MoveUpAction());
		actions.add(new MoveDownAction());
		actions.add(new MoveToBottomAction());
		actions.add(null);
		actions.add(new RemoveAction());
		actions.add(new CopyFullPathAction());
		actions.add(new DeleteAction());
		actions.add(null);
		actions.add(new RenameAction());
		actions.add(null);
		if (dir) {
			actions.add(new RefreshAction());
			actions.add(null);
		}
		actions.add(new PropertiesAction(true));
		return actions;
	}


	@Override
	public String getToolTipText() {
		return Messages.getString("ProjectPlugin.ToolTip.FileProjectEntry",
				getFile().getAbsolutePath(),
				Utilities.getFileSizeStringFor(getFile()));
	}


	@Override
	protected void handleDelete() {

		String text = Messages.getString("Action.DeleteFile.Confirm",
				getFile().getName());
		RText rtext = plugin.getApplication();
		String title = rtext.getString("ConfDialogTitle");

		int rc = JOptionPane.showConfirmDialog(rtext, text, title,
				JOptionPane.YES_NO_OPTION);
		if (rc==JOptionPane.YES_OPTION) {
			if (!UIUtil.deleteFile(entry.getFile())) {
				text = Messages.getString("ProjectPlugin.Error.DeletingFile",
						getFile().getName());
				title = rtext.getString("ErrorDialogTitle");
				JOptionPane.showMessageDialog(rtext, text, title,
						JOptionPane.ERROR_MESSAGE);
			}
			entry.removeFromParent();
			removeFromParent();
			plugin.refreshTree(getParent());
		}

	}


	@Override
	protected void handleProperties() {
		FileTreeNode.handleProperties(plugin.getApplication(), getFile());
	}


	@Override
	protected void handleRename() {
		RText rtext = plugin.getApplication();
		boolean directory = entry.getFile().isDirectory();
		String key = "ProjectPlugin." + (directory ? "Folder" : "File");
		String type = Messages.getString(key);
		RenameDialog dialog = new RenameDialog(rtext, !directory, type, createNameChecker());
		if (this instanceof FolderProjectEntryTreeNode) {
			FolderProjectEntry fpe = (FolderProjectEntry)entry;
			dialog.setDescription(getIcon(),
					Messages.getString("RenameDialog.DisplayName.Desc"));
			dialog.setNameLabel(Messages.getString("RenameDialog.DisplayName.Label"));
			dialog.setFileName(fpe.getDisplayName());
		}
		else {
			dialog.setFileName(FileDisplayNames.get().getName(entry.getFile()));
		}
		dialog.setVisible(true);
		String newName = dialog.getFileName();
		if (newName!=null) {
			handleRenameImpl(newName);
		}
	}


	void handleRenameImpl(String newName) {
		File old = entry.getFile();
		File newFile = new File(old.getParentFile(), newName);
		boolean success = old.renameTo(newFile);
		if (success) {
			plugin.getTree().nodeChanged(this);
		}
		else {
			UIManager.getLookAndFeel().provideErrorFeedback(null);
		}
	}


}
