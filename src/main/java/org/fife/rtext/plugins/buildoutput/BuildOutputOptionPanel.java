/*
 * 12/22/2010
 *
 * ConsoleOptionPanel.java - Option panel for managing the Console plugin.
 * Copyright (C) 2010 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.buildoutput;

import org.fife.rtext.AbstractConsoleTextAreaOptionPanel;
import org.fife.rtext.RTextUtilities;
import org.fife.ui.RColorSwatchesButton;
import org.fife.ui.utils.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class BuildOutputOptionPanel extends AbstractConsoleTextAreaOptionPanel<Plugin> implements ActionListener {

    /**
     * ID used to identify this option panel.
     */
    private static final String OPTION_PANEL_ID = "BuildOptionPanel";

    private JCheckBox highlightInputCB;

    protected JCheckBox cbFiles;
    protected RColorSwatchesButton filesButton;
    protected JCheckBox cbPositions;
    protected RColorSwatchesButton positionsButton;
    protected JCheckBox cbErrors;
    protected RColorSwatchesButton errorsButton;
    protected JCheckBox cbWarnings;
    protected RColorSwatchesButton warningsButton;



    /**
     * Constructor.
     *
     * @param plugin The plugin.
     */
    BuildOutputOptionPanel(Plugin plugin) {
        super(plugin);
        setId(OPTION_PANEL_ID);
        setName(plugin.getString("Options.Title"));
        setId(OPTION_PANEL_ID);
        ComponentOrientation o = ComponentOrientation.getOrientation(getLocale());

        // Set up our border and layout.
        setBorder(UIUtil.getEmpty5Border());
        setLayout(new BorderLayout());
        Box topPanel = Box.createVerticalBox();

        // Add the "general" options panel.
        Container generalPanel = createGeneralPanel();
        topPanel.add(generalPanel);
        topPanel.add(Box.createVerticalStrut(5));

        // Add the "colors" option panel.
        Container colorsPanel = createColorsPanel();
        topPanel.add(colorsPanel);
        topPanel.add(Box.createVerticalStrut(5));

        addRestoreDefaultsButton(topPanel);

        // Put it all together!
        topPanel.add(Box.createVerticalGlue());
        add(topPanel, BorderLayout.NORTH);
        applyComponentOrientation(o);
    }


    @Override
    protected int createExtraColorPickers(JPanel panel) {
        cbFiles = createColorActivateCB(getPlugin().getString("Color.Files"));
        filesButton = createColorSwatchesButton();
        cbPositions = createColorActivateCB(getPlugin().getString("Color.Positions"));
        positionsButton = createColorSwatchesButton();
        cbWarnings = createColorActivateCB(getPlugin().getString("Color.Warnings"));
        warningsButton = createColorSwatchesButton();
        cbErrors = createColorActivateCB(getPlugin().getString("Color.Errors"));
        errorsButton = createColorSwatchesButton();

        panel.add(cbFiles);
        panel.add(filesButton);
        panel.add(cbPositions);
        panel.add(positionsButton);
        panel.add(cbWarnings);
        panel.add(warningsButton);
        panel.add(cbErrors);
        panel.add(errorsButton);

        return 4;
    }

    /**
     * Called when the user toggles various properties in this panel.
     *
     * @param e The event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Parent class listens to color buttons and "Restore Defaults"
        super.actionPerformed(e);

        Object source = e.getSource();

        if (highlightInputCB == source) {
            setDirty(true);
        } else if (cbFiles == source) {
            boolean selected = cbFiles.isSelected();
            filesButton.setEnabled(selected);
            setDirty(true);
        } else if (cbPositions == source) {
            boolean selected = cbPositions.isSelected();
            positionsButton.setEnabled(selected);
            setDirty(true);
        } else if (cbWarnings == source) {
            boolean selected = cbWarnings.isSelected();
            warningsButton.setEnabled(selected);
            setDirty(true);
        } else if (cbErrors == source) {
            boolean selected = cbErrors.isSelected();
            errorsButton.setEnabled(selected);
            setDirty(true);
        }
    }


    /**
     * Overridden to add our "syntax highlight user input" checkbox.
     *
     * @param parent The container to add the content in.
     */
    @Override
    protected void addExtraColorRelatedContent(Box parent) {
        highlightInputCB = createColorActivateCB(getPlugin().getString("Highlight.Input"));
        addLeftAligned(parent, highlightInputCB);
    }


    @Override
    protected void doApplyImpl(Frame owner) {
        Plugin plugin = getPlugin();
        BuildOutputWindow window = plugin.getDockableWindow();
        window.setActive(visibleCB.isSelected());
        window.setPosition(locationCombo.getSelectedIndex());

        plugin.setSyntaxHighlightInput(highlightInputCB.isSelected());

        Color c = cbExceptions.isSelected() ? exceptionsButton.getColor() : null;
        window.setForeground(BuildOutputTextArea.STYLE_EXCEPTION, c);
        c = cbPrompt.isSelected() ? promptButton.getColor() : null;
        window.setForeground(BuildOutputTextArea.STYLE_PROMPT, c);
        c = cbStdout.isSelected() ? stdoutButton.getColor() : null;
        window.setForeground(BuildOutputTextArea.STYLE_STDOUT, c);
        c = cbStderr.isSelected() ? stderrButton.getColor() : null;
        window.setForeground(BuildOutputTextArea.STYLE_STDERR, c);
        c = cbBackground.isSelected() ? backgroundButton.getColor() : null;
        window.setBackground(BuildOutputTextArea.STYLE_BACKGROUND, c);

        c = cbFiles.isSelected() ? filesButton.getColor() : null;
        window.setBackground(BuildOutputTextArea.STYLE_FILE, c);
        c = cbPositions.isSelected() ? positionsButton.getColor() : null;
        window.setBackground(BuildOutputTextArea.STYLE_FILE_POS, c);
        c = cbWarnings.isSelected() ? warningsButton.getColor() : null;
        window.setBackground(BuildOutputTextArea.STYLE_WARNINGS, c);
        c = cbErrors.isSelected() ? errorsButton.getColor() : null;
        window.setBackground(BuildOutputTextArea.STYLE_ERRORS, c);
    }


    /**
     * Always returns <code>null</code>, as the user cannot enter invalid
     * input on this panel.
     *
     * @return <code>null</code> always.
     */
    @Override
    protected OptionsPanelCheckResult ensureValidInputsImpl() {
        return null;
    }


    @Override
    protected boolean notDefaults() {
        if (super.notDefaults()) {
            return true;
        }
        boolean isDark = RTextUtilities.isDarkLookAndFeel();
        Color defaultFiles = isDark ? BuildOutputTextArea.DEFAULT_DARK_FILES_FG : BuildOutputTextArea.DEFAULT_LIGHT_FILES_FG;
        Color defaultPos = isDark ? BuildOutputTextArea.DEFAULT_DARK_POS_FG : BuildOutputTextArea.DEFAULT_LIGHT_POS_FG;
        Color defaultWarnings = isDark ? BuildOutputTextArea.DEFAULT_DARK_WARNINGS_FG : BuildOutputTextArea.DEFAULT_LIGHT_WARNINGS_FG;
        Color defaultErrors = isDark ? BuildOutputTextArea.DEFAULT_DARK_ERRORS_FG : BuildOutputTextArea.DEFAULT_LIGHT_ERRORS_FG;

        return !highlightInputCB.isSelected() ||
                !cbFiles.isSelected() ||
                !cbPositions.isSelected() ||
                !cbWarnings.isSelected() ||
                !cbErrors.isSelected() ||
                !defaultFiles.equals(filesButton.getColor()) ||
                !defaultPos.equals(positionsButton.getColor()) ||
                !defaultWarnings.equals(warningsButton.getColor()) ||
                !defaultErrors.equals(errorsButton.getColor());
    }

    @Override
    protected void restoreDefaultsForColorsPanel() {
        super.restoreDefaultsForColorsPanel();

        cbFiles.setSelected(true);
        cbPositions.setSelected(true);
        cbWarnings.setSelected(true);
        cbErrors.setSelected(true);

        boolean isDark = RTextUtilities.isDarkLookAndFeel();
        if (isDark) {
            filesButton.setColor(BuildOutputTextArea.DEFAULT_DARK_FILES_FG);
            positionsButton.setColor(BuildOutputTextArea.DEFAULT_DARK_POS_FG);
            warningsButton.setColor(BuildOutputTextArea.DEFAULT_DARK_WARNINGS_FG);
            errorsButton.setColor(BuildOutputTextArea.DEFAULT_DARK_ERRORS_FG);
        } else {
            filesButton.setColor(BuildOutputTextArea.DEFAULT_LIGHT_FILES_FG);
            positionsButton.setColor(BuildOutputTextArea.DEFAULT_LIGHT_POS_FG);
            warningsButton.setColor(BuildOutputTextArea.DEFAULT_LIGHT_WARNINGS_FG);
            errorsButton.setColor(BuildOutputTextArea.DEFAULT_LIGHT_ERRORS_FG);
        }
    }

    /**
     * Overridden to set all colors to values appropriate for the current Look
     * and Feel.
     *
     * @param event The broadcasted event.
     */
    @Override
    public void optionsEvent(String event) {
        restoreDefaultColors();
        super.optionsEvent(event);
    }


    /**
     * Changes all consoles to use the default colors for the current
     * application theme.
     */
    private void restoreDefaultColors() {
        Plugin plugin = getPlugin();
        plugin.restoreDefaultColors();
        setValues(plugin.getApplication());
    }


    @Override
    protected void restoreDefaults() {
        super.restoreDefaults();
        highlightInputCB.setSelected(true);
    }


    @Override
    protected void setValuesImpl(Frame owner) {
        Plugin plugin = getPlugin();
        BuildOutputWindow window = plugin.getDockableWindow();
        visibleCB.setSelected(window.isActive());
        locationCombo.setSelectedIndex(window.getPosition());

        highlightInputCB.setSelected(plugin.getSyntaxHighlightInput());
        cbStdout.setSelected(window.isStyleUsed(BuildOutputTextArea.STYLE_STDOUT));
        stdoutButton.setEnabled(window.isStyleUsed(BuildOutputTextArea.STYLE_STDOUT));
        cbStderr.setSelected(window.isStyleUsed(BuildOutputTextArea.STYLE_STDERR));
        stderrButton.setEnabled(window.isStyleUsed(BuildOutputTextArea.STYLE_STDERR));
        cbPrompt.setSelected(window.isStyleUsed(BuildOutputTextArea.STYLE_PROMPT));
        promptButton.setEnabled(window.isStyleUsed(BuildOutputTextArea.STYLE_PROMPT));
        cbExceptions.setSelected(window.isStyleUsed(BuildOutputTextArea.STYLE_EXCEPTION));
        exceptionsButton.setEnabled(window.isStyleUsed(BuildOutputTextArea.STYLE_EXCEPTION));
        cbBackground.setSelected(window.isStyleBackgroundUsed(BuildOutputTextArea.STYLE_BACKGROUND));
        backgroundButton.setEnabled(window.isStyleBackgroundUsed(BuildOutputTextArea.STYLE_BACKGROUND));
        cbFiles.setSelected(window.isStyleUsed(BuildOutputTextArea.STYLE_FILE));
        filesButton.setEnabled(window.isStyleUsed(BuildOutputTextArea.STYLE_FILE));
        cbPositions.setSelected(window.isStyleUsed(BuildOutputTextArea.STYLE_FILE_POS));
        positionsButton.setEnabled(window.isStyleUsed(BuildOutputTextArea.STYLE_FILE_POS));
        cbWarnings.setSelected(window.isStyleUsed(BuildOutputTextArea.STYLE_WARNINGS));
        warningsButton.setEnabled(window.isStyleUsed(BuildOutputTextArea.STYLE_WARNINGS));
        cbErrors.setSelected(window.isStyleUsed(BuildOutputTextArea.STYLE_ERRORS));
        errorsButton.setEnabled(window.isStyleUsed(BuildOutputTextArea.STYLE_ERRORS));

        stdoutButton.setColor(window.getForeground(BuildOutputTextArea.STYLE_STDOUT));
        stderrButton.setColor(window.getForeground(BuildOutputTextArea.STYLE_STDERR));
        promptButton.setColor(window.getForeground(BuildOutputTextArea.STYLE_PROMPT));
        exceptionsButton.setColor(window.getForeground(BuildOutputTextArea.STYLE_EXCEPTION));
        backgroundButton.setColor(window.getBackground(BuildOutputTextArea.STYLE_BACKGROUND));
        filesButton.setColor(window.getForeground(BuildOutputTextArea.STYLE_FILE));
        positionsButton.setColor(window.getForeground(BuildOutputTextArea.STYLE_FILE_POS));
        warningsButton.setColor(window.getForeground(BuildOutputTextArea.STYLE_WARNINGS));
        errorsButton.setColor(window.getForeground(BuildOutputTextArea.STYLE_ERRORS));
    }


}
