package ru.trolsoft.ide.plugins.codelisting;

import org.fife.rtext.RText;
import org.fife.rtext.RTextMenuBar;
import org.fife.rtext.RTextUtilities;
import org.fife.ui.app.AppAction;
import org.fife.ui.app.GUIPlugin;
import org.fife.ui.app.Plugin;
import org.fife.ui.app.PluginOptionsDialogPanel;
import org.fife.ui.app.icons.IconGroup;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class CodeListingPlugin extends GUIPlugin<RText> {
    private static final String VERSION = "1.0.0";
    private static final String MSG_BUNDLE = "ru.trolsoft.ide.plugins.codelisting.CodeListingPlugin";
    private static final String CODE_LISTING_WINDOW = "CodeListingWindow";
    private static final String VIEW_CODE_LISTING_ACTION = "viewCodeListingAction";

    private static final ResourceBundle MSG = ResourceBundle.getBundle(MSG_BUNDLE);

    private final Icon icon;
    private final CodeListingWindow window;


    public CodeListingPlugin(RText app) {
        super(app);
        icon = new ImageIcon(getClass().getResource("icon.png"));
        CodeListingPrefs prefs = loadPrefs();

        AppAction<RText> a = new ViewCodeListingAction(app, MSG, this);
        a.setAccelerator(prefs.windowVisibilityAccelerator);
        app.addAction(VIEW_CODE_LISTING_ACTION, a);

        window = createWindow(app, prefs);
    }


    private CodeListingPrefs loadPrefs() {
        var prefs = new CodeListingPrefs();
        File prefsFile = getPrefsFile();
        if (prefsFile.isFile()) {
            try {
                prefs.load(prefsFile);
            } catch (IOException ioe) {
                getApplication().displayException(ioe);
                // (Some) defaults will be used
            }
        }
        return prefs;
    }

    private static File getPrefsFile() {
        return new File(RTextUtilities.getPreferencesDirectory(), "codeListing.properties");
    }

    private CodeListingWindow createWindow(RText app, CodeListingPrefs prefs) {
        var window = new CodeListingWindow(app, this);
        window.setPosition(prefs.windowPosition);
        window.setActive(prefs.windowVisible);
        putDockableWindow(CODE_LISTING_WINDOW, window);

//        window.setForeground(BuildOutputTextArea.STYLE_EXCEPTION, prefs.exceptionFG);
//        window.setBackground(BuildOutputTextArea.STYLE_BACKGROUND, prefs.background);

        return window;
    }

    public CodeListingWindow getDockableWindow() {
        return window;
    }

    public String getString(String key, String... params) {
        String temp = MSG.getString(key);
        return MessageFormat.format(temp, (Object[]) params);
    }

    @Override
    public PluginOptionsDialogPanel<? extends Plugin<RText>> getOptionsDialogPanel() {
        return null;
    }

    @Override
    public String getPluginAuthor() {
        return "TrolSoft";
    }

    @Override
    public Icon getPluginIcon() {
        return icon;
    }

    @Override
    public String getPluginName() {
        return MSG.getString("Plugin.Name");
    }

    @Override
    public String getPluginVersion() {
        return VERSION;
    }

    @Override
    public void install() {
        RText app = getApplication();
        RTextMenuBar mb = (RTextMenuBar) app.getJMenuBar();

        addMenuAction(app, mb);

        getApplication().addDockableWindow(window);
    }

    private void addMenuAction(RText app, RTextMenuBar mb) {
        // Add an item to the "View" menu to toggle window visibility
        final JMenu menu = mb.getMenuByName(RTextMenuBar.MENU_DOCKED_WINDOWS);
        Action a = app.getAction(VIEW_CODE_LISTING_ACTION);
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(a);
        item.setSelected(isCodeListingWindowVisible());
        item.setToolTipText(null);
        item.applyComponentOrientation(app.getComponentOrientation());
        menu.add(item);
    }

    @Override
    public void savePreferences() {
        var prefs = new CodeListingPrefs();
        prefs.windowPosition = window.getPosition();
        AppAction<?> a = (AppAction<?>) getApplication().getAction(VIEW_CODE_LISTING_ACTION);
        prefs.windowVisibilityAccelerator = a.getAccelerator();
        prefs.windowVisible = window.isActive();

        File prefsFile = getPrefsFile();
        try {
            prefs.save(prefsFile);
        } catch (IOException ioe) {
            getApplication().displayException(ioe);
        }
    }

    @Override
    public boolean uninstall() {
        return true;
    }

    @Override
    public void updateIconsForNewIconGroup(IconGroup iconGroup) {

    }

    public boolean isCodeListingWindowVisible() {
        return window != null && window.isActive();
    }


    public void setCodeListingWindowVisible(boolean visible) {
        if (visible != isCodeListingWindowVisible()) {
//            if (visible && window == null) {
//                //window = new ListingViewerWindow(getApplication(), this);
//                //getApplication().addDockableWindow(window);
//            }
            window.setActive(visible);
        }
    }
}
