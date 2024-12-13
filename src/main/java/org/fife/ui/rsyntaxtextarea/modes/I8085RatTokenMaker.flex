/*
 * Generated on 2/23/22, 1:57 PM
 */
package org.fife.ui.rsyntaxtextarea.modes;

import java.io.*;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.*;


/**
 * 
 */
%%

%public
%class I8085RatTokenMaker
%extends AbstractJFlexCTokenMaker
%unicode
/* Case sensitive */
%type org.fife.ui.rsyntaxtextarea.Token


%{


	/**
	 * Constructor.  This must be here because JFlex does not generate a
	 * no-parameter constructor.
	 */
	public I8085RatTokenMaker() {
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
	 * @see #addHyperlinkToken(int, int, int)
	 */
	private void addToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so, false);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param array The character array.
	 * @param start The starting offset in the array.
	 * @param end The ending offset in the array.
	 * @param tokenType The token's type.
	 * @param startOffset The offset in the document at which this token
	 *        occurs.
	 * @param hyperlink Whether this token is a hyperlink.
	 */
	public void addToken(char[] array, int start, int end, int tokenType,
						int startOffset, boolean hyperlink) {
		super.addToken(array, start,end, tokenType, startOffset, hyperlink);
		zzStartRead = zzMarkedPos;
	}


	/**
	 * {@inheritDoc}
	 */
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
	 *        <code>text</code> starts.
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

			/* No documentation comments */
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

Letter							= [A-Za-z]
LetterOrUnderscore				= ({Letter}|"_")
NonzeroDigit						= [1-9]
Digit							= ("0"|{NonzeroDigit})
HexDigit							= ({Digit}|[A-Fa-f])
OctalDigit						= ([0-7])
BinaryDigit						= ([0-1])
AnyCharacterButApostropheOrBackSlash	= ([^\\'])
AnyCharacterButDoubleQuoteOrBackSlash	= ([^\\\"\n])
EscapedSourceCharacter				= ("u"{HexDigit}{HexDigit}{HexDigit}{HexDigit})
Escape							= ("\\"(([btnfr\"'\\])|([0123]{OctalDigit}?{OctalDigit}?)|({OctalDigit}{OctalDigit}?)|{EscapedSourceCharacter}))
NonSeparator						= ([^\t\f\r\n\ \(\)\{\}\[\]\;\,\.\=\>\<\!\~\?\:\+\-\*\/\&\|\^\%\"\']|"#"|"\\")
IdentifierStart					= ({LetterOrUnderscore}|"$")
IdentifierPart						= ({IdentifierStart}|{Digit}|"@"|("\\"{EscapedSourceCharacter}))

PreprocessorWord	= define|undef|include|ifdef|ifndef|if|else|elif|endif|warning|error|message

LineTerminator				= (\n)
WhiteSpace				= ([ \t\f]+)

CharLiteral	= ([\']({AnyCharacterButApostropheOrBackSlash}|{Escape})[\'])
UnclosedCharLiteral			= ([\'][^\'\n]*)
ErrorCharLiteral			= ({UnclosedCharLiteral}[\'])
StringLiteral				= ([\"]({AnyCharacterButDoubleQuoteOrBackSlash}|{Escape})*[\"])
UnclosedStringLiteral		= ([\"]([\\].|[^\\\"])*[^\"]?)
ErrorStringLiteral			= ({UnclosedStringLiteral}[\"])

MLCBegin					= "/*"
MLCEnd					= "*/"

/* No documentation comments */
LineCommentBegin			= ";"
CLineCommentBegin			= "//"


IntegerLiteral			= ({Digit}+)
HexLiteral			= (0x{HexDigit}+)
BinaryLiteral			= (0b{BinaryDigit}+)
FloatLiteral			= (({Digit}+)("."{Digit}+)?(e[+-]?{Digit}+)? | ({Digit}+)?("."{Digit}+)(e[+-]?{Digit}+)?)
ErrorNumberFormat			= (({IntegerLiteral}|{HexLiteral}|{BinaryLiteral}|{FloatLiteral}){NonSeparator}+)


Separator				= ([\(\)\{\}\[\]])
Separator2				= ([\;,.])

Identifier				= ({IdentifierStart}{IdentifierPart}*)
//Label				    = (({Letter}|{Digit})+[\:])
Label				    = (({Identifier})+[\:])


URLGenDelim				= ([:\/\?#\[\]@])
URLSubDelim				= ([\!\$&'\(\)\*\+,;=])
URLUnreserved			= ({LetterOrUnderscore}|{Digit}|[\-\.\~])
URLCharacter			= ({URLGenDelim}|{URLSubDelim}|{URLUnreserved}|[%])
URLCharacters			= ({URLCharacter}*)
URLEndCharacter			= ([\/\$]|{Letter}|{Digit})
URL						= (((https?|f(tp|ile))"://"|"www.")({URLCharacters}{URLEndCharacter})?)

Register                = A|B|C|D|E|H|L|M|BC|DE|HL|SP|PSW


/* No string state */
/* No char state */
%state MLC
/* No documentation comment state */
%state EOL_COMMENT

%%

<YYINITIAL> {

	/* Keywords */
	"as"|"bitmask"|"break"|"continue"|"do"|"else"|"extern"|"goto"|"if"|
    "inline"|"loop"|"mem"|"port"|"proc"|"return"|"saveregs"|"use"|
    "var"|"while"|"struct"|"typedef"|"absolute"|"$org"|"$encoding"|"$output"|"$cleanup" 		{ addToken(Token.RESERVED_WORD); }

      /* Keywords 2 (just an optional set of keywords colored differently) */
      "high"|"low"|"sizeof"|"signed" { addToken(Token.RESERVED_WORD_2); }


	/* Assembler instructions */
    "CALL"|"CC"|"CM"|"CNC"|"CNZ"|"CP"|"CPE"|"CPO"|"CZ"|"JC"|"JM"|"JMP"|"JNC"|"JNZ"|"JP"|"JPE"|"JPO"|
    "JZ"|"LDA"|"LHLD"|"SHLD"|"STA"|"CMA"|"CMC"|"DAA"|"DI"|"EI"|"HLT"|"NOP"|"PCHL"|"RAL"|"RAR"|"RC"|
    "RET"|"RIM"|"RLC"|"RM"|"RNC"|"RNZ"|"RP"|"RPE"|"RPO"|"RRC"|"RZ"|"SIM"|"SPHL"|"STC"|"XCHG"|"XTHL"|
    "MOV"|"ADC"|"ADD"|"ANA"|"CMP"|"DCR"|"INR"|"ORA"|"SBB"|"SUB"|"XRA"|"ACI"|"ADI"|"ANI"|"CPI"|"LXI"|
    "MVI"|"ORI"|"SBI"|"SUI"|"XRI"|"IN"|"OUT"|"DAD"|"DCX"|"INX"|"POP"|"PUSH"|"LDAX"|"STAX"|"RST" |
	
    "call"|"cc"|"cm"|"cnc"|"cnz"|"cp"|"cpe"|"cpo"|"cz"|"jc"|"jm"|"jmp"|"jnc"|"jnz"|"jp"|"jpe"|"jpo"|
    "jz"|"lda"|"lhld"|"shld"|"sta"|"cma"|"cmc"|"daa"|"di"|"ei"|"hlt"|"nop"|"pchl"|"ral"|"rar"|"rc"|
    "ret"|"rim"|"rlc"|"rm"|"rnc"|"rnz"|"rp"|"rpe"|"rpo"|"rrc"|"rz"|"sim"|"sphl"|"stc"|"xchg"|"xthl"|
    "mov"|"adc"|"add"|"ana"|"cmp"|"dcr"|"inr"|"ora"|"sbb"|"sub"|"xra"|"aci"|"adi"|"ani"|"cpi"|"lxi"|
    "mvi"|"ori"|"sbi"|"sui"|"xri"|"in"|"out"|"dad"|"dcx"|"inx"|"pop"|"push"|"ldax"|"stax"|"rst"	  { addToken(Token.CPU_INSTRUCTION); }

	/* Registers */
	{Register}                            { addToken(Token.REGISTER); }
    {Register}("."{Register})*            { addToken(Token.REGISTER); }

      /* Data types */
    "byte"|"word"|"dword"|"qword"|"ptr"     { addToken(Token.DATA_TYPE); }
	/* Functions */
	/* No functions */

	

	{LineTerminator}				{ addNullToken(); return firstToken; }

	{Identifier}					{ addToken(Token.IDENTIFIER); }

    {Label}                         { addToken(Token.LABEL); }

	{WhiteSpace}					{ addToken(Token.WHITESPACE); }

    /* Preprocessor directives */
    "#"{WhiteSpace}*{PreprocessorWord}	{ addToken(Token.PREPROCESSOR); }


	/* String/Character literals. */
	{CharLiteral}				{ addToken(Token.LITERAL_CHAR); }
{UnclosedCharLiteral}		{ addToken(Token.ERROR_CHAR); addNullToken(); return firstToken; }
{ErrorCharLiteral}			{ addToken(Token.ERROR_CHAR); }
	{StringLiteral}				{ addToken(Token.LITERAL_STRING_DOUBLE_QUOTE); }
{UnclosedStringLiteral}		{ addToken(Token.ERROR_STRING_DOUBLE); addNullToken(); return firstToken; }
{ErrorStringLiteral}			{ addToken(Token.ERROR_STRING_DOUBLE); }

	/* Comment literals. */
	{MLCBegin}	{ start = zzMarkedPos-2; yybegin(MLC); }
	/* No documentation comments */
	{LineCommentBegin}			{ start = zzMarkedPos-1; yybegin(EOL_COMMENT); }
	{CLineCommentBegin}			{ start = zzMarkedPos-2; yybegin(EOL_COMMENT); }

	/* Separators. */
	{Separator}					{ addToken(Token.SEPARATOR); }
	{Separator2}					{ addToken(Token.IDENTIFIER); }

	/* Operators. */
	"!" |"%" |"%=" |"&" |"&&" |"*" |"*=" |"+" |"++" |"+=" |"," |"-" |"--" |"-=" |
    "/" |"/=" |":" |"<" |"<<" |"<<=" |"=" |"==" |">" |">>" |">>=" |"?" |"^" |"|" |
    "||" |"~"		{ addToken(Token.OPERATOR); }

	/* Numbers */
	{IntegerLiteral}				{ addToken(Token.LITERAL_NUMBER_DECIMAL_INT); }
	{HexLiteral}					{ addToken(Token.LITERAL_NUMBER_HEXADECIMAL); }
    {BinaryLiteral}					{ addToken(Token.LITERAL_NUMBER_BINARY); }
	{FloatLiteral}					{ addToken(Token.LITERAL_NUMBER_FLOAT); }
	{ErrorNumberFormat}			{ addToken(Token.ERROR_NUMBER_FORMAT); }

	/* Ended with a line not in a string or comment. */
	<<EOF>>						{ addNullToken(); return firstToken; }

	/* Catch any other (unhandled) characters. */
	.							{ addToken(Token.IDENTIFIER); }

}


/* No char state */

/* No string state */

<MLC> {

	[^hwf\n*]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_MULTILINE); start = zzMarkedPos; }
	[hwf]					{}

	\n						{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }
	{MLCEnd}					{ yybegin(YYINITIAL); addToken(start,zzStartRead+2-1, Token.COMMENT_MULTILINE); }
	"*"						{}
	<<EOF>>					{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }

}


/* No documentation comment state */

<EOL_COMMENT> {
	[^hwf\n]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_EOL); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_EOL); start = zzMarkedPos; }
	[hwf]					{}
	\n						{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }
	<<EOF>>					{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }
}

