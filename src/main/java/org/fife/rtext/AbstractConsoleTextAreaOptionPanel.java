/*
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext;

import org.fife.ui.RColorSwatchesButton;
import org.fife.ui.utils.UIUtil;
import org.fife.ui.app.Plugin;
import org.fife.ui.app.PluginOptionsDialogPanel;
import org.fife.ui.app.console.AbstractConsoleTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;


/**
 * A base class for option panels for plugins that contain a "console" that
 * renders stdout, stderr, etc.  This base class provides the infrastructure
 * to allow the user to customize the styles used in the console.
 *
 * @param <P> The type of plugin this option panel is for.
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class AbstractConsoleTextAreaOptionPanel<P extends Plugin<?>> extends PluginOptionsDialogPanel<P>
        implements ActionListener, ItemListener, PropertyChangeListener {

    protected JCheckBox visibleCB;
    protected JLabel locationLabel;
    protected JComboBox<String> locationCombo;

    protected JCheckBox cbStdout;
    protected JCheckBox cbStderr;
    protected JCheckBox cbPrompt;
    protected JCheckBox cbExceptions;
    protected JCheckBox cbBackground;
    protected RColorSwatchesButton stdoutButton;
    protected RColorSwatchesButton stderrButton;
    protected RColorSwatchesButton promptButton;
    protected RColorSwatchesButton exceptionsButton;
    protected RColorSwatchesButton backgroundButton;

    protected JButton defaultsButton;

    private static final ResourceBundle MSG = ResourceBundle.getBundle("org.fife.rtext.RText");


    /**
     * Constructor.
     *
     * @param plugin The plugin.
     */
    public AbstractConsoleTextAreaOptionPanel(P plugin) {
        super(plugin);
    }


    /**
     * Called when the user toggles various properties in this panel.
     *
     * @param e The event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (visibleCB == source) {
            setVisibleCBSelected(visibleCB.isSelected());
            setDirty(true);
        } else if (cbBackground == source) {
            boolean selected = cbBackground.isSelected();
            backgroundButton.setEnabled(selected);
            setDirty(true);
        } else if (cbExceptions == source) {
            boolean selected = cbExceptions.isSelected();
            exceptionsButton.setEnabled(selected);
            setDirty(true);
        } else if (cbPrompt == source) {
            boolean selected = cbPrompt.isSelected();
            promptButton.setEnabled(selected);
            setDirty(true);
        } else if (cbStderr == source) {
            boolean selected = cbStderr.isSelected();
            stderrButton.setEnabled(selected);
            setDirty(true);
        } else if (cbStdout == source) {
            boolean selected = cbStdout.isSelected();
            stdoutButton.setEnabled(selected);
            setDirty(true);
        } else if (defaultsButton == source) {
            if (notDefaults()) {
                restoreDefaults();
                setDirty(true);
            }
        }
    }


    /**
     * Provides a hook for subclasses to add content in the "Colors" section,
     * before the color buttons for stdout, stderr, etc.  The default
     * implementation does nothing; subclasses can override.
     *
     * @param parent The container to add the content in.
     */
    protected void addExtraColorRelatedContent(Box parent) {
        // Do nothing (comment for Sonar)
    }


    /**
     * Adds the "Restore Defaults" button to the UI.
     *
     * @param parent The container to add it to.
     */
    protected void addRestoreDefaultsButton(Container parent) {
        defaultsButton = new JButton(getString("RestoreDefaults"));
        defaultsButton.setActionCommand("RestoreDefaults");
        defaultsButton.addActionListener(this);
        addLeftAligned(parent, defaultsButton);
    }


    /**
     * Returns a checkbox used to toggle whether a color in a console uses
     * a special color.
     *
     * @param label The label for the checkbox.
     * @return The checkbox.
     */
    protected JCheckBox createColorActivateCB(String label) {
        JCheckBox cb = new JCheckBox(label);
        cb.addActionListener(this);
        return cb;
    }


    /**
     * Creates a color picker button we're listening for changes on.
     *
     * @return The button.
     */
    protected RColorSwatchesButton createColorSwatchesButton() {
        RColorSwatchesButton button = new RColorSwatchesButton();
        button.addPropertyChangeListener(RColorSwatchesButton.COLOR_CHANGED_PROPERTY, this);
        return button;
    }


    /**
     * Creates the "Colors" section of options for this plugin.
     *
     * @return A panel with the "color" options.
     */
    protected Container createColorsPanel() {
        Box temp = Box.createVerticalBox();

        temp.setBorder(new OptionPanelBorder(getString("Options.Colors")));

        addExtraColorRelatedContent(temp);

        cbStdout = createColorActivateCB(getString("Color.Stdout"));
        stdoutButton = createColorSwatchesButton();
        cbStderr = createColorActivateCB(getString("Color.Stderr"));
        stderrButton = createColorSwatchesButton();
        cbPrompt = createColorActivateCB(getString("Color.Prompts"));
        promptButton = createColorSwatchesButton();
        cbExceptions = createColorActivateCB(getString("Color.Exceptions"));
        exceptionsButton = createColorSwatchesButton();
        cbBackground = createColorActivateCB(getString("Color.Background"));
        backgroundButton = createColorSwatchesButton();

        JPanel sp = new JPanel(new SpringLayout());
        if (getComponentOrientation().isLeftToRight()) {
            sp.add(cbStdout);
            sp.add(stdoutButton);
            sp.add(cbStderr);
            sp.add(stderrButton);
            sp.add(cbPrompt);
            sp.add(promptButton);
            sp.add(cbExceptions);
            sp.add(exceptionsButton);
            sp.add(cbBackground);
            sp.add(backgroundButton);
        } else {
            sp.add(stdoutButton);
            sp.add(cbStdout);
            sp.add(stderrButton);
            sp.add(cbStderr);
            sp.add(promptButton);
            sp.add(cbPrompt);
            sp.add(exceptionsButton);
            sp.add(cbExceptions);
            sp.add(cbBackground);
            sp.add(backgroundButton);
        }
        int extraRows = createExtraColorPickers(sp);
        UIUtil.makeSpringCompactGrid(sp, 5 + extraRows, 2, 0, 0, 5, 5);

        JPanel temp2 = new JPanel(new BorderLayout());
        temp2.add(sp, BorderLayout.LINE_START);
        temp.add(temp2);
        temp.add(Box.createVerticalGlue());

        return temp;
    }

    protected int createExtraColorPickers(JPanel panel) {
        return 0;
    }


    /**
     * Creates the "General" section of options for this plugin.
     *
     * @return A panel with the "general" options.
     */
    protected Container createGeneralPanel() {
        ResourceBundle gpb = ResourceBundle.getBundle("org.fife.ui.app.GUIPlugin");

        Box temp = Box.createVerticalBox();
        temp.setBorder(new OptionPanelBorder(gpb.getString("Options.General")));

        // A check box toggling the plugin's visibility.
        visibleCB = new JCheckBox(gpb.getString("Visible"));
        visibleCB.addActionListener(this);
        JPanel temp2 = new JPanel(new BorderLayout());
        temp2.add(visibleCB, BorderLayout.LINE_START);
        temp.add(temp2);
        temp.add(Box.createVerticalStrut(5));

        // A combo in which to select the dockable window's placement.
        Box locationPanel = createHorizontalBox();
        locationCombo = new JComboBox<>();
        UIUtil.fixComboOrientation(locationCombo);
        locationCombo.addItem(gpb.getString("Location.top"));
        locationCombo.addItem(gpb.getString("Location.left"));
        locationCombo.addItem(gpb.getString("Location.bottom"));
        locationCombo.addItem(gpb.getString("Location.right"));
        locationCombo.addItem(gpb.getString("Location.floating"));
        locationCombo.addItemListener(this);
        locationLabel = new JLabel(gpb.getString("Location.title"));
        locationLabel.setLabelFor(locationCombo);
        locationPanel.add(locationLabel);
        locationPanel.add(Box.createHorizontalStrut(5));
        locationPanel.add(locationCombo);
        locationPanel.add(Box.createHorizontalGlue());
        temp.add(locationPanel);

        temp.add(Box.createVerticalGlue());
        return temp;

    }


    private String getString(String key) {
        return MSG.getString(key);
    }


    @Override
    public JComponent getTopJComponent() {
        return visibleCB;
    }


    /**
     * Called when the user changes the desired location of the dockable
     * window.
     *
     * @param e The event.
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == locationCombo && e.getStateChange() == ItemEvent.SELECTED) {
            setDirty(true);
        }
    }


    /**
     * Returns whether something on this panel is NOT set to its default value.
     *
     * @return Whether some property in this panel is NOT set to its default
     * value.
     */
    protected boolean notDefaults() {
        boolean isDark = RTextUtilities.isDarkLookAndFeel();
        Color defaultStdout = isDark ? AbstractConsoleTextArea.DEFAULT_DARK_STDOUT_FG :
                AbstractConsoleTextArea.DEFAULT_LIGHT_STDOUT_FG;
        Color defaultStderr = isDark ? AbstractConsoleTextArea.DEFAULT_DARK_STDERR_FG :
                AbstractConsoleTextArea.DEFAULT_LIGHT_STDERR_FG;
        Color defaultPrompt = isDark ? AbstractConsoleTextArea.DEFAULT_DARK_PROMPT_FG :
                AbstractConsoleTextArea.DEFAULT_LIGHT_PROMPT_FG;
        Color defaultException = isDark ? AbstractConsoleTextArea.DEFAULT_DARK_EXCEPTION_FG :
                AbstractConsoleTextArea.DEFAULT_LIGHT_EXCEPTION_FG;
        Color defaultBackground = isDark ? AbstractConsoleTextArea.DEFAULT_DARK_BACKGROUND :
                AbstractConsoleTextArea.DEFAULT_LIGHT_BACKGROUND;


        return !visibleCB.isSelected() ||
                locationCombo.getSelectedIndex() != 2 ||
                !cbStdout.isSelected() ||
                !cbStderr.isSelected() ||
                !cbPrompt.isSelected() ||
                !cbExceptions.isSelected() ||
                !cbBackground.isSelected() ||
                !defaultStdout.equals(stdoutButton.getColor()) ||
                !defaultStderr.equals(stderrButton.getColor()) ||
                !defaultPrompt.equals(promptButton.getColor()) ||
                !defaultException.equals(exceptionsButton.getColor()) ||
                !defaultBackground.equals(backgroundButton.getColor());
    }


    /**
     * Called when one of our color picker buttons is modified.
     *
     * @param e The event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        setDirty(true);
    }


    /**
     * Restores all properties on this panel to their default values.
     */
    protected void restoreDefaults() {
        setVisibleCBSelected(true);
        locationCombo.setSelectedIndex(2);
        restoreDefaultsForColorsPanel();
    }


    /**
     * Restores defaults values for all widgets in the "Colors" panel.
     */
    protected void restoreDefaultsForColorsPanel() {
        cbStdout.setSelected(true);
        cbStderr.setSelected(true);
        cbPrompt.setSelected(true);
        cbExceptions.setSelected(true);
        cbBackground.setSelected(true);
        stdoutButton.setEnabled(true);
        stderrButton.setEnabled(true);
        promptButton.setEnabled(true);
        exceptionsButton.setEnabled(true);
        backgroundButton.setEnabled(true);

        boolean isDark = RTextUtilities.isDarkLookAndFeel();
        if (isDark) {
            stdoutButton.setColor(AbstractConsoleTextArea.DEFAULT_DARK_STDOUT_FG);
            stderrButton.setColor(AbstractConsoleTextArea.DEFAULT_DARK_STDERR_FG);
            promptButton.setColor(AbstractConsoleTextArea.DEFAULT_DARK_PROMPT_FG);
            exceptionsButton.setColor(AbstractConsoleTextArea.DEFAULT_DARK_EXCEPTION_FG);
            backgroundButton.setColor(AbstractConsoleTextArea.DEFAULT_DARK_BACKGROUND);
        } else {
            stdoutButton.setColor(AbstractConsoleTextArea.DEFAULT_LIGHT_STDOUT_FG);
            stderrButton.setColor(AbstractConsoleTextArea.DEFAULT_LIGHT_STDERR_FG);
            promptButton.setColor(AbstractConsoleTextArea.DEFAULT_LIGHT_PROMPT_FG);
            exceptionsButton.setColor(AbstractConsoleTextArea.DEFAULT_LIGHT_EXCEPTION_FG);
            backgroundButton.setColor(AbstractConsoleTextArea.DEFAULT_LIGHT_BACKGROUND);
        }
    }


    protected void setVisibleCBSelected(boolean selected) {
        visibleCB.setSelected(selected);
        locationLabel.setEnabled(selected);
        locationCombo.setEnabled(selected);
    }


}
