/*
 * 12/17/2010
 *
 * ConsoleWindow.java - Text component for the console.
 * Copyright (C) 2010 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.buildoutput;

import org.fife.rtext.RText;
import org.fife.ui.widgets.RScrollPane;
import org.fife.ui.utils.UIUtil;
import org.fife.ui.utils.WebLookAndFeelUtils;
import org.fife.ui.dockablewindows.DockableWindow;
import org.fife.ui.dockablewindows.DockableWindowConstants;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * A dockable window that acts as a console.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class BuildOutputWindow extends DockableWindow implements PropertyChangeListener {

    private final BuildOutputTextArea textArea;

//    private final JToolBar toolbar;
//    private final StopAction stopAction;


    BuildOutputWindow(RText app, Plugin plugin) {
        setDockableWindowName(plugin.getString("DockableWindow.Title"));
        setIcon(plugin.getPluginIcon());
        setPosition(DockableWindowConstants.BOTTOM);
        setLayout(new BorderLayout());

        // Create the main panel, containing the shells.
        CardLayout cards = new CardLayout();
        JPanel mainPanel = new JPanel(cards);
        add(mainPanel);

        textArea = new BuildOutputTextArea(plugin);
        textArea.setEditable(false);
        setPrimaryComponent(textArea);
        textArea.addPropertyChangeListener(BuildOutputTextArea.PROPERTY_PROCESS_RUNNING, this);
        RScrollPane sp = new RScrollPane(textArea);
        UIUtil.removeTabbedPaneFocusTraversalKeyBindings(sp);
        mainPanel.add(sp, "System");

        // Create a "toolbar" for the shells.
//        toolbar = new JToolBar();
//        toolbar.setFloatable(false);
//
//        toolbar.add(Box.createHorizontalGlue());
//
//        stopAction = new StopAction(app, Plugin.MSG, plugin);
//        JButton b = new JButton(stopAction);
//        b.setText(null);
//        toolbar.add(b);
//        WebLookAndFeelUtils.fixToolbar(toolbar);
//        add(toolbar, BorderLayout.NORTH);
    }


    /**
     * Clears any text from all consoles.
     */
    public void clearConsoles() {
        textArea.setEditable(true);
        textArea.clear();
        textArea.setEditable(false);
    }


    /**
     * Returns the color used for a given type of text in the consoles.
     *
     * @param style The style; e.g. {@link BuildOutputTextArea#STYLE_STDOUT}.
     * @return The color, or <code>null</code> if the system default color
     * is being used.
     * @see #setForeground(String, Color)
     */
    public Color getForeground(String style) {
        Color c = null;
        Style s = textArea.getStyle(style);
        if (s != null) {
            c = StyleConstants.getForeground(s);
        }
        return c;
    }

    public Color getBackground(String style) {
        Color c = null;
        Style s = textArea.getStyle(style);
        if (s != null) {
            c = StyleConstants.getBackground(s);
        }
        return c;
    }


    /**
     * Returns whether a special style is used for a given type of text in the consoles.
     *
     * @param style The style of text.
     * @return Whether a special style is used.
     */
    public boolean isStyleUsed(String style) {
        return textArea.getStyle(style).isDefined(StyleConstants.Foreground);
        //return getForeground(style)!=null;
    }

    public boolean isStyleBackgroundUsed(String style) {
        return textArea.getStyle(style).isDefined(StyleConstants.Background);
        //return getForeground(style)!=null;
    }


    /**
     * Called whenever a process starts or completes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (BuildOutputTextArea.PROPERTY_PROCESS_RUNNING.equals(prop)) {
            boolean running = (Boolean) e.getNewValue();
//            stopAction.setEnabled(running);
        }
    }


    /**
     * Changes all consoles to use the default colors for the current application theme.
     */
    public void restoreDefaultColors() {
        textArea.restoreDefaultColors();
    }


    /**
     * Sets the color used for a given type of text in the consoles.
     *
     * @param style The style; e.g. {@link BuildOutputTextArea#STYLE_STDOUT}.
     * @param fg    The new foreground color to use, or <code>null</code> to
     *              use the system default foreground color.
     * @see #getForeground(String)
     */
    public void setForeground(String style, Color fg) {
        Style s = textArea.getStyle(style);
        if (s != null) {
            if (fg != null) {
                StyleConstants.setForeground(s, fg);
            } else {
                s.removeAttribute(StyleConstants.Foreground);
            }
        }
    }

    public void setBackground(String style, Color bg) {
        Style s = textArea.getStyle(style);
        if (s != null) {
            if (bg != null) {
                StyleConstants.setBackground(s, bg);
                textArea.setBackground(bg);
            } else {
                s.removeAttribute(StyleConstants.Background);
            }
        }
    }




    /**
     * Toggles whether user input should be syntax highlighted.
     *
     * @param highlightInput Whether to syntax highlight user input.
     */
    public void setSyntaxHighlightInput(boolean highlightInput) {
        textArea.refreshUserInputStyles();
    }


    /**
     * Stops the currently running process, if any.
     */
    public void stopCurrentProcess() {
        textArea.stopCurrentProcess();
    }


    @Override
    public void updateUI() {
        super.updateUI();
//        if (toolbar != null) {
//            WebLookAndFeelUtils.fixToolbar(toolbar);
//        }
    }

    public void execute(String cmd) {
        textArea.handleSubmit(cmd);
    }

    public void execute(BuildTask task) {
        textArea.startTask(task);
    }

    public void prompt(String msg) {
        textArea.appendPrompt(msg);
    }
}
