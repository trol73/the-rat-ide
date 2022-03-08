/*
 * 12/18/2011
 *
 * CPlusPlusOptionsPanel.java - Options for C++.
 * Copyright (C) 2011 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.langsupport;

import org.fife.rtext.RText;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;


/**
 * Options panel containing options for C++.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class CPlusPlusOptionsPanel extends FoldingOnlyOptionsPanel {


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	CPlusPlusOptionsPanel(RText app) {
		super(app, "Options.CPlusPlus.Name",
				SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
	}


}
