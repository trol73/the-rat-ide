/*
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;


/**
 * A dialog that closes itself when the Escape key is pressed.  Subclasses
 * can extend the {@link #escapePressed()} method and provide custom handling
 * logic (parameter validation, custom closing, etc.).
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class EscapableDialog extends JDialog {

    /**
     * The key in an <code>InputMap</code> for the Escape key action.
     */
    private static final String ESCAPE_KEY = "OnEsc";


    /**
     * Constructor.
     */
    public EscapableDialog() {
        init();
    }


    /**
     * Constructor.
     *
     * @param owner The parent window.
     */
    public EscapableDialog(Window owner) {
        super(owner);
        init();
    }


    /**
     * Constructor.
     *
     * @param owner The parent window.
     * @param modal Whether this dialog should be modal.
     */
    public EscapableDialog(Window owner, boolean modal) {
        super(owner, modal ? Dialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        init();
    }


    /**
     * Constructor.
     *
     * @param owner The parent window.
     * @param title The title of the dialog.
     */
    public EscapableDialog(Window owner, String title) {
        super(owner, title);
        init();
    }


    /**
     * Constructor.
     *
     * @param owner The parent window.
     * @param title The title of the dialog.
     * @param modal Whether this dialog should be modal.
     */
    public EscapableDialog(Window owner, String title, boolean modal) {
        super(owner, title, modal ? Dialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        init();
    }


    /**
     * Called when the Escape key is pressed in this dialog.  Subclasses
     * can override to handle any custom "Cancel" logic.  The default
     * implementation hides the dialog (via <code>setVisible(false);</code>).
     */
    protected void escapePressed() {
        setVisible(false);
    }


    /**
     * Initializes this dialog.
     */
    private void init() {
        setEscapeClosesDialog(true);
    }


    /**
     * Toggles whether the Escape key closes this dialog.
     *
     * @param closes Whether Escape should close this dialog (actually,
     *               whether {@link #escapePressed()} should be called when Escape
     *               is pressed).
     */
    public void setEscapeClosesDialog(boolean closes) {
        JRootPane rootPane = getRootPane();
        InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        if (closes) {
            im.put(ks, ESCAPE_KEY);
            actionMap.put(ESCAPE_KEY, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    escapePressed();
                }
            });
        } else {
            im.remove(ks);
            actionMap.remove(ESCAPE_KEY);
        }

    }


}