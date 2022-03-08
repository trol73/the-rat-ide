/*
 * 19/10/2015
 *
 * IntelHexTokenMaker.java - An object that can take a chunk of text and
 * return a linked list of tokens representing Intel HEX files.
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea.modes;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.swing.text.Segment;

/**
 * Created on 21/02/17.
 *
 * @author Oleg Trifonov
 * @version 0.1
 */
public class IntelHexTokenMaker extends AbstractTokenMaker {

	@Override
	public TokenMap getWordsToHighlight() {
		return new TokenMap();
	}

	@Override
	public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
		resetTokenList();
		char[] array = text.array;
		int offset = text.offset;
		int count = text.count;
		int end = offset + count;

		int newStartOffset = startOffset - offset;
		int currentTokenStart = offset;

		if (count > 10 && array[offset] == ':') {
			int crc = 0;
			currentTokenStart++;	// skip ':'
			int len = getByte(array, currentTokenStart, end);
			crc += len;
			currentTokenStart += 2;
			int address = getWord(array, currentTokenStart, end);
			crc += address >> 8;
			crc += address & 0xff;
			currentTokenStart += 4;
			int type = getByte(array, currentTokenStart, end);
			crc += type;
			currentTokenStart += 2;
			boolean dataOk = true;
			if (currentTokenStart + 2 <= end) {
				for (int i = 0; i < len; i++) {
					int v = getByte(array, currentTokenStart, end);
					if (v < 0) {
						dataOk = false;
					}
					crc += v;
					currentTokenStart += 2;
				}
			} else {
				dataOk = false;
			}
			// crc
			int endCrc = getByte(array, currentTokenStart, end);
			crc = (-crc) & 0xff;


			if (len >= 0 && address >= 0 && dataOk && endCrc >= 0) {
				currentTokenStart = offset;
				// ':'
				addToken(text, currentTokenStart, currentTokenStart, Token.PREPROCESSOR, newStartOffset + currentTokenStart);
				currentTokenStart++;
				// length
				addToken(text, currentTokenStart, currentTokenStart + 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
				currentTokenStart += 2;
				// offset
				addToken(text, currentTokenStart, currentTokenStart + 3, Token.LITERAL_STRING_DOUBLE_QUOTE, newStartOffset + currentTokenStart);
				currentTokenStart += 4;
				// type
				addToken(text, currentTokenStart, currentTokenStart + 1, Token.COMMENT_KEYWORD, newStartOffset + currentTokenStart);
				currentTokenStart += 2;

				// content
				addToken(text, currentTokenStart, currentTokenStart + len*2-1, Token.DATA_TYPE, newStartOffset + currentTokenStart);
				currentTokenStart += len*2;
				// crc
				addToken(text, currentTokenStart, currentTokenStart + 1, endCrc == crc ? Token.LITERAL_NUMBER_HEXADECIMAL : Token.ERROR_NUMBER_FORMAT, newStartOffset + currentTokenStart);
				currentTokenStart += 2;
				if (end > currentTokenStart) {
					addToken(text, currentTokenStart, end-1, Token.ERROR_NUMBER_FORMAT, newStartOffset + currentTokenStart);
				}
			} else {
				addToken(text, offset, end-1, Token.ERROR_NUMBER_FORMAT, startOffset);
			}
		} else {
			if (count > 0) {
				addToken(text, offset, end-1, Token.ERROR_NUMBER_FORMAT, startOffset);
			}
		}

		addNullToken();

		return firstToken;
	}



	private static int getByte(char[] array, int offset, int end) {
		if (offset+1 >= end) {
			return -1;
		}
		try {
			int hi = array[offset];
			if (hi >= '0' && hi <= '9') {
				hi -= '0';
			} else if (hi >= 'A' && hi <= 'F') {
				hi = hi - 'A' + 10;
			} else if (hi >= 'a' && hi <= 'f') {
				hi = hi - 'a' + 10;
			} else {
				return -1;
			}
			int lo = array[offset + 1];
			if (lo >= '0' && lo <= '9') {
				lo -= '0';
			} else if (lo >= 'A' && lo <= 'F') {
				lo = lo - 'A' + 10;
			} else if (lo >= 'a' && lo <= 'f') {
				lo = lo - 'a' + 10;
			} else {
				return -1;
			}
			return (hi << 4) + lo;
		} catch (Throwable t) {
			return -1;
		}
	}

	private static int getWord(char[] array, int offset, int end) {
		int hi = getByte(array, offset, end);
		if (hi < 0) {
			return -1;
		}
		int lo = getByte(array, offset+2, end);
		if (lo < 0) {
			return -1;
		}
		return (hi << 8) + lo;
	}


}
