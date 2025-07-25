/*
 * 9/19/2006
 *
 * ReplaceInFilesDialog.java - A dialog that replaces instances of text
 * across multiple files.
 * Copyright (C) 2006 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.ui.search;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;

import org.fife.ui.widgets.AssistanceIconPanel;
import org.fife.rsta.ui.search.SearchComboBox;
import org.fife.ui.utils.UIUtil;


/**
 * Dialog that does string replacement across multiple files.
 *
 * @author Robert Futrell
 * @version 0.9
 */
public class ReplaceInFilesDialog extends FindInFilesDialog {

	private SearchComboBox replaceCombo;


	/**
	 * Creates a new <code>ReplaceInFilesDialog</code>.
	 *
	 * @param owner The main window that owns this dialog.
	 */
	public ReplaceInFilesDialog(Frame owner) {
		super(owner);
		this.setTitle(getString2("ReplaceInFilesDialogTitle"));
		btnFind.setText(getString("Replace"));
		btnFind.setMnemonic((int)getString("Replace.Mnemonic").charAt(0));
	}


	@Override
	protected Box createDetailsPanel() {

		// A panel containing the "Report detail" panel and some check boxes.
		Box panel = Box.createVerticalBox();
		panel.add(Box.createVerticalStrut(5));
		subfoldersCheckBox = new JCheckBox(getString2("SearchSubfolders"));
		subfoldersCheckBox.setMnemonic((int)getString2("SearchSubfoldersMnemonic").charAt(0));
		panel.add(subfoldersCheckBox);
		cbVerbose = new JCheckBox(getString2("Verbose"));
		cbVerbose.setMnemonic((int)getString2("VerboseMnemonic").charAt(0));
		panel.add(cbVerbose);
		panel.add(Box.createVerticalGlue());

		return panel;

	}


	@Override
	protected Box createExtraOptionsPanel() {
		return null;
	}


	@Override
	protected JPanel createInputPanel() {

		JPanel inputPanel = super.createInputPanel();

		replaceCombo = new SearchComboBox(null, true);
		getTextComponent(replaceCombo).addFocusListener(new FindInFilesFocusAdapter());
		JLabel replaceLabel = UIUtil.newLabel(getBundle(), "ReplaceWith",
				replaceCombo);

		JPanel temp = new JPanel(new BorderLayout());
		temp.add(replaceCombo);
		AssistanceIconPanel aip = new AssistanceIconPanel(replaceCombo, DECORATIVE_ICON_WIDTH);
		temp.add(aip, BorderLayout.LINE_START);

		ComponentOrientation orientation = ComponentOrientation.
									getOrientation(getLocale());
		if (orientation.isLeftToRight()) {
			inputPanel.add(replaceLabel, 2);
			inputPanel.add(temp, 3);
		}
		else {
			inputPanel.add(temp, 2);
			inputPanel.add(replaceLabel, 3);
		}

		UIUtil.makeSpringCompactGrid(inputPanel,
									5,2,		// rows,cols,
									0,0,		// initial-x, initial-y,
									0,5);	// x-spacing, y-spacing.

		return inputPanel;

	}


	@Override
	protected ResultsComponent createResultsComponent() {
		ReplaceInFilesTable table = new ReplaceInFilesTable();
		table.addMouseListener(new FindInFilesDialogMouseListener(table));
		return table;
	}


	@Override
	protected FindInFilesThread createWorkerThread(File directory) {
		return new ReplaceInFilesThread(this, directory);
	}


	@Override
	protected void escapePressed() {
		if (replaceCombo != null) {
			// Workaround for the strange behavior (Java bug?) that sometimes
			// the Escape keypress "gets through" from the AutoComplete's
			// registered key Actions, and gets to this EscapableDialog, which
			// hides the entire dialog.  Reproduce by doing the following:
			//   1. In an empty find field, press Ctrl+Space
			//   2. Type "\\".
			//   3. Press Escape.
			// The entire dialog will hide, instead of the completion popup.
			// Further, bringing the Find dialog back up, the completion popup
			// will still be visible.
			if (replaceCombo.hideAutoCompletePopups()) {
				return;
			}
		}
		super.escapePressed();
	}


	/**
	 * Returns the text to replace with.
	 *
	 * @return The text the user wants to replace with.
	 */
	public String getReplaceString() {
		return (String)replaceCombo.getSelectedItem();
	}


	/**
	 * Called when the regex checkbox is clicked.
	 */
	@Override
	protected void handleRegExCheckBoxClicked() {

		super.handleRegExCheckBoxClicked();

		// "Content assist" support
		boolean b = cbRegex.isSelected();
		// Always true except when debugging.  findTextCombo done in parent
		if (replaceCombo != null) {
			replaceCombo.setAutoCompleteEnabled(b);
		}

	}


	@Override
	public void setContentAssistImage(Image image) {
		super.setContentAssistImage(image);
		replaceCombo.setContentAssistImage(image);
	}


	@Override
	protected void setSearching(boolean searching) {
		super.setSearching(searching);
		boolean enabled = !searching;
		replaceCombo.setEnabled(enabled);
	}


	@Override
	public void setVisible(boolean visible) {

		// Make sure content assist is enabled (regex check box might have
		// been checked in a different search dialog).
		if (visible) {
			boolean regexEnabled = cbRegex.isSelected();
			// Always true except when debugging.  findTextCombo done in parent
			if (replaceCombo != null) {
				replaceCombo.setAutoCompleteEnabled(regexEnabled);
			}
		}

		super.setVisible(visible);

	}


	/**
	 * Overridden to update the "Replace with" combo box updated also.
	 */
	@Override
	public void updateUI() {

		super.updateUI();

		// Replace listeners on "Replace with" combo box
		FindInFilesFocusAdapter focusAdapter = new FindInFilesFocusAdapter();
		JTextComponent textField = getTextComponent(replaceCombo);
		textField.addFocusListener(focusAdapter);

	}


}
