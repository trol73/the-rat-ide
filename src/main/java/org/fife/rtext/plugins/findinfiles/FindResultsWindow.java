package org.fife.rtext.plugins.findinfiles;

import org.fife.rtext.RText;
import org.fife.ui.dockablewindows.DockableWindow;
import org.fife.ui.dockablewindows.DockableWindowConstants;
import org.fife.ui.search.FindInFilesTable;
import org.fife.ui.utils.UIUtil;
import org.fife.ui.widgets.RScrollPane;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FindResultsWindow extends DockableWindow implements PropertyChangeListener {
    private final FindInFilesTable results;

    public FindResultsWindow(RText app, Plugin plugin) {
        setDockableWindowName(plugin.getString("DockableWindow.Title"));
        setIcon(plugin.getPluginIcon());
        setPosition(DockableWindowConstants.BOTTOM);
        setLayout(new BorderLayout());

        CardLayout cards = new CardLayout();
        JPanel mainPanel = new JPanel(cards);
        add(mainPanel);

        results = new FindInFilesTable();
        setPrimaryComponent(results);
        add(results);

        RScrollPane sp = new RScrollPane(results);
        UIUtil.removeTabbedPaneFocusTraversalKeyBindings(sp);
        mainPanel.add(sp, "System");
        setActive(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public FindInFilesTable getSearchResults() {
        return results;
    }

}
