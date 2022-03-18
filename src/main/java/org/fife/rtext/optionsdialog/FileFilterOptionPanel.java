/*
 * 03/01/2004
 *
 * FileFilterOptionPanel.java - Option panel letting the user configure
 * what extensions get opened with what syntax highlighting scheme.
 * Copyright (C) 2004 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.optionsdialog;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.fife.rtext.AbstractMainView;
import org.fife.rtext.RText;
import org.fife.rtext.SyntaxFilters;
import org.fife.ui.options.OptionsDialogPanel;
import org.fife.ui.widgets.SelectableLabel;
import org.fife.ui.utils.UIUtil;
import org.fife.ui.modifiabletable.*;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;


/**
 * Option panel letting the user configure what files get opened with what
 * syntax highlighting scheme.
 *
 * @author Robert Futrell
 * @version 0.2
 */
class FileFilterOptionPanel extends OptionsDialogPanel
					implements ActionListener, ModifiableTableListener {

	private final ModifiableTable filterTable;
	private final FilterTableModel model;
	private final JCheckBox guessTypeCB;
	private final JCheckBox ignoreExtsCB;
	private final RText rtext;


	/**
	 * Constructor.
	 *
	 * @param rtext The RText instance.
	 * @param msg The resource bundle to use.
	 */
	FileFilterOptionPanel(RText rtext, ResourceBundle msg) {

		super(msg.getString("OptFFName"));
		this.rtext = rtext;

		ComponentOrientation orientation = ComponentOrientation.
									getOrientation(getLocale());

		setLayout(new BorderLayout());
		setBorder(UIUtil.getEmpty5Border());
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(
					new OptionPanelBorder(msg.getString("OptFFLabel")));
		add(contentPane);

		SelectableLabel desc = new SelectableLabel(msg.getString("OptFFDesc"));
		JPanel temp = new JPanel(new BorderLayout());
		temp.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
		temp.add(desc, BorderLayout.LINE_START);
		contentPane.add(temp, BorderLayout.NORTH);
		model = new FilterTableModel(msg.getString("OptFFCol1"), msg.getString("OptFFCol2"));
		filterTable = new ModifiableTable(model, ModifiableTable.BOTTOM, ModifiableTable.MODIFY);
		filterTable.setRowHandler(new FileFilterRowHandler());
		filterTable.addModifiableTableListener(this);
		JTable table = filterTable.getTable();
		table.setPreferredScrollableViewportSize(new Dimension(300,300));
		contentPane.add(filterTable);

		JPanel bottomPanel = new JPanel(new BorderLayout());

		guessTypeCB = new JCheckBox(msg.getString("GuessContentType"));
		guessTypeCB.setActionCommand("GuessContentType");
		guessTypeCB.addActionListener(this);

		ignoreExtsCB = new JCheckBox(msg.getString("IgnoreTheseExtensions"));
		ignoreExtsCB.setActionCommand("IgnoreTheseExtensions");
		ignoreExtsCB.addActionListener(this);

		Box box = Box.createVerticalBox();
		addLeftAligned(box, guessTypeCB);
		addLeftAligned(box, ignoreExtsCB);
		box.add(Box.createVerticalStrut(5)); // Distance from "Restore Defaults"
		//box.add(Box.createVerticalGlue());
		bottomPanel.add(box, BorderLayout.NORTH);

		JButton rdButton = new JButton(msg.getString("RestoreDefaults"));
		rdButton.setActionCommand("RestoreDefaults");
		rdButton.addActionListener(this);
		temp = new JPanel(new BorderLayout());
		temp.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));
		temp.add(rdButton, BorderLayout.LINE_START);
		bottomPanel.add(temp, BorderLayout.SOUTH);

		add(bottomPanel, BorderLayout.SOUTH);
		applyComponentOrientation(orientation);

	}


	/**
	 * Listens for actions in this panel.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if ("RestoreDefaults".equals(command)) {
			// Empty constructor returns defaults.
			SyntaxFilters defaultFilters = new SyntaxFilters();
			boolean changed = setSyntaxFilters(defaultFilters);
			if (changed ||
					!guessTypeCB.isSelected() ||
					!ignoreExtsCB.isSelected()) {
				guessTypeCB.setSelected(true);
				ignoreExtsCB.setSelected(true);
				setDirty(true);
			}
		}

		else if ("GuessContentType".equals(command)) {
			setDirty(true);
		}

		else if ("IgnoreTheseExtensions".equals(command)) {
			setDirty(true);
		}

	}


	/**
	 * Applies the settings entered into this dialog on the specified
	 * application.
	 *
	 * @param owner The application.
	 */
	@Override
	protected void doApplyImpl(Frame owner) {
		RText rtext = (RText)owner;
		AbstractMainView mainView = rtext.getMainView();
		mainView.setSyntaxFilters(getSyntaxFilters());
		mainView.setGuessFileContentType(guessTypeCB.isSelected());
		mainView.setIgnoreBackupExtensions(ignoreExtsCB.isSelected());
	}


	@Override
	protected OptionsPanelCheckResult ensureValidInputsImpl() {

		// Make sure each filter value in the table is valid.
		int rowCount = model.getRowCount();
		for (int i=0; i<rowCount; i++) {
			String filters = (String)model.getValueAt(i, 1);
			if (!SyntaxFilters.isValidFileFilterString(filters)) {
				String temp = rtext.getString("InvalidFFString", filters);
				return new OptionsPanelCheckResult(this, filterTable, temp);
			}
		}

		// Otherwise, the input was okay.
		return null;

	}


	/**
	 * Returns the syntax file filters accepted by the user.
	 *
	 * @return The syntax file filters.
	 */
	private SyntaxFilters getSyntaxFilters() {
		SyntaxFilters filters = new SyntaxFilters();
		for (int i=0; i<model.styles.length; i++) {
			filters.setFiltersForSyntaxStyle(model.styles[i],
									(String)model.getValueAt(i,1));
		}
		return filters;
	}


	/**
	 * Returns the <code>JComponent</code> at the "top" of this Options
	 * panel.  This is the component that will receive focus if the user
	 * switches to this Options panel in the Options dialog.  As an added
	 * bonus, if this component is a <code>JTextComponent</code>, its
	 * text is selected for easy changing.
	 */
	@Override
	public JComponent getTopJComponent() {
		return filterTable;
	}


	/**
	 * Called whenever the extension/color mapping table is changed.
	 *
	 * @param e An event describing the change.
	 */
	@Override
	public void modifiableTableChanged(ModifiableTableChangeEvent e) {
		setDirty(true);
	}


	/**
	 * Sets the syntax filters displayed for each file type.
	 *
	 * @param syntaxFilters the filters for each type.
	 * @return Whether any of the values actually changed.
	 * @see SyntaxFilters
	 */
	private boolean setSyntaxFilters(SyntaxFilters syntaxFilters) {

		String filterString;
		boolean changed = false;

		for (int i=0; i<model.styles.length; i++) {
			String style = model.styles[i];
			filterString = syntaxFilters.getFilterString(style);
			String old = (String)model.getValueAt(i, 1);
			if (!filterString.equals(old)) {
				model.setValueAt(filterString, i,1);
				changed = true;
			}
		}

		return changed;

	}


	/**
	 * Sets the values displayed by this panel to reflect those in the
	 * application.  Child panels are not handled.
	 *
	 * @param owner The parent application.
	 * @see #setValues(Frame)
	 */
	@Override
	protected void setValuesImpl(Frame owner) {
		RText rtext = (RText)owner;
		AbstractMainView mainView = rtext.getMainView();
		setSyntaxFilters(mainView.getSyntaxFilters());
		guessTypeCB.setSelected(mainView.getGuessFileContentType());
		ignoreExtsCB.setSelected(mainView.getIgnoreBackupExtensions());
	}


	/**
	 * Table data for the "File filter" table.
	 */
	static class FilterTableModel extends DefaultTableModel
							implements SyntaxConstants {

		String[] styles;

		FilterTableModel(String fileTypeHeader, String filterHeader) {
			super(new Object[] { fileTypeHeader, filterHeader }, 0);
			styles = new String[51];
			int c = 0;
			addRow(new Object[] { "ActionScript",		null }); styles[c++]  = SYNTAX_STYLE_ACTIONSCRIPT;
			addRow(new Object[] { "Assembler (AVR)",	null }); styles[c++]  = SYNTAX_STYLE_ASSEMBLER_AVR;
			addRow(new Object[] { "Assembler (6502)",	null }); styles[c++]  = SYNTAX_STYLE_ASSEMBLER_6502;
			addRow(new Object[] { "Assembler (x86)",	null }); styles[c++]  = SYNTAX_STYLE_ASSEMBLER_X86;
			addRow(new Object[] { "AVR RAT",	        null }); styles[c++]  = SYNTAX_STYLE_AVR_RAT;
			addRow(new Object[] { "BBCode",				null }); styles[c++]  = SYNTAX_STYLE_BBCODE;
			addRow(new Object[] { "C",					null }); styles[c++]  = SYNTAX_STYLE_C;
			addRow(new Object[] { "C++",				null }); styles[c++]  = SYNTAX_STYLE_CPLUSPLUS;
			addRow(new Object[] { "C#",					null }); styles[c++]  = SYNTAX_STYLE_CSHARP;
			addRow(new Object[] { "Clojure",			null }); styles[c++]  = SYNTAX_STYLE_CLOJURE;
			addRow(new Object[] { "CSS",				null }); styles[c++]  = SYNTAX_STYLE_CSS;
			addRow(new Object[] { "CSV",				null }); styles[c++]  = SYNTAX_STYLE_CSV;
			addRow(new Object[] { "D",					null }); styles[c++] = SYNTAX_STYLE_D;
			addRow(new Object[] { "Delphi",				null }); styles[c++] = SYNTAX_STYLE_DELPHI;
			addRow(new Object[] { "Dockerfile",			null }); styles[c++] = SYNTAX_STYLE_DOCKERFILE;
			addRow(new Object[] { "DTD",				null }); styles[c++] = SYNTAX_STYLE_DTD;
			addRow(new Object[] { "Flex",				null }); styles[c++] = SYNTAX_STYLE_MXML;
			addRow(new Object[] { "Fortran",			null }); styles[c++] = SYNTAX_STYLE_FORTRAN;
			addRow(new Object[] { "Go",					null }); styles[c++] = SYNTAX_STYLE_GO;
			addRow(new Object[] { "Groovy",				null }); styles[c++] = SYNTAX_STYLE_GROOVY;
			addRow(new Object[] { "Hosts",				null }); styles[c++] = SYNTAX_STYLE_HOSTS;
			addRow(new Object[] { "htaccess",			null }); styles[c++] = SYNTAX_STYLE_HTACCESS;
			addRow(new Object[] { "HTML",				null }); styles[c++] = SYNTAX_STYLE_HTML;
			addRow(new Object[] { "INI",				null }); styles[c++] = SYNTAX_STYLE_INI;
			addRow(new Object[] { "Intel hex",			null }); styles[c++] = SYNTAX_STYLE_INTEL_HEX;
			addRow(new Object[] { "Java",				null }); styles[c++] = SYNTAX_STYLE_JAVA;
			addRow(new Object[] { "JavaScript",			null }); styles[c++] = SYNTAX_STYLE_JAVASCRIPT;
			addRow(new Object[] { "JSP",				null }); styles[c++] = SYNTAX_STYLE_JSP;
			addRow(new Object[] { "JSON",				null }); styles[c++] = SYNTAX_STYLE_JSON;
			addRow(new Object[] { "JSON with comments",	null }); styles[c++] = SYNTAX_STYLE_JSON_WITH_COMMENTS;
			addRow(new Object[] { "LaTeX",				null }); styles[c++] = SYNTAX_STYLE_LATEX;
			addRow(new Object[] { "Less",				null }); styles[c++] = SYNTAX_STYLE_LESS;
			addRow(new Object[] { "Lisp",				null }); styles[c++] = SYNTAX_STYLE_LISP;
			addRow(new Object[] { "Lua",				null }); styles[c++] = SYNTAX_STYLE_LUA;
			addRow(new Object[] { "Make",				null }); styles[c++] = SYNTAX_STYLE_MAKEFILE;
			addRow(new Object[] { "NSIS",				null }); styles[c++] = SYNTAX_STYLE_NSIS;
			addRow(new Object[] { "Perl",				null }); styles[c++] = SYNTAX_STYLE_PERL;
			addRow(new Object[] { "PHP",				null }); styles[c++] = SYNTAX_STYLE_PHP;
			addRow(new Object[] { "Properties files",	null }); styles[c++] = SYNTAX_STYLE_PROPERTIES_FILE;
			addRow(new Object[] { "Python",				null }); styles[c++] = SYNTAX_STYLE_PYTHON;
			addRow(new Object[] { "Ruby",				null }); styles[c++] = SYNTAX_STYLE_RUBY;
			addRow(new Object[] { "SAS",				null }); styles[c++] = SYNTAX_STYLE_SAS;
			addRow(new Object[] { "Scala",				null }); styles[c++] = SYNTAX_STYLE_SCALA;
			addRow(new Object[] { "SQL",				null }); styles[c++] = SYNTAX_STYLE_SQL;
			addRow(new Object[] { "Tcl",				null }); styles[c++] = SYNTAX_STYLE_TCL;
			addRow(new Object[] { "TypeScript",			null }); styles[c++] = SYNTAX_STYLE_TYPESCRIPT;
			addRow(new Object[] { "UNIX shell scripts",	null }); styles[c++] = SYNTAX_STYLE_UNIX_SHELL;
			addRow(new Object[] { "Visual Basic",       null }); styles[c++] = SYNTAX_STYLE_VISUAL_BASIC;
			addRow(new Object[] { "Windows Batch",		null }); styles[c++] = SYNTAX_STYLE_WINDOWS_BATCH;
			addRow(new Object[] { "XML",				null }); styles[c++] = SYNTAX_STYLE_XML;
			addRow(new Object[] { "YAML",				null }); styles[c++] = SYNTAX_STYLE_YAML;
		}

	}


	/**
	 * Handles the addition, removal, and modifying of rows in
	 * the file filter table.
	 */
	private class FileFilterRowHandler extends AbstractRowHandler {

		@Override
		public Object[] getNewRowInfo(Object[] oldData) {
			String oldValue = (String)oldData[1];
			String filterType = (String)oldData[0];
			String input;
			while (true) {
				String prompt = rtext.getString("FileFilterPrompt", filterType);
				input = JOptionPane.showInputDialog(
					FileFilterOptionPanel.this, prompt, oldValue);
				if (input!=null) {
					if (!SyntaxFilters.isValidFileFilterString(input)) {
						String temp = rtext.getString("InvalidFFString",
												input);
						JOptionPane.showMessageDialog(
									FileFilterOptionPanel.this, temp,
									rtext.getString("ErrorDialogTitle"),
									JOptionPane.ERROR_MESSAGE);
						continue;
					}
				}
				break;
			} // End of while (true).
			if (input!=null) {
				return new Object[] { oldData[0], input };
			}
			return null;
		}

		@Override
		public boolean canRemoveRow(int row) {
			return false; // Can modify any row, but not remove any
		}

	}


}
