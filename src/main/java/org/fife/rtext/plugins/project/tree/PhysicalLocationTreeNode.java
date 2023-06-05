/*
 * 01/02/2010
 *
 * PhysicalLocationTreeNode.java - Marker interface for a tree node
 * representing a physical file or folder.
 * Copyright (C) 2010 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.project.tree;

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.TreeNode;

import org.fife.rtext.RText;
import org.fife.rtext.plugins.project.BaseAction;
import org.fife.rtext.plugins.project.NewFileOrFolderDialog;
import org.fife.rtext.plugins.project.PopupContent;
import org.fife.rtext.plugins.project.ProjectPlugin;
import org.fife.rtext.plugins.project.tree.FileTreeNode.FileNameChecker;
import org.fife.ui.app.icons.IconGroup;


/**
 * Marker interface for a tree node representing a physical file or folder.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface PhysicalLocationTreeNode extends TreeNode {


	/**
	 * Returns the file or folder represented by this node.
	 *
	 * @return The file or folder.
	 */
	File getFile();


	/**
	 * Returns the plugin.
	 *
	 * @return The plugin.
	 */
	ProjectPlugin getPlugin();


	/**
	 * Refreshes the children of this node.
	 */
	void handleRefresh();


	/**
	 * Returns whether this folder tree node has not yet been populated
	 * (expanded).
	 *
	 * @return Whether this node has not yet been populated.
	 */
	boolean isNotPopulated();


	/**
	 * Refreshes the child nodes of this node.  The tree model will need to be
	 * reloaded after making this call.
	 */
	void refreshChildren();


	/**
	 * Dummy class signifying that this tree node has not yet had its children
	 * calculated.
	 */
	class NotYetPopulatedChild extends AbstractWorkspaceTreeNode {

		NotYetPopulatedChild(ProjectPlugin plugin) {
			super(plugin);
		}

		@Override
		public String getDisplayName() {
			return null;
		}

		@Override
		public Icon getIcon() {
			return null;
		}

		@Override
		public List<PopupContent> getPopupActions() {
			return null;
		}

		@Override
		public String getToolTipText() {
			return null;
		}

		@Override
		protected void handleDelete() {
		}

		@Override
		protected void handleProperties() {
		}

		protected void handleRefresh() {
		}

		@Override
		protected void handleRename() {
		}

	}


	/**
	 * Creates a new child file or folder whose parent is this node's file
	 * (which must be a directory).
	 */
	class NewFileOrFolderAction extends BaseAction {
		static final String TYPE_NONE = "";
		static final String TYPE_C_WITH_HEADER = "_c_h";
		static final String TYPE_C = "_c";
		static final String TYPE_C_HEADER = "_h";
		static final String TYPE_ART = "_art";
		static final String TYPE_ART_HEADER = "_arth";
		private final PhysicalLocationTreeNode node;
		private final boolean isFile;
		private final String type;

		NewFileOrFolderAction(PhysicalLocationTreeNode node, boolean isFile, String type) {
			super(isFile ? "Action.NewFile" + type: "Action.NewFolder");
			IconGroup iconGroup = node.getPlugin().getApplication().getIconGroup();
			setIcon(iconGroup.getIcon(isFile ? "add_file" + type : "add_folder"));
			this.node = node;
			this.isFile = isFile;
			this.type = type;
		}

		NewFileOrFolderAction(PhysicalLocationTreeNode node, boolean isFile) {
			this(node, isFile, TYPE_NONE);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			File parent = node.getFile();
			if (parent == null || !parent.isDirectory()) {
				UIManager.getLookAndFeel().provideErrorFeedback(null);
				return;
			}

			RText rtext = node.getPlugin().getApplication();
			NameChecker nameChecker = new FileNameChecker(parent, !isFile);
			NewFileOrFolderDialog dialog = new NewFileOrFolderDialog(rtext, !isFile, nameChecker, type);
			dialog.setFileName(null); // Force focus on the text field
			dialog.center();
			dialog.setVisible(true);


			String newName = dialog.getFileName();
			if (newName != null) {
				boolean success = createFileOrFolderImpl(newName);
				if (!success) {
					UIManager.getLookAndFeel().provideErrorFeedback(rtext);
					return;
				}
				node.handleRefresh();
				if (dialog.getOpenOnCreate()) {
					rtext.openFile(createFileObject(newName));
				}
			}

		}

		private File createFileObject(String name) {
			switch (type) {
				case TYPE_C -> {
					if (!name.endsWith(".c")) {
						name += ".c";
					}
					return new File(node.getFile(), name);
				}
				case TYPE_C_HEADER-> {
					if (!name.endsWith(".h")) {
						name += ".h";
					}
					return new File(node.getFile(), name);
				}
				case TYPE_C_WITH_HEADER -> {
					if (name.endsWith(".h") || name.endsWith(".c")) {
						name = name.substring(0, name.length()-2);
					}
					String macroName = "_" + name.toUpperCase() + "_H_";
					try {
						File hFile = new File(node.getFile(), name + ".h");
						if (!hFile.createNewFile()) {
							return null;
						}
						writeFile(hFile, "#ifndef " + macroName + "\n" +
								"#define " + macroName + "\n" +
								"\n\n\n" +
								"#endif // " + macroName + "\n"
						);
					} catch (IOException e) {
						return null;
					}
					File cFile = new File(node.getFile(), name + ".c");
					writeFile(cFile, "#include \"" + name + ".h\"\n");
					return cFile;
				}
				case TYPE_ART -> {
					if (!name.endsWith(".art")) {
						name += ".art";
					}
					return new File(node.getFile(), name);
				}
				case TYPE_ART_HEADER -> {
					if (!name.endsWith(".arth")) {
						name += ".arth";
					}
					return new File(node.getFile(), name);
				}
			}
			return new File(node.getFile(), name);
		}

		private boolean createFileOrFolderImpl(String name) {
			boolean success = false;
			if (isFile) {
				try {
					createFileObject(name).createNewFile();
					success = true;
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			else {
				success = createFileObject(name).mkdir();
			}
			return success;
		}

	}

	private static void writeFile(File file, String content) {
		try (Writer writer = new FileWriter(file)) {
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
