package org.fife.rtext.plugins.findinfiles;

import org.fife.rtext.RText;
import org.fife.ui.app.GUIPlugin;
import org.fife.ui.app.PluginOptionsDialogPanel;
import org.fife.ui.app.icons.IconGroup;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Plugin extends GUIPlugin<RText> {
    private static final String MSG_BUNDLE = "org.fife.rtext.plugins.findfiles.Plugin";
    static final ResourceBundle MSG = ResourceBundle.getBundle(MSG_BUNDLE);
    private static final String DOCKABLE_WINDOW_CODE_SEARCH = "codeSearchDockableWindow";
    private final FindResultsWindow window;
    /**
     * Constructor.
     *
     * @param app The parent application.
     */
    public Plugin(RText app) {
        super(app);

        window = new FindResultsWindow(app, this);
        putDockableWindow(DOCKABLE_WINDOW_CODE_SEARCH, window);
    }

    /**
     * Returns the dockable window containing the consoles.
     *
     * @return The dockable window.
     */
    public FindResultsWindow getDockableWindow() {
        return window;
    }


    /**
     * Returns a localized message.
     *
     * @param key    The key.
     * @param params Any parameters for the message.
     * @return The localized message.
     */
    public String getString(String key, String... params) {
        String temp = MSG.getString(key);
        return MessageFormat.format(temp, (Object[]) params);
    }


    @Override
    public PluginOptionsDialogPanel<? extends org.fife.ui.app.Plugin<RText>> getOptionsDialogPanel() {
        return null;
    }

    @Override
    public String getPluginAuthor() {
        return "";
    }

    @Override
    public Icon getPluginIcon() {
        return null;
    }

    @Override
    public String getPluginName() {
        return "";
    }

    @Override
    public String getPluginVersion() {
        return "";
    }

    @Override
    public void install() {

    }

    @Override
    public void savePreferences() {

    }

    @Override
    public boolean uninstall() {
        return false;
    }

    @Override
    public void updateIconsForNewIconGroup(IconGroup iconGroup) {

    }
}
