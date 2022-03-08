/*
 * 01/04/2011
 *
 * ConsolePrefs.java - Preferences for the console plugin.
 * Copyright (C) 2011 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.console;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.KeyStroke;

import org.fife.rtext.RTextUtilities;
import org.fife.ui.app.prefs.Prefs;
import org.fife.ui.dockablewindows.DockableWindow;
import org.fife.ui.dockablewindows.DockableWindowConstants;


/**
 * Preferences for the Console plugin.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConsolePrefs extends Prefs {

	/**
	 * Whether the GUI plugin window is active (visible).
	 */
	public boolean windowVisible;

	/**
	 * The location of the dockable console output window.
	 */
	public int windowPosition;

	/**
	 * Key stroke that toggles the console window's visibility.
	 */
	public KeyStroke windowVisibilityAccelerator;

	/**
	 * Whether user input should be syntax highlighted.
	 */
	public boolean syntaxHighlightInput;

	/**
	 * The color used for stdout in consoles.
	 */
	public Color stdoutFG;

	/**
	 * The color used for stderr in consoles.
	 */
	public Color stderrFG;

	/**
	 * The color used for exceptions in consoles.
	 */
	public Color exceptionFG;

	/**
	 * The color used for prompts in consoles.
	 */
	public Color promptFG;


	/**
	 * Overridden to validate the dockable window position value.
	 */
	@Override
	public void load(InputStream in) throws IOException {
		super.load(in);
		// Ensure window position is valid.
		if (!DockableWindow.isValidPosition(windowPosition)) {
			windowPosition = DockableWindowConstants.BOTTOM;
		}
	}


	@Override
	public void setDefaults() {

		windowVisible = false;
		windowPosition = DockableWindowConstants.BOTTOM;
		windowVisibilityAccelerator = null;
		syntaxHighlightInput = true;

		boolean isDark = RTextUtilities.isDarkLookAndFeel();
		if (isDark) {
			stdoutFG = ConsoleTextArea.DEFAULT_DARK_STDOUT_FG;
			stderrFG = ConsoleTextArea.DEFAULT_DARK_STDERR_FG;
			exceptionFG = ConsoleTextArea.DEFAULT_DARK_EXCEPTION_FG;
			promptFG = ConsoleTextArea.DEFAULT_DARK_PROMPT_FG;
		}
		else {
			stdoutFG = ConsoleTextArea.DEFAULT_LIGHT_STDOUT_FG;
			stderrFG = ConsoleTextArea.DEFAULT_LIGHT_STDERR_FG;
			exceptionFG = ConsoleTextArea.DEFAULT_LIGHT_EXCEPTION_FG;
			promptFG = ConsoleTextArea.DEFAULT_LIGHT_PROMPT_FG;
		}
	}


}
