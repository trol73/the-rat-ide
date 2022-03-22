/*
 * 12/04/2011
 *
 * TokenTypes.java - All token types supported by RSyntaxTextArea.
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE file for details.
 */
package org.fife.ui.rsyntaxtextarea;


/**
 * All token types supported by RSyntaxTextArea.<p>
 *
 * If you're creating your own {@code TokenMaker} for a new language, it's
 * important to note that while most of these token types are used purely
 * for styling information, that {@code TokenTypes.SEPARATOR} is given special
 * treatment in this library.  Specifically, many utility methods assume that
 * tokens such as curly braces and square brackets are identified as type
 * {@code SEPARATOR}.  For example,
 * {@code RSyntaxTextArea.setPaintMatchedBracketPair} makes this assumption.
 * <p>
 *
 * Note that all valid token types are &gt;= 0, so extensions of the TokenMaker
 * class are free to internally use all ints &lt; 0 ONLY for "end-of-line"
 * style markers; they are ignored by painting implementations.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface TokenTypes {

	/**
	 * Tokens of type <code>NULL</code> mark the end of lines with no
	 * multi-line token at the end being continued to the next line,
	 * for example, being in the middle of a block comment in Java.
	 */
	int NULL							= 0;

	int COMMENT_EOL						= 1;
	int COMMENT_MULTILINE				= 2;
	int COMMENT_DOCUMENTATION			= 3;
	int COMMENT_KEYWORD					= 4;
	int COMMENT_MARKUP					= 5;

	int RESERVED_WORD					= 6;
	int RESERVED_WORD_2					= 7;

	int FUNCTION						= 8;

	int LITERAL_BOOLEAN					= 9;
	int LITERAL_NUMBER_DECIMAL_INT		= 10;
	int LITERAL_NUMBER_FLOAT			= 11;
	int LITERAL_NUMBER_HEXADECIMAL		= 12;
	int LITERAL_NUMBER_BINARY		    = 13;
	int LITERAL_STRING_DOUBLE_QUOTE		= 14;
	int LITERAL_CHAR					= 15;
	int LITERAL_BACKQUOTE				= 16;

	int DATA_TYPE						= 17;

	int VARIABLE						= 18;

	int REGEX							= 19;

	int ANNOTATION						= 20;

	int IDENTIFIER						= 21;

	int WHITESPACE						= 22;

	/**
	 * Separators are typically single-character tokens such as parens
	 * brackets and braces ({@code [}, {@code ]}, <code>{</code>,
	 * <code>}</code>, etc.).  In particular, brackets and braces
	 * must be of this token type for bracket matching to work.
	 */
	int SEPARATOR						= 23;

	int OPERATOR						= 24;

	int PREPROCESSOR					= 25;

	int REGISTER					    = 26;
	int CPU_INSTRUCTION					= 27;
	int IO_PORT							= 28;
	int IO_PORT_BIT						= 29;
	int INTERRUPT_VECTOR				= 30;
	int LABEL							= 31;

	int MARKUP_TAG_DELIMITER			= 32;
	int MARKUP_TAG_NAME					= 33;
	int MARKUP_TAG_ATTRIBUTE			= 34;
	int MARKUP_TAG_ATTRIBUTE_VALUE		= 35;
	int MARKUP_COMMENT					= 36;
	int MARKUP_DTD						= 37;
	int MARKUP_PROCESSING_INSTRUCTION	= 38;
	int MARKUP_CDATA_DELIMITER			= 39;
	int MARKUP_CDATA					= 40;
	int MARKUP_ENTITY_REFERENCE			= 41;

	int ERROR_IDENTIFIER				= 42;
	int ERROR_NUMBER_FORMAT				= 43;
	int ERROR_STRING_DOUBLE 			= 44;
	int ERROR_CHAR						= 45;

	int DEFAULT_NUM_TOKEN_TYPES = 46;

}
