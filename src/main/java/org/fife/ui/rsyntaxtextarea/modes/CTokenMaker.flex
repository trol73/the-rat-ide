/*
 * 11/13/2004
 *
 * CTokenMaker.java - An object that can take a chunk of text and
 * return a linked list of tokens representing it in the C programming
 * language.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE file for details.
 */
package org.fife.ui.rsyntaxtextarea.modes;

import java.io.*;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.*;


/**
 * Scanner for the C programming language.
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
 *   <li>The generated <code>CTokenMaker.java</code> file will contain two
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
 * @author Robert Futrell
 * @version 0.6
 *
 */
%%

%public
%class CTokenMaker
%extends AbstractJFlexCTokenMaker
%unicode
%type org.fife.ui.rsyntaxtextarea.Token


%{


	/**
	 * Constructor.  This must be here because JFlex does not generate a
	 * no-parameter constructor.
	 */
	public CTokenMaker() {
		super();
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
	 * {@inheritDoc}
	 */
	@Override
	public String[] getLineCommentStartAndEnd(int languageIndex) {
		return new String[] { "//", null };
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

Letter				= [A-Za-z]
LetterOrUnderscore	= ({Letter}|[_])
Digit				= [0-9]
HexDigit			= {Digit}|[A-Fa-f]
OctalDigit			= [0-7]
BinaryDigit			= ([0-1])
Exponent			= [eE][+-]?{Digit}+

PreprocessorWord	= define|elif|else|endif|error|if|ifdef|ifndef|include|line|pragma|undef|warning
IncludeWord         = include

Trigraph				= ("??="|"??("|"??)"|"??/"|"??'"|"??<"|"??>"|"??!"|"??-")

OctEscape1			= ([\\]{OctalDigit})
OctEscape2			= ([\\]{OctalDigit}{OctalDigit})
OctEscape3			= ([\\][0-3]{OctalDigit}{OctalDigit})
OctEscape				= ({OctEscape1}|{OctEscape2}|{OctEscape3})
HexEscape				= ([\\][xX]{HexDigit}{HexDigit})

AnyChrChr					= ([^\'\n\\])
Escape					= ([\\]([abfnrtv\'\"\?\\0e]))
UnclosedCharLiteral			= ([\']({Escape}|{OctEscape}|{HexEscape}|{Trigraph}|{AnyChrChr}))
CharLiteral				= ({UnclosedCharLiteral}[\'])
ErrorUnclosedCharLiteral		= ([\'][^\'\n]*)
ErrorCharLiteral			= (([\'][\'])|{ErrorUnclosedCharLiteral}[\'])
AnyStrChr					= ([^\"\n\\])
FalseTrigraph				= (("?"(("?")*)[^\=\(\)\/\'\<\>\!\-\\\?\"\n])|("?"[\=\(\)\/\'\<\>\!\-]))
StringLiteral				= ([\"]((((("?")*)({Escape}|{OctEscape}|{HexEscape}|{Trigraph}))|{FalseTrigraph}|{AnyStrChr})*)(("?")*)[\"])
UnclosedStringLiteral		= ([\"]([\\].|[^\\\"])*[^\"]?)
ErrorStringLiteral			= ({UnclosedStringLiteral}[\"])


LineTerminator		= \n
WhiteSpace		= [ \t\f]

MLCBegin			= "/*"
MLCEnd			= "*/"
LineCommentBegin	= "//"

NonFloatSuffix		= (([uU][lL]?)|([lL][uU]?)|([uU][lL][lL]?))
IntegerLiteral		= ({Digit}+{Exponent}?{NonFloatSuffix}?)
HexLiteral		    = ("0"[xX]{HexDigit}+{NonFloatSuffix}?)
BinaryLiteral		= (0b{BinaryDigit}+)
FloatLiteral		= ((({Digit}*[\.]{Digit}+)|({Digit}+[\.]{Digit}*)){Exponent}?[fFlL]?)
ErrorNumberFormat	= (({IntegerLiteral}|{HexLiteral}|{BinaryLiteral}|{FloatLiteral}){NonSeparator}+)

NonSeparator		= ([^\t\f\r\n\ \(\)\{\}\[\]\;\,\.\=\>\<\!\~\?\:\+\-\*\/\&\|\^\%\"\']|"#")
Identifier		= ({LetterOrUnderscore}({LetterOrUnderscore}|{Digit}|[$])*)
ErrorIdentifier	= ({NonSeparator}+)


URLGenDelim				= ([:\/\?#\[\]@])
URLSubDelim				= ([\!\$&'\(\)\*\+,;=])
URLUnreserved			= ({LetterOrUnderscore}|{Digit}|[\-\.\~])
URLCharacter			= ({URLGenDelim}|{URLSubDelim}|{URLUnreserved}|[%])
URLCharacters			= ({URLCharacter}*)
URLEndCharacter			= ([\/\$]|{Letter}|{Digit})
URL						= (((https?|f(tp|ile))"://"|"www.")({URLCharacters}{URLEndCharacter})?)

%state MLC
%state EOL_COMMENT

%%

<YYINITIAL> {

	/* Keywords */
	"auto" |
	"break" |
	"case" |
	"const" |
	"continue" |
	"default" |
	"do" |
	"else" |
	"enum" |
	"extern" |
	"for" |
	"goto" |
	"if" |
	"inline" |
	"register" |
	"sizeof" |
	"static" |
	"struct" |
	"switch" |
	"typedef" |
	"union" |
	"volatile" |
	"void" |
	"while"	|
	"restrict"	|
    "return"				{ addToken(Token.RESERVED_WORD); }

    "asm" |
    "ISR" |
	"true" |
	"false"				{ addToken(Token.RESERVED_WORD_2); }

	/* Data types. */
	"char" |
	"div_t" |
	"double" |
	"float" |
	"int" |
	"ldiv_t" |
	"long" |
	"short" |
	"signed" |
	"size_t" |
	"unsigned" |
	"int8_t" |
    "uint8_t" |
    "int16_t" |
    "uint16_t" |
    "int32_t" |
    "uint32_t" |
    "int64_t" |
    "uint64_t" |
	"wchar_t" |
	"bool"				{ addToken(Token.DATA_TYPE); }

	/* Standard functions */
	"abort" |
	"abs" |
	"acos" |
	"asctime" |
	"asin" |
	"assert" |
	"atan2" |
	"atan" |
	"atexit" |
	"atof" |
	"atoi" |
	"atol" |
	"bsearch" |
	"btowc" |
	"calloc" |
	"ceil" |
	"clearerr" |
	"clock" |
	"cosh" |
	"cos" |
	"ctime" |
	"difftime" |
	"div" |
	"errno" |
	"exit" |
	"exp" |
	"fabs" |
	"fclose" |
	"feof" |
	"ferror" |
	"fflush" |
	"fgetc" |
	"fgetpos" |
	"fgetwc" |
	"fgets" |
	"fgetws" |
	"floor" |
	"fmod" |
	"fopen" |
	"fprintf" |
	"fputc" |
	"fputs" |
	"fputwc" |
	"fputws" |
	"fread" |
	"free" |
	"freopen" |
	"frexp" |
	"fscanf" |
	"fseek" |
	"fsetpos" |
	"ftell" |
	"fwprintf" |
	"fwrite" |
	"fwscanf" |
	"getchar" |
	"getc" |
	"getenv" |
	"gets" |
	"getwc" |
	"getwchar" |
	"gmtime" |
	"isalnum" |
	"isalpha" |
	"iscntrl" |
	"isdigit" |
	"isgraph" |
	"islower" |
	"isprint" |
	"ispunct" |
	"isspace" |
	"isupper" |
	"isxdigit" |
	"labs" |
	"ldexp" |
	"ldiv" |
	"localeconv" |
	"localtime" |
	"log10" |
	"log" |
	"longjmp" |
	"malloc" |
	"mblen" |
	"mbrlen" |
	"mbrtowc" |
	"mbsinit" |
	"mbsrtowcs" |
	"mbstowcs" |
	"mbtowc" |
	"memchr" |
	"memcmp" |
	"memcpy" |
	"memmove" |
	"memset" |
	"mktime" |
	"modf" |
	"offsetof" |
	"perror" |
	"pow" |
	"printf" |
	"putchar" |
	"putc" |
	"puts" |
	"putwc" |
	"putwchar" |
	"qsort" |
	"raise" |
	"rand" |
	"realloc" |
	"remove" |
	"rename" |
	"rewind" |
	"scanf" |
	"setbuf" |
	"setjmp" |
	"setlocale" |
	"setvbuf" |
	"setvbuf" |
	"signal" |
	"sinh" |
	"sin" |
	"sprintf" |
	"sqrt" |
	"srand" |
	"sscanf" |
	"strcat" |
	"strchr" |
	"strcmp" |
	"strcmp" |
	"strcoll" |
	"strcpy" |
	"strcspn" |
	"strerror" |
	"strftime" |
	"strlen" |
	"strncat" |
	"strncmp" |
	"strncpy" |
	"strpbrk" |
	"strrchr" |
	"strspn" |
	"strstr" |
	"strtod" |
	"strtok" |
	"strtol" |
	"strtoul" |
	"strxfrm" |
	"swprintf" |
	"swscanf" |
	"system" |
	"tanh" |
	"tan" |
	"time" |
	"tmpfile" |
	"tmpnam" |
	"tolower" |
	"toupper" |
	"ungetc" |
	"ungetwc" |
	"va_arg" |
	"va_end" |
	"va_start" |
	"vfprintf" |
	"vfwprintf" |
	"vprintf" |
	"vsprintf" |
	"vswprintf" |
	"vwprintf" |
	"wcrtomb" |
	"wcscat" |
	"wcschr" |
	"wcscmp" |
	"wcscoll" |
	"wcscpy" |
	"wcscspn" |
	"wcsftime" |
	"wcslen" |
	"wcsncat" |
	"wcsncmp" |
	"wcsncpy" |
	"wcspbrk" |
	"wcsrchr" |
	"wcsrtombs" |
	"wcsspn" |
	"wcsstr" |
	"wcstod" |
	"wcstok" |
	"wcstol" |
	"wcstombs" |
	"wcstoul" |
	"wcsxfrm" |
	"wctob" |
	"wctomb" |
	"wmemchr" |
	"wmemcmp" |
	"wmemcpy" |
	"wmemmove" |
	"wmemset" |
	"wprintf" |
	"wscanf"				{ addToken(Token.FUNCTION); }

	/* Standard-defined macros. */
	"__DATE__" |
	"__TIME__" |
	"__FILE__" |
	"__LINE__" |
	"__STDC__"				{ addToken(Token.PREPROCESSOR); }

	{LineTerminator}				{ addNullToken(); return firstToken; }

	{Identifier}					{ addToken(Token.IDENTIFIER); }

	{WhiteSpace}+					{ addToken(Token.WHITESPACE); }

	/* Preprocessor directives */
	"#"{WhiteSpace}*{PreprocessorWord}	{ addToken(Token.PREPROCESSOR); }

    "#"{WhiteSpace}*{IncludeWord}{WhiteSpace}*"<"({Identifier}|"/"|".")*">"	{ addToken(Token.PREPROCESSOR); }


	/* String/Character Literals. */
	{CharLiteral}					{ addToken(Token.LITERAL_CHAR); }
	{UnclosedCharLiteral}			{ addToken(Token.ERROR_CHAR); /*addNullToken(); return firstToken;*/ }
	{ErrorUnclosedCharLiteral}		{ addToken(Token.ERROR_CHAR); addNullToken(); return firstToken; }
	{ErrorCharLiteral}				{ addToken(Token.ERROR_CHAR); }
	{StringLiteral}				{ addToken(Token.LITERAL_STRING_DOUBLE_QUOTE); }
	{UnclosedStringLiteral}			{ addToken(Token.ERROR_STRING_DOUBLE); addNullToken(); return firstToken; }
	{ErrorStringLiteral}			{ addToken(Token.ERROR_STRING_DOUBLE); }

	/* Comment Literals. */
	{MLCBegin}					{ start = zzMarkedPos-2; yybegin(MLC); }
	{LineCommentBegin}			{ start = zzMarkedPos-2; yybegin(EOL_COMMENT); }

	/* Separators. */
	"(" |
	")" |
	"[" |
	"]" |
	"{" |
	"}"							{ addToken(Token.SEPARATOR); }

	/* Operators. */
	{Trigraph} |
	"=" |
	"+" |
	"-" |
	"*" |
	"/" |
	"%" |
	"~" |
	"<" |
	">" |
	"<<" |
	">>" |
	"==" |
	"+=" |
	"-=" |
	"*=" |
	"/=" |
	"%=" |
	"&=" |
	"|=" |
	"^=" |
	">=" |
	"<=" |
	"!=" |
	">>=" |
	"<<=" |
	"^" |
	"&" |
	"&&" |
	"|" |
	"||" |
	"?" |
	":" |
	"," |
	"!" |
	"++" |
	"--" |
	"." |
	","	|
	";" { addToken(Token.OPERATOR); }

	/* Numbers */
	{IntegerLiteral}				{ addToken(Token.LITERAL_NUMBER_DECIMAL_INT); }
	{HexLiteral}					{ addToken(Token.LITERAL_NUMBER_HEXADECIMAL); }
	{BinaryLiteral}					{ addToken(Token.LITERAL_NUMBER_BINARY); }
	{FloatLiteral}					{ addToken(Token.LITERAL_NUMBER_FLOAT); }
	{ErrorNumberFormat}				{ addToken(Token.ERROR_NUMBER_FORMAT); }

	/* Some lines will end in '\' to wrap an expression. */
	"\\"							{ addToken(Token.IDENTIFIER); }

	{ErrorIdentifier}				{ addToken(Token.ERROR_IDENTIFIER); }

	/* Other punctuation, we'll highlight it as "identifiers." */
	";"							{ addToken(Token.IDENTIFIER); }

	/* Ended with a line not in a string or comment. */
	<<EOF>>						{ addNullToken(); return firstToken; }

	/* Catch any other (unhandled) characters and flag them as bad. */
	.							{ addToken(Token.ERROR_IDENTIFIER); }

}

<MLC> {

	[^hwf\n\*]+					{}
	{URL}						{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_MULTILINE); start = zzMarkedPos; }
	[hwf]						{}

	\n							{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }
	{MLCEnd}						{ yybegin(YYINITIAL); addToken(start,zzStartRead+1, Token.COMMENT_MULTILINE); }
	\*							{}
	<<EOF>>						{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }

}


<EOL_COMMENT> {
	[^hwf\n]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_EOL); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_EOL); start = zzMarkedPos; }
	[hwf]					{}
	\n						{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }
	<<EOF>>					{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }
}
