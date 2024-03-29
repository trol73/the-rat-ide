/*
 * 10/03/2015
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE file for details.
 */
package org.fife.ui.rsyntaxtextarea.parser;

import java.util.List;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for the {@link TaskTagParser} class.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class TaskTagParserTest {


	@Test
	void testConstructor() {
		TaskTagParser parser = new TaskTagParser();
		Assertions.assertEquals("TODO|FIXME|HACK", parser.getTaskPattern());
	}


	@Test
	void testGetTaskPattern() {

		TaskTagParser parser = new TaskTagParser();
		Assertions.assertEquals("TODO|FIXME|HACK", parser.getTaskPattern());

		parser.setTaskPattern("Hello|World");
		Assertions.assertEquals("Hello|World", parser.getTaskPattern());

		parser.setTaskPattern(null);
		Assertions.assertNull(parser.getTaskPattern());

	}


	@Test
	void testParse_happyPath() throws Exception {

		TaskTagParser parser = new TaskTagParser();

		RSyntaxDocument doc = new RSyntaxDocument(
				SyntaxConstants.SYNTAX_STYLE_C);
		doc.insertString(0, "/* TODO: Fix this */", null);

		ParseResult res = parser.parse(doc, doc.getSyntaxStyle());
		Assertions.assertEquals(parser, res.getParser());
		Assertions.assertEquals(0, res.getFirstLineParsed());
		Assertions.assertEquals(0, res.getLastLineParsed());
		List<ParserNotice> notices = res.getNotices();
		Assertions.assertEquals(1, notices.size());
		// Note that the parser does not understand EOL vs. MLC comments, so
		// it just returns everything from the start of the task to the end of
		// the line.
		Assertions.assertEquals("TODO: Fix this */", notices.get(0).getToolTipText());

	}


	@Test
	void testParse_nullTaskPattern() throws Exception {

		TaskTagParser parser = new TaskTagParser();
		parser.setTaskPattern(null);

		RSyntaxDocument doc = new RSyntaxDocument(
				SyntaxConstants.SYNTAX_STYLE_C);
		doc.insertString(0, "/* TODO: Fix this */ for", null);

		ParseResult res = parser.parse(doc, doc.getSyntaxStyle());
		Assertions.assertEquals(parser, res.getParser());
		Assertions.assertEquals(0, res.getFirstLineParsed());
		Assertions.assertEquals(0, res.getLastLineParsed());
		List<ParserNotice> notices = res.getNotices();
		Assertions.assertEquals(0, notices.size());

	}


	@Test
	void testParse_noLanguage() throws Exception {

		TaskTagParser parser = new TaskTagParser();
		parser.setTaskPattern(null);

		RSyntaxDocument doc = new RSyntaxDocument(
				SyntaxConstants.SYNTAX_STYLE_NONE);
		doc.insertString(0, "/* TODO: Fix this */ for", null);

		ParseResult res = parser.parse(doc, doc.getSyntaxStyle());
		Assertions.assertEquals(parser, res.getParser());
		Assertions.assertEquals(0, res.getFirstLineParsed());
		Assertions.assertEquals(0, res.getLastLineParsed());
		List<ParserNotice> notices = res.getNotices();
		Assertions.assertEquals(0, notices.size());

		doc.setSyntaxStyle((String)null); // Not really valid, but whatever
		res = parser.parse(doc, doc.getSyntaxStyle());
		Assertions.assertEquals(parser, res.getParser());
		Assertions.assertEquals(0, res.getFirstLineParsed());
		Assertions.assertEquals(0, res.getLastLineParsed());
		notices = res.getNotices();
		Assertions.assertEquals(0, notices.size());

	}


	@Test
	void testSetTaskPattern() {

		TaskTagParser parser = new TaskTagParser();
		Assertions.assertEquals("TODO|FIXME|HACK", parser.getTaskPattern());

		parser.setTaskPattern("Hello|World");
		Assertions.assertEquals("Hello|World", parser.getTaskPattern());

		parser.setTaskPattern(null);
		Assertions.assertNull(parser.getTaskPattern());

		parser.setTaskPattern(""); // We convert empty string to null
		Assertions.assertNull(parser.getTaskPattern());

	}


}
