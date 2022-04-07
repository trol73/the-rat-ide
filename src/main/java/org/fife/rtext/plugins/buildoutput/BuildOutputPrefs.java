/*
 * 01/04/2011
 *
 * ConsolePrefs.java - Preferences for the build output plugin.
 * Copyright (C) 2011 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.buildoutput;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.KeyStroke;

import org.fife.rtext.RTextUtilities;
import org.fife.ui.app.prefs.Prefs;
import org.fife.ui.dockablewindows.DockableWindow;
import org.fife.ui.dockablewindows.DockableWindowConstants;


public class BuildOutputPrefs extends Prefs {

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
    public Color background;
    public Color filesFG;
    public Color posFG;
    public Color warningsFG;
    public Color errorsFG;


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
            stdoutFG = BuildOutputTextArea.DEFAULT_DARK_STDOUT_FG;
            stderrFG = BuildOutputTextArea.DEFAULT_DARK_STDERR_FG;
            exceptionFG = BuildOutputTextArea.DEFAULT_DARK_EXCEPTION_FG;
            promptFG = BuildOutputTextArea.DEFAULT_DARK_PROMPT_FG;
            background = BuildOutputTextArea.DEFAULT_DARK_BACKGROUND;
            filesFG = BuildOutputTextArea.DEFAULT_DARK_FILES_FG;
            posFG = BuildOutputTextArea.DEFAULT_DARK_POS_FG;
            warningsFG = BuildOutputTextArea.DEFAULT_DARK_WARNINGS_FG;
            errorsFG = BuildOutputTextArea.DEFAULT_DARK_ERRORS_FG;
        } else {
            stdoutFG = BuildOutputTextArea.DEFAULT_LIGHT_STDOUT_FG;
            stderrFG = BuildOutputTextArea.DEFAULT_LIGHT_STDERR_FG;
            exceptionFG = BuildOutputTextArea.DEFAULT_LIGHT_EXCEPTION_FG;
            promptFG = BuildOutputTextArea.DEFAULT_LIGHT_PROMPT_FG;
            background = BuildOutputTextArea.DEFAULT_LIGHT_BACKGROUND;
            filesFG = BuildOutputTextArea.DEFAULT_LIGHT_FILES_FG;
            posFG = BuildOutputTextArea.DEFAULT_LIGHT_POS_FG;
            warningsFG = BuildOutputTextArea.DEFAULT_LIGHT_WARNINGS_FG;
            errorsFG = BuildOutputTextArea.DEFAULT_LIGHT_ERRORS_FG;
        }
    }


}
