/*
 * 03/22/2010
 *
 * TidyAction.java - Action that "tidies" source code.
 * Copyright (C) 2010 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.tidy;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.fife.rtext.RText;
import org.fife.rtext.RTextEditorPane;
import org.fife.ui.app.AppAction;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;


/**
 * Action for tidying source code.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class TidyAction extends AppAction<RText> {

	/**
	 * The tidying plugin.
	 */
	private final Plugin plugin;


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 * @param plugin The tidying plugin.
	 */
	TidyAction(RText app, Plugin plugin) {
		super(app, Plugin.MSG, "Action.Tidy");
		this.plugin = plugin;
	}


	/**
	 * Callback for this action.
	 *
	 * @param e The event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		RText app = getApplication();
		RTextEditorPane textArea = app.getMainView().getCurrentTextArea();
		String style = textArea.getSyntaxEditingStyle();
		PrettyPrintResult result = null;

		if (SyntaxConstants.SYNTAX_STYLE_JSON.equals(style)) {
			result = tidyJson();
		}
		else if (SyntaxConstants.SYNTAX_STYLE_HTML.equals(style) ||
				SyntaxConstants.SYNTAX_STYLE_XML.equals(style)) {
			result = tidyMarkup();
		}
		else { // Never happens
			UIManager.getLookAndFeel().provideErrorFeedback(textArea);
		}

		if (result!=null) {
			finish(textArea, result);
		}

	}


	/**
	 * Makes summary messages more palatable to JOptionPanes.
	 */
	private static String cleanupSummary(String summary) {

		// Single-line JSON streams shouldn't create super-long JOptionPanes.
		final int maxSummaryLength = 200;
		if (summary.length()> maxSummaryLength) {
			summary = summary.substring(0, maxSummaryLength) + "...";
		}

		// jsonbeans will print arbitrarily-long JSON blocks in its exception
		// messages, so keep it <= 5 lines.
		final int maxSummaryLineCount = 10;
		int lineCount = 1;
		int index = 0;
		while ((index=summary.indexOf('\n', index))>-1) {
			if (lineCount== maxSummaryLineCount) {
				summary = summary.substring(0, index) + "...";
				break;
			}
			lineCount++;
			index++;
		}

		// Tabs don't show up in JOptionPanes.
		return summary.replaceAll("\t", "   ");

	}


	/**
	 * Called when the pretty print operation completes.  If it was successful,
	 * the text area's content is updated.  An optional summary message is
	 * displayed.
	 *
	 * @param textArea The text area whose content was pretty printed.
	 * @param result A summary describing the pretty printing operation.
	 */
	private void finish(RTextEditorPane textArea, PrettyPrintResult result) {

		int icon = JOptionPane.INFORMATION_MESSAGE;

		switch (result.getResult()) {
			case PrettyPrinter.RESULT_WARNINGS:
				icon = JOptionPane.WARNING_MESSAGE;
				// Fall through
			case PrettyPrinter.RESULT_OK:
				textArea.beginAtomicEdit();
				try {
					textArea.setText(result.getText());
				} finally { // Treat clear and set as 1 operation (!)
					textArea.endAtomicEdit();
				}
				break;
			case PrettyPrinter.RESULT_ERRORS:
				icon = JOptionPane.ERROR_MESSAGE;
				break;
		}

		String summary = result.getSummary();
		if (summary!=null) {
			summary = cleanupSummary(summary);
			String title = Plugin.MSG.getString("Dialog.Result.Title");
			RText app = getApplication();
			JOptionPane.showMessageDialog(app, summary, title, icon);
		}
		else if (result.getResult()==PrettyPrinter.RESULT_ERRORS) {
			// If somehow we didn't get an error message, but the tidy failed,
			// still alert the user that something bad happened.
			UIManager.getLookAndFeel().provideErrorFeedback(null);
		}

	}


	private PrettyPrintResult tidyJson() {

		RText app = getApplication();
		RTextEditorPane textArea = app.getMainView().getCurrentTextArea();
		String json = textArea.getText();
		PrettyPrinter printer = new JsonPrettyPrinter(plugin);

		try {
			return printer.prettyPrint(json);
		} catch (Exception e) { // Never happens
			app.displayException(e);
		}

		return null;
	}


	private PrettyPrintResult tidyMarkup() {
		RText app = getApplication();
		RTextEditorPane textArea = app.getMainView().getCurrentTextArea();
		String text = textArea.getText();
		return new MarkupPrettyPrinter(app, plugin).prettyPrint(text);
	}


}
