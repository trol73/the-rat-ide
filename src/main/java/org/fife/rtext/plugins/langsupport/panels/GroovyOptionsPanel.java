/*
 * 12/18/2011
 *
 * GroovyOptionsPanel.java - Options for Groovy.
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
 * Options panel containing options for Groovy.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class GroovyOptionsPanel extends FoldingOnlyOptionsPanel {


	/**
	 * Constructor.
	 */
	public GroovyOptionsPanel(RText app) {
		super(app, "Options.Groovy.Name", SyntaxConstants.SYNTAX_STYLE_GROOVY);
	}


}
