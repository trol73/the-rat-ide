package org.fife.rtext.actions;

import org.fife.rtext.RText;
import org.fife.ui.app.AppAction;
import org.fife.ui.utils.UIUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

public class AvrGccPageAction extends AppAction<RText> {
    AvrGccPageAction(RText owner, ResourceBundle msg, Icon icon) {
        super(owner, msg, "AvrGccPageAction");
        setIcon(icon);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (!UIUtil.browse("https://trolsoft.ru/articles/avr-c-and-asm")) {
            RText app = getApplication();
            UIManager.getLookAndFeel().provideErrorFeedback(app);
        }
    }

}
