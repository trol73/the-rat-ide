package ru.trolsoft.ide.plugins.codelisting;

import org.fife.rtext.RText;
import org.fife.ui.app.AppAction;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

public class ViewCodeListingAction extends AppAction<RText>  {

    private final CodeListingPlugin plugin;

    public ViewCodeListingAction(RText owner, ResourceBundle msg, CodeListingPlugin plugin) {
        super(owner, msg, "Action.ShowCodeListing");
        this.plugin = plugin;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        plugin.setCodeListingWindowVisible(!plugin.isCodeListingWindowVisible());
    }
}
