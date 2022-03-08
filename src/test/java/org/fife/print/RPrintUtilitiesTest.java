/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE file for details.
 */
package org.fife.print;

import org.fife.ui.SwingRunnerExtension;
import org.fife.ui.rsyntaxtextarea.AbstractRSyntaxTextAreaTest;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.print.PageFormat;


/**
 * Unit tests for the {@link RPrintUtilities} class.
 *
 * @author Robert Futrell
 * @version 1.0
 */
@ExtendWith(SwingRunnerExtension.class)
class RPrintUtilitiesTest extends AbstractRSyntaxTextAreaTest {


	private static String createContent(int minLength) {

		StringBuilder sb = new StringBuilder();

		while (sb.length() < minLength) {
			sb.append("lorem ipsum\tlorem   ipsum 0123456789001234567890012345678900123456789001234567890")
				.append("012345678900123456789001234567890 lorem ipsum\n");
		}

		return sb.toString();
	}


	@Test
	void testPrintDocumentMonospaced_happyPath() throws BadLocationException {

		Graphics g = createTestGraphics();
		PlainDocument doc = new PlainDocument();
		doc.insertString(0, createContent(5000), null);
		PageFormat pageFormat = new PageFormat();

		RPrintUtilities.printDocumentMonospaced(g, doc, 10, 0, pageFormat, 4);
	}


	@Test
	void testPrintDocumentMonospaced_happyPath_tabSizeZero() throws BadLocationException {

		Graphics g = createTestGraphics();
		PlainDocument doc = new PlainDocument();
		doc.insertString(0, createContent(5000), null);
		PageFormat pageFormat = new PageFormat();

		RPrintUtilities.printDocumentMonospaced(g, doc, 10, 0, pageFormat, 0);
	}


	@Test
	void testPrintDocumentMonospaced_happyPath_empty() throws BadLocationException {

		Graphics g = createTestGraphics();
		PlainDocument doc = new PlainDocument();
		doc.insertString(0, createContent(0), null);
		PageFormat pageFormat = new PageFormat();

		RPrintUtilities.printDocumentMonospaced(g, doc, 10, 0, pageFormat, 4);
	}


	@Test
	void testPrintDocumentMonospacedWordWrap_happyPath() throws BadLocationException {

		Graphics g = createTestGraphics();
		PlainDocument doc = new PlainDocument();
		doc.insertString(0, createContent(5000), null);
		PageFormat pageFormat = new PageFormat();

		RPrintUtilities.printDocumentMonospacedWordWrap(g, doc, 10, 0, pageFormat, 4);
	}


	@Test
	void testPrintDocumentWordWrap_happyPath() {

		Graphics g = createTestGraphics();
		RSyntaxTextArea textArea = createTextArea();
		textArea.setText(createContent(4000));
		PageFormat pageFormat = new PageFormat();
		Font font = new Font(Font.DIALOG, Font.PLAIN, 12);

		RPrintUtilities.printDocumentWordWrap(g, textArea, font, 0, pageFormat, 4);
	}
}
