/*
 * 11/28/2014
 *
 * DOptionsPanel.java - Options for D.
 * Copyright (C) 2014 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.langsupport;

import org.fife.rtext.RText;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;


/**
 * Options panel containing options for D.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class DOptionsPanel extends FoldingOnlyOptionsPanel {


	/**
	 * Constructor.
	 *
	 * @param app  The parent application.
	 */
	DOptionsPanel(RText app) {
		super(app, "Options.D.Name", SyntaxConstants.SYNTAX_STYLE_D);
	}


}
