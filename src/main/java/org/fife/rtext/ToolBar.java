/*
 * 11/14/2003
 *
 * ToolBar.java - Toolbar used by RText.
 * Copyright (C) 2003 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext;

import javax.swing.JButton;

import org.fife.ui.CustomizableToolBar;
import org.fife.ui.rtextarea.RTextArea;


/**
 * The toolbar used by {@link RText}.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class ToolBar extends CustomizableToolBar {


	/**
	 * Creates the toolbar.
	 *
	 * @param title The title of this toolbar when it is floating.
	 * @param rtext The main application that owns this toolbar.
	 */
	ToolBar(String title, RText rtext) {
		super(title);

		addAction(rtext,  RText.NEW_ACTION);
		addAction(rtext, RText.OPEN_ACTION);
		addAction(rtext,  RText.SAVE_ACTION);
		addSeparator();

		addAction(rtext, RText.PRINT_ACTION);
		addSeparator();

		addAction(RTextArea.CUT_ACTION);
		addAction(RTextArea.COPY_ACTION);
		addAction(RTextArea.PASTE_ACTION);
		addAction(RTextArea.DELETE_ACTION);
		addSeparator();

		addAction(rtext, RText.FIND_ACTION);
		addAction(rtext, RText.REPLACE_ACTION);
		addSeparator();

		// Necessary to keep button size from changing when undo text changes.
		addAction(RTextArea.UNDO_ACTION).putClientProperty("hideActionText", Boolean.TRUE);
		addAction(RTextArea.REDO_ACTION).putClientProperty("hideActionText", Boolean.TRUE);
		addSeparator();

		addAction(rtext, RText.BUILD_ACTION);
		addAction(rtext, RText.UPLOAD_ACTION);

		// Make the toolbar have the right-click customize menu.
		makeCustomizable();
	}

	private JButton addAction(RText rtext, String key) {
		JButton btn = createButton(rtext.getAction(key));
		add(btn);
		return btn;
	}

	private JButton addAction(int action) {
		JButton btn = createButton(RTextArea.getAction(action));
		add(btn);
		return btn;
	}

}
