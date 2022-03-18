/*
 * 12/29/2012
 *
 * JsonOptionsPanel.java - Options for JSON.
 * Copyright (C) 2011 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.langsupport.panels;

import org.fife.rtext.RText;
import org.fife.rtext.plugins.langsupport.FoldingOnlyOptionsPanel;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;


/**
 * Options panel containing options for .jshintrc files.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JSHintOptionsPanel extends FoldingOnlyOptionsPanel {


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public JSHintOptionsPanel(RText app) {
		super(app, "Options.JSHint.Name", SyntaxConstants.SYNTAX_STYLE_JSON_WITH_COMMENTS);
	}


}
