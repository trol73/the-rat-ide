package org.fife.rtext.actions;

import org.fife.rtext.RText;
import org.fife.ui.app.AppAction;
import org.fife.ui.utils.UIUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

public class AvrRatPageAction extends AppAction<RText> {
    AvrRatPageAction(RText owner, ResourceBundle msg, Icon icon) {
        super(owner, msg, "AvrRatPageAction");
        setIcon(icon);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (!UIUtil.browse("https://trolsoft.ru/soft/the-rat-avr")) {
            RText app = getApplication();
            UIManager.getLookAndFeel().provideErrorFeedback(app);
        }
    }
}
