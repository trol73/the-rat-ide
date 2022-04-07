package ru.trolsoft.ide.plugins.codelisting;

import org.fife.ui.app.prefs.Prefs;
import org.fife.ui.dockablewindows.DockableWindow;
import org.fife.ui.dockablewindows.DockableWindowConstants;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

public class CodeListingPrefs extends Prefs {
    public boolean windowVisible;
    public int windowPosition;
    public KeyStroke windowVisibilityAccelerator;

    @Override
    public void setDefaults() {
        windowVisible = false;
        windowPosition = DockableWindowConstants.BOTTOM;
        windowVisibilityAccelerator = null;
    }

    @Override
    public void load(InputStream in) throws IOException {
        super.load(in);

        // Ensure window position is valid.
        if (!DockableWindow.isValidPosition(windowPosition)) {
            windowPosition = DockableWindowConstants.BOTTOM;
        }
    }

}
