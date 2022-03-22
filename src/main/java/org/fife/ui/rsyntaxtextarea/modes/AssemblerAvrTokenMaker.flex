/*
 * 19/10/2015
 *
 * AssemblerAvrTokenMaker.java - An object that can take a chunk of text and
 * return a linked list of tokens representing Atmel AVR assembler.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea.modes;

import java.io.*;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.*;


/**
 * This class takes plain text and returns tokens representing AVR
 * assembler.<p>
 *
 * This implementation was created using
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.1; however, the generated file
 * was modified for performance.  Memory allocation needs to be almost
 * completely removed to be competitive with the handwritten lexers (subclasses
 * of <code>AbstractTokenMaker</code>, so this class has been modified so that
 * Strings are never allocated (via yytext()), and the scanner never has to
 * worry about refilling its buffer (needlessly copying chars around).
 * We can achieve this because RText always scans exactly 1 line of tokens at a
 * time, and hands the scanner this line as an array of characters (a Segment
 * really).  Since tokens contain pointers to char arrays instead of Strings
 * holding their contents, there is no need for allocating new memory for
 * Strings.<p>
 *
 * The actual algorithm generated for scanning has, of course, not been
 * modified.<p>
 *
 * If you wish to regenerate this file yourself, keep in mind the following:
 * <ul>
 *   <li>The generated <code>AssemblerAvrTokenMaker.java</code> file will contain two
 *       definitions of both <code>zzRefill</code> and <code>yyreset</code>.
 *       You should hand-delete the second of each definition (the ones
 *       generated by the lexer), as these generated methods modify the input
 *       buffer, which we'll never have to do.</li>
 *   <li>You should also change the declaration/definition of zzBuffer to NOT
 *       be initialized.  This is a needless memory allocation for us since we
 *       will be pointing the array somewhere else anyway.</li>
 *   <li>You should NOT call <code>yylex()</code> on the generated scanner
 *       directly; rather, you should use <code>getTokenList</code> as you would
 *       with any other <code>TokenMaker</code> instance.</li>
 * </ul>
 *
 * @author Oleg Trifonov
 * @version 0.1
 *
 */
%%

%public
%class AssemblerAvrTokenMaker
%extends AbstractJFlexTokenMaker
%unicode
%ignorecase
%type org.fife.ui.rsyntaxtextarea.Token


%{


	/**
	 * Constructor.  We must have this here as JFLex does not generate a
	 * no parameter constructor.
	 */
	public AssemblerAvrTokenMaker() {
		super();
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 */
	private void addToken(int tokenType) {
		addToken(zzStartRead, zzMarkedPos-1, tokenType);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 */
	private void addToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param array The character array.
	 * @param start The starting offset in the array.
	 * @param end The ending offset in the array.
	 * @param tokenType The token's type.
	 * @param startOffset The offset in the document at which this token
	 *                    occurs.
	 */
	@Override
	public void addToken(char[] array, int start, int end, int tokenType, int startOffset) {
		super.addToken(array, start,end, tokenType, startOffset);
		zzStartRead = zzMarkedPos;
	}

      /**
       * Adds the token specified to the current linked list of tokens.
       *
       * @param tokenType The token's type.
       * @see #addToken(int, int, int)
       */
      private void addHyperlinkToken(int start, int end, int tokenType) {
        int so = start + offsetShift;
        addToken(zzBuffer, start,end, tokenType, so, true);
      }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getLineCommentStartAndEnd(int languageIndex) {
		return new String[] { ";", null };
	}


	/**
	 * Returns the first token in the linked list of tokens generated
	 * from <code>text</code>.  This method must be implemented by
	 * subclasses so they can correctly implement syntax highlighting.
	 *
	 * @param text The text from which to get tokens.
	 * @param initialTokenType The token type we should start with.
	 * @param startOffset The offset into the document at which
	 *                    <code>text</code> starts.
	 * @return The first <code>Token</code> in a linked list representing
	 *         the syntax highlighted text.
	 */
	public Token getTokenList(Segment text, int initialTokenType, int startOffset) {

		resetTokenList();
		this.offsetShift = -text.offset + startOffset;

		// Start off in the proper state.
		int state = Token.NULL;
		switch (initialTokenType) {
			case Token.COMMENT_MULTILINE:
				state = MLC;
				start = text.offset;
				break;
			
			default:
				state = Token.NULL;
		}

		s = text;
		try {
			yyreset(zzReader);
			yybegin(state);
			return yylex();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return new TokenImpl();
		}

	}

	


	/**
	 * Refills the input buffer.
	 *
	 * @return      <code>true</code> if EOF was reached, otherwise
	 *              <code>false</code>.
	 */
	private boolean zzRefill() {
		return zzCurrentPos>=s.offset+s.count;
	}


	/**
	 * Resets the scanner to read from a new input stream.
	 * Does not close the old reader.
	 *
	 * All internal variables are reset, the old input stream 
	 * <b>cannot</b> be reused (internal buffer is discarded and lost).
	 * Lexical state is set to <tt>YY_INITIAL</tt>.
	 *
	 * @param reader   the new input stream 
	 */
	public final void yyreset(Reader reader) {
		// 's' has been updated.
		zzBuffer = s.array;
		/*
		 * We replaced the line below with the two below it because zzRefill
		 * no longer "refills" the buffer (since the way we do it, it's always
		 * "full" the first time through, since it points to the segment's
		 * array).  So, we assign zzEndRead here.
		 */
		//zzStartRead = zzEndRead = s.offset;
		zzStartRead = s.offset;
		zzEndRead = zzStartRead + s.count - 1;
		zzCurrentPos = zzMarkedPos = zzPushbackPos = s.offset;
		zzLexicalState = YYINITIAL;
		zzReader = reader;
		zzAtBOL  = true;
		zzAtEOF  = false;
	}


%}

Letter				= ([A-Za-z_])
Digit				= ([0-9])
HexDigit							= ({Digit}|[A-Fa-f])
OctalDigit						= ([0-7])
BinaryDigit						= ([0-1])
Number				    = ({Digit}+)
HexNumber			    = (0x{HexDigit}+)
BinaryNumber			= (0b{BinaryDigit}+)

LetterOrUnderscore		= ({Letter}|"_")

Identifier			= (({Letter}|{Digit})[^ \t\f\n\,\.\+\-\*\/\%\[\]]+)
IdentifierC		= ({LetterOrUnderscore}({LetterOrUnderscore}|{Digit}|[$])*)

UnclosedStringLiteral	= ([\"][^\"]*)
StringLiteral			= ({UnclosedStringLiteral}[\"])
UnclosedCharLiteral		= ([\'][^\']*)
CharLiteral			= ({UnclosedCharLiteral}[\'])

CommentBegin			= ([;])

MLCBegin			= "/*"
MLCEnd			= "*/"
LineCommentBegin	= "//"


LineTerminator			= (\n)
WhiteSpace			= ([ \t\f])


URLGenDelim				= ([:\/\?#\[\]@])
URLSubDelim				= ([\!\$&'\(\)\*\+,;=])
URLUnreserved			= ({LetterOrUnderscore}|{Digit}|[\-\.\~])
URLCharacter			= ({URLGenDelim}|{URLSubDelim}|{URLUnreserved}|[%])
URLCharacters			= ({URLCharacter}*)
URLEndCharacter			= ([\/\$]|{Letter}|{Digit})
URL						= (((https?|f(tp|ile))"://"|"www.")({URLCharacters}{URLEndCharacter})?)


Label				= (({Letter}|{Digit})+[\:])

Operator				= ("+"|"-"|"*"|"/"|"%"|"^"|"|"|"&"|"~"|"!"|"="|"<"|">")

%state MLC
%state EOL_COMMENT


%%

<YYINITIAL> {

	/* Keywords */
	".BYTE" |
	".CSEG" |
	".CSEGSIZE" |
	".DEF" |
	".DSEG" |
	".ENDM" |
	".ENDMACRO" |
	".EQU" |
	".ESEG" |
	".EXIT" |
	".GLOBAL" |
	".EXTERN" |
	".INCLUDE" |
	".LIST" |
	".LISTMAC" |
	".MACRO" |
	".NOLIST" |
	".ORG" |
	".SET" |
	".ELSE" |
	".ELIF" |
	".ENDIF" |
	".ERROR" |
	".IF" |
	".IFDEF" |
	".IFNDEF" |
	".MESSAGE" |
	".UNDEF" |
	".WARNING" |
	".OVERLAP" |
	".NOOVERLAP"		{ addToken(Token.RESERVED_WORD); }

	".DB" |
	".DW" |
	".DD" |
	".DQ"			{ addToken(Token.DATA_TYPE); }

	"_SFR_IO_ADDR" |
	"LO8" |
	"HI8"          { addToken(Token.FUNCTION); }


	/* Registers */
	"SREG" |
	"STACK" |
	"SP" |
	"EIND" |
	"RAMPX" |
	"RAMPY" |
	"RAMPZ" |
	"RAMPD" |
	"R0" |
	"R1" |
	"R2" |
	"R3" |
	"R4" |
	"R5" |
	"R6" |
	"R7" |
	"R8" |
	"R9" |
	"R10" |
	"R11" |
	"R12" |
	"R13" |
	"R14" |
	"R15" |
	"R16" |
	"R17" |
	"R18" |
	"R19" |
	"R20" |
	"R21" |
	"R22" |
	"R23" |
	"R24" |
	"R25" |
	"R26" |
	"R27" |
	"R28" |
	"R29" |
	"R30" |
	"R31" |
	"R32"	{ addToken(Token.REGISTER); }

	/* Instructions. */
	"ADD" |
	"ADC" |
	"SUB" |
	"SUBI" |
	"SBC" |
	"SBCI" |
	"AND" |
	"ANDI" |
	"OR" |
	"ORI" |
	"EOR" |
	"COM" |
	"NEG" |
	"SBR" |
	"CBR" |
	"INC" |
	"DEC" |
	"TST" |
	"CLR" |
	"SER" |
	"RJMP" |
	"RCALL" |
	"RET" |
	"RETI" |
	"CPSE" |
	"CP" |
	"CPC" |
	"CPI" |
	"SBRC" |
	"SBRS" |
	"SBIC" |
	"SBIS" |
	"BRBS" |
	"BRBC" |
	"BREQ" |
	"BRNE" |
	"BRCS" |
	"BRCC" |
	"BRSH" |
	"BRLO" |
	"BRMI" |
	"BRPL" |
	"BRGE" |
	"BRLT" |
	"BRHS" |
	"BRHC" |
	"BRTS" |
	"BRTC" |
	"BRVS" |
	"BRVC" |
	"BRIE" |
	"BRID" |
	"LD" |
	"ST" |
	"MOV" |
	"LDI" |
	"IN" |
	"OUT" |
	"LPM" |
	"SBI" |
	"CBI" |
	"LSL" |
	"LSR" |
	"ROL" |
	"ROR" |
	"ASR" |
	"SWAP" |
	"BSET" |
	"BCLR" |
	"BST" |
	"BLD" |
	"SEC" |
	"CLC" |
	"SEN" |
	"CLN" |
	"SEZ" |
	"CLZ" |
	"SEI" |
	"CLI" |
	"SES" |
	"CLS" |
	"SEV" |
	"CLV" |
	"SET" |
	"CLT" |
	"SEH" |
	"CLH" |
	"NOP" |
	"SLEEP" |
	"WDR" |
	"ADIW" |
	"SBIW" |
	"IJMP" |
	"ICALL" |
	"LDD" |
	"LDS" |
	"STD" |
	"STS" |
	"PUSH" |
	"POP" |
	"JMP" |
	"CALL" |
	"MUL" |
	"MULS" |
	"MULSU" |
	"FMUL" |
	"FMULS" |
	"FMULSU" |
	"MOVW" |
	"SPM" |
	"BREAK" |
	"EIJMP" |
	"EICALL" |
	"DES" |
	"XCH" |
	"LAS" |
	"LAC" |
	"LAT" 		{ addToken(Token.CPU_INSTRUCTION); }
}

<YYINITIAL> {

	{LineTerminator}				{ addNullToken(); return firstToken; }

	{WhiteSpace}+					{ addToken(Token.WHITESPACE); }

	/* String/Character Literals. */
	{CharLiteral}					{ addToken(Token.LITERAL_CHAR); }
	{UnclosedCharLiteral}			{ addToken(Token.ERROR_CHAR); /*addNullToken(); return firstToken;*/ }
	{StringLiteral}				{ addToken(Token.LITERAL_STRING_DOUBLE_QUOTE); }
	{UnclosedStringLiteral}			{ addToken(Token.ERROR_STRING_DOUBLE); addNullToken(); return firstToken; }


	"#"{WhiteSpace}*"include"{WhiteSpace}*"<"({IdentifierC}|"/"|".")*">"	{ addToken(Token.PREPROCESSOR); }
	/* Labels. */
	{Label}						{ addToken(Token.LABEL); }

	^%({Letter}|{Digit})*			{ addToken(Token.FUNCTION); }

	/* Comment Literals. */
	{CommentBegin}.*				{ addToken(Token.COMMENT_EOL); addNullToken(); return firstToken; }

	/* Operators. */
	{Operator}					{ addToken(Token.OPERATOR); }

	/* Numbers */
	{Number}						{ addToken(Token.LITERAL_NUMBER_DECIMAL_INT); }
    {HexNumber}						{ addToken(Token.LITERAL_NUMBER_HEXADECIMAL); }
    {BinaryNumber}					{ addToken(Token.LITERAL_NUMBER_BINARY); }


	/* Ended with a line not in a string or comment. */
	<<EOF>>						{ addNullToken(); return firstToken; }

	/* Comment Literals. */
	{MLCBegin}					{ start = zzMarkedPos-2; yybegin(MLC); }
	{LineCommentBegin}			{ start = zzMarkedPos-2; yybegin(EOL_COMMENT); }
	

	/* Catch any other (unhandled) characters. */
	{Identifier}					{ addToken(Token.IDENTIFIER); }
	.							{ addToken(Token.IDENTIFIER); }

}


<MLC> {

	[^hwf\n\*]+			{}
	{URL}				{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_MULTILINE); start = zzMarkedPos; }
	[hwf]				{}

	\n					{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }
	{MLCEnd}			{ yybegin(YYINITIAL); addToken(start,zzStartRead+1, Token.COMMENT_MULTILINE); }
	\*					{}
	<<EOF>>				{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }

}

<EOL_COMMENT> {
	[^hwf\n]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_EOL); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_EOL); start = zzMarkedPos; }
	[hwf]					{}
	\n						{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }
	<<EOF>>					{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }

}

