/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE file for details.
 */
package org.fife.ui.rsyntaxtextarea;

import org.fife.ui.SwingRunnerExtension;
import org.fife.ui.rsyntaxtextarea.parser.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.*;
import java.awt.event.MouseEvent;


/**
 * Unit tests for the {@link ErrorStrip} class.
 *
 * @author Robert Futrell
 * @version 1.0
 */
@ExtendWith(SwingRunnerExtension.class)
class ErrorStripTest extends AbstractRSyntaxTextAreaTest {

	private ErrorStrip strip;


	@BeforeEach
	void setUp() {

		RSyntaxTextArea textArea = createTextArea();

		// Force reparsing by instance, not by index, to avoid the
		// code folding parser
		TestParser testParser = new TestParser();
		textArea.addParser(testParser);
		textArea.forceReparsing(testParser);

		strip = new ErrorStrip(textArea);
	}


	@Test
	void testAddRemoveNotify() {
		strip.addNotify();
		strip.removeNotify();
	}


	@Test
	void testDoLayout() {
		strip.doLayout();
	}

	@Test
	void testGetSetCaretMarkerColor() {
		Assertions.assertEquals(Color.BLACK, strip.getCaretMarkerColor());
		strip.setCaretMarkerColor(Color.RED);
		Assertions.assertEquals(Color.RED, strip.getCaretMarkerColor());
	}


	@Test
	void testGetSetFollowCaret() {
		Assertions.assertTrue(strip.getFollowCaret());
		strip.setFollowCaret(false);
		Assertions.assertFalse(strip.getFollowCaret());
	}


	@Test
	void testGetSetLevelThreshold() {
		Assertions.assertEquals(ParserNotice.Level.WARNING, strip.getLevelThreshold());
		strip.setLevelThreshold(ParserNotice.Level.INFO);
		Assertions.assertEquals(ParserNotice.Level.INFO, strip.getLevelThreshold());
	}


	@Test
	void testGetSetShowMarkAll() {
		Assertions.assertTrue(strip.getShowMarkAll());
		strip.setShowMarkAll(false);
		Assertions.assertFalse(strip.getShowMarkAll());
	}


	@Test
	void testGetSetShowMarkedOccurrences() {
		Assertions.assertTrue(strip.getShowMarkedOccurrences());
		strip.setShowMarkedOccurrences(false);
		Assertions.assertFalse(strip.getShowMarkedOccurrences());
	}


	@Test
	void testGetPreferredSize() {
		Assertions.assertNotNull(strip.getPreferredSize());
	}


	@Test
	void testGetToolTipText() {
		MouseEvent e = new MouseEvent(strip, 0, 0, 0, 1, 1, 1, false);
		Assertions.assertEquals("Line: 1", strip.getToolTipText(e));
	}


	@Test
	void testPaintComponent() {

		Graphics g = createTestGraphics();
		strip.paintComponent(g);
	}


	@Test
	void testSetMarkerToolTipProvider_null() {
		strip.setMarkerToolTipProvider(null); // No exception thrown
	}


	@Test
	void testSetMarkerToolTipProvider_notNull() {
		ErrorStrip.ErrorStripMarkerToolTipProvider provider = notices -> "test";
		strip.setMarkerToolTipProvider(provider);
	}


	/**
	 * A dummy parser that returns a single parser notice for test purposes.
	 */
	private static class TestParser extends AbstractParser {

		@Override
		public ParseResult parse(RSyntaxDocument doc, String style) {

			DefaultParseResult result = new DefaultParseResult(this);

			result.addNotice(new DefaultParserNotice(this, "test notice", 1));
			return result;
		}
	}
}
