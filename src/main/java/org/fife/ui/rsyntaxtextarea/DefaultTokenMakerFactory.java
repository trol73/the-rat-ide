/*
 * 12/14/2008
 *
 * DefaultTokenMakerFactory.java - The default TokenMaker factory.
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE file for details.
 */
package org.fife.ui.rsyntaxtextarea;


/**
 * The default implementation of <code>TokenMakerFactory</code>.  This factory
 * can create {@link TokenMaker}s for all languages known to
 * {@link RSyntaxTextArea}.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class DefaultTokenMakerFactory extends AbstractTokenMakerFactory implements SyntaxConstants {

	@Override
	protected void initTokenMakerMap() {
		String pkg = "org.fife.ui.rsyntaxtextarea.modes.";
		String pkgEx = "ru.trolsoft.ide.syntaxhighlight.";

		putMapping(SYNTAX_STYLE_NONE,			pkg + "PlainTextTokenMaker");
		putMapping(SYNTAX_STYLE_ACTIONSCRIPT,	pkg + "ActionScriptTokenMaker");
		putMapping(SYNTAX_STYLE_ASSEMBLER_X86,	pkg + "AssemblerX86TokenMaker");
		putMapping(SYNTAX_STYLE_ASSEMBLER_6502,	pkg + "Assembler6502TokenMaker");
		putMapping(SYNTAX_STYLE_ASSEMBLER_8085,	pkg + "Assembler8085TokenMaker");
		putMapping(SYNTAX_STYLE_ASSEMBLER_AVR,	pkg + "AssemblerAvrTokenMaker");
		putMapping(SYNTAX_STYLE_I8085_RAT,	pkg + "I8085RatTokenMaker");
		putMapping(SYNTAX_STYLE_AVR_RAT,	    pkgEx + "AvrRatTokenMaker");
		putMapping(SYNTAX_STYLE_CHIP_TEST,	    pkg + "ChipTestTokenMaker");
		putMapping(SYNTAX_STYLE_BBCODE,			pkg + "BBCodeTokenMaker");
		putMapping(SYNTAX_STYLE_C,				pkgEx + "CTokenMaker");
		putMapping(SYNTAX_STYLE_CLOJURE,		pkg + "ClojureTokenMaker");
		putMapping(SYNTAX_STYLE_CPLUSPLUS,		pkg + "CPlusPlusTokenMaker");
		putMapping(SYNTAX_STYLE_CSHARP,			pkg + "CSharpTokenMaker");
		putMapping(SYNTAX_STYLE_CSS,			pkg + "CSSTokenMaker");
		putMapping(SYNTAX_STYLE_CSV,			pkg + "CsvTokenMaker");
		putMapping(SYNTAX_STYLE_D,				pkg + "DTokenMaker");
		putMapping(SYNTAX_STYLE_DART,			pkg + "DartTokenMaker");
		putMapping(SYNTAX_STYLE_DELPHI,			pkg + "DelphiTokenMaker");
		putMapping(SYNTAX_STYLE_DOCKERFILE,		pkg + "DockerTokenMaker");
		putMapping(SYNTAX_STYLE_DTD,			pkg + "DtdTokenMaker");
		putMapping(SYNTAX_STYLE_FORTRAN,		pkg + "FortranTokenMaker");
		putMapping(SYNTAX_STYLE_GO,				pkg + "GoTokenMaker");
		putMapping(SYNTAX_STYLE_GROOVY,			pkg + "GroovyTokenMaker");
		putMapping(SYNTAX_STYLE_HOSTS,			pkg + "HostsTokenMaker");
		putMapping(SYNTAX_STYLE_HTACCESS,		pkg + "HtaccessTokenMaker");
		putMapping(SYNTAX_STYLE_HTML,			pkg + "HTMLTokenMaker");
		putMapping(SYNTAX_STYLE_INI,			pkg + "IniTokenMaker");
		putMapping(SYNTAX_STYLE_INTEL_HEX,		pkg + "IntelHexTokenMaker");
		putMapping(SYNTAX_STYLE_JAVA,			pkg + "JavaTokenMaker");
		putMapping(SYNTAX_STYLE_JAVASCRIPT,		pkg + "JavaScriptTokenMaker");
		putMapping(SYNTAX_STYLE_JSON_WITH_COMMENTS,	pkg + "JshintrcTokenMaker");
		putMapping(SYNTAX_STYLE_JSON,			pkg + "JsonTokenMaker");
		putMapping(SYNTAX_STYLE_JSP,			pkg + "JSPTokenMaker");
		putMapping(SYNTAX_STYLE_KOTLIN,			pkg + "KotlinTokenMaker");
		putMapping(SYNTAX_STYLE_LATEX,			pkg + "LatexTokenMaker");
		putMapping(SYNTAX_STYLE_LESS,			pkg + "LessTokenMaker");
		putMapping(SYNTAX_STYLE_LISP,			pkg + "LispTokenMaker");
		putMapping(SYNTAX_STYLE_LUA,			pkg + "LuaTokenMaker");
		putMapping(SYNTAX_STYLE_MAKEFILE,		pkg + "MakefileTokenMaker");
		putMapping(SYNTAX_STYLE_MARKDOWN,		pkg + "MarkdownTokenMaker");
		putMapping(SYNTAX_STYLE_MXML,			pkg + "MxmlTokenMaker");
		putMapping(SYNTAX_STYLE_NSIS,			pkg + "NSISTokenMaker");
		putMapping(SYNTAX_STYLE_PERL,			pkg + "PerlTokenMaker");
		putMapping(SYNTAX_STYLE_PHP,			pkg + "PHPTokenMaker");
		putMapping(SYNTAX_STYLE_PROPERTIES_FILE,pkg + "PropertiesFileTokenMaker");
		putMapping(SYNTAX_STYLE_PYTHON,			pkg + "PythonTokenMaker");
		putMapping(SYNTAX_STYLE_RUBY,			pkg + "RubyTokenMaker");
		putMapping(SYNTAX_STYLE_SAS,			pkg + "SASTokenMaker");
		putMapping(SYNTAX_STYLE_SCALA,			pkg + "ScalaTokenMaker");
		putMapping(SYNTAX_STYLE_SQL,			pkg + "SQLTokenMaker");
		putMapping(SYNTAX_STYLE_TCL,			pkg + "TclTokenMaker");
		putMapping(SYNTAX_STYLE_TYPESCRIPT,		pkg + "TypeScriptTokenMaker");
		putMapping(SYNTAX_STYLE_UNIX_SHELL,		pkg + "UnixShellTokenMaker");
		putMapping(SYNTAX_STYLE_VISUAL_BASIC,	pkg + "VisualBasicTokenMaker");
		putMapping(SYNTAX_STYLE_WINDOWS_BATCH,	pkg + "WindowsBatchTokenMaker");
		putMapping(SYNTAX_STYLE_XML,			pkg + "XMLTokenMaker");
		putMapping(SYNTAX_STYLE_YAML,			pkg + "YamlTokenMaker");
	}


}
