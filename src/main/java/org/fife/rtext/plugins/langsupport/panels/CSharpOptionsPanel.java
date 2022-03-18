/*
 * 12/18/2011
 *
 * CSharpOptionsPanel.java - Options for C#.
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
 * Options panel containing options for C#.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CSharpOptionsPanel extends FoldingOnlyOptionsPanel {

	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	public CSharpOptionsPanel(RText app) {
		super(app, "Options.CSharp.Name", SyntaxConstants.SYNTAX_STYLE_CSHARP);
	}


}
