package org.fife.rtext.plugins.project;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.widgets.DecorativeIconPanel;
import org.fife.rtext.RText;
import org.fife.rtext.RTextUtilities;
import org.fife.rtext.plugins.project.tree.NameChecker;
import org.fife.ui.EscapableDialog;
import org.fife.ui.ResizableFrameContentPane;
import org.fife.ui.widgets.SelectableLabel;
import org.fife.ui.utils.UIUtil;


public abstract class AbstractEnterFileNameDialog extends EscapableDialog implements ActionListener {
    private boolean descAdded;
    private JPanel topPanel;
    private JLabel nameLabel;
    private JButton okButton;
    protected JTextField nameField;
    private DecorativeIconPanel renameDIP;
    private final NameChecker nameChecker;
    final boolean isForFile;

//    private static Icon errorIcon;

    /**
     * Constructor.
     *
     * @param owner     The rtext window that owns this dialog.
     * @param directory Whether this dialog is for a directory as opposed to a regular file.
     * @param checker   The validator for the entered file name.
     */
    protected AbstractEnterFileNameDialog(RText owner, boolean directory, NameChecker checker) {
        super(owner);
        this.nameChecker = checker;
        this.isForFile = !directory;
        construct();
    }


    /**
     * Creates (the majority of) the UI.
     */
    private void construct() {
        RText owner = (RText) getOwner();
        Listener listener = new Listener();
        ComponentOrientation orientation = ComponentOrientation.getOrientation(getLocale());
        ResourceBundle bundle = owner.getResourceBundle();

        JPanel cp = new ResizableFrameContentPane(new BorderLayout());
        cp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(cp);

        // A panel containing the main content.
        String key = "FileName.Field.Label";
        nameLabel = new JLabel(Messages.getString(key));
        nameLabel.setDisplayedMnemonic(Messages.getString(key + ".Mnemonic").charAt(0));
        nameField = new JTextField(40);
        nameField.getDocument().addDocumentListener(listener);
        nameLabel.setLabelFor(nameField);
        renameDIP = new DecorativeIconPanel();
        Box box = new Box(BoxLayout.LINE_AXIS);
        box.add(nameLabel);
        box.add(Box.createHorizontalStrut(5));
        box.add(RTextUtilities.createAssistancePanel(nameField, renameDIP));
        box.add(Box.createHorizontalGlue());
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(box, BorderLayout.NORTH);
        Container extraContent = createExtraContent();
        if (extraContent != null) {
            mainContentPanel.add(extraContent, BorderLayout.SOUTH);
        }
        topPanel = new JPanel(new BorderLayout());
        topPanel.add(mainContentPanel, BorderLayout.SOUTH);

        // Make a panel containing the OK and Cancel buttons.
        okButton = UIUtil.newButton(bundle, "OKButtonLabel", "OKButtonMnemonic");
        okButton.setActionCommand("OK");
        okButton.addActionListener(this);
        JButton cancelButton = UIUtil.newButton(bundle, "Cancel", "CancelMnemonic");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);

        // Put everything into a neat little package.
        cp.add(topPanel, BorderLayout.NORTH);
        Container buttons = UIUtil.createButtonFooter(okButton, cancelButton);
        cp.add(buttons, BorderLayout.SOUTH);
        JRootPane rootPane = getRootPane();
        rootPane.setDefaultButton(okButton);
        setTitle(getTitle());
        setModal(true);
        applyComponentOrientation(orientation);
        packSpecial();
        setLocationRelativeTo(owner);
    }


    /**
     * Creates and adds a panel containing description information for the
     * file being named in this dialog.
     */
    protected abstract void addDescPanel();


    /**
     * Creates extra content to be displayed after the file/folder name text
     * field.  The default implementation returns <code>null</code>, which
     * specifies no content.  Subclasses can override.
     *
     * @return The extra content.
     */
    protected Container createExtraContent() {
        return null;
    }


    /**
     * Overridden to set the name "the user entered" to <code>null</code>.
     */
    @Override
    public void escapePressed() {
        nameField.setText(null); // So user gets back nothing
        super.escapePressed();
    }


    /**
     * Returns the icon to use for fields with errors.
     *
     * @param rtext The parent application.
     * @return The icon.
     */
    public static Icon getErrorIcon(RText rtext) {
        // The IconGroup caches this value, so we just always fetch it so we can pick up changes in themes/icon groups
        return rtext.getIconGroup().getIcon("error_annotation", 12, 12);
    }


    /**
     * Returns a localized error message to use in this dialog.
     *
     * @param key The (part of the) key to display.
     * @return The localized error message.
     */
    private static String getLocalizedReason(String key) {
        return Messages.getString("FileName.InvalidName." + key);
    }


    /**
     * Returns the name selected.
     *
     * @return The name selected, or <code>null</code> if the dialog was
     * cancelled.
     * @see #setFileName(String)
     */
    public String getFileName() {
        String name = nameField.getText();
        return !name.isEmpty() ? name : null;
    }


    /**
     * Packs this dialog, taking special care to not be too wide due to our
     * <code>SelectableLabel</code>.
     */
    private void packSpecial() {
        pack();
        setSize(520, getHeight() + 60); // Enough for line wrapping
    }


    private void setBadNameValue(String reason) {
        renameDIP.setShowIcon(true);
        renameDIP.setIcon(getErrorIcon((RText) getParent()));
        renameDIP.setToolTipText(getLocalizedReason(reason));
        okButton.setEnabled(false);
    }


    /**
     * Adds descriptive text and an icon to the top of the dialog.
     *
     * @param icon The icon to add.
     * @param desc The descriptive text to add.
     */
    public void setDescription(Icon icon, String desc) {
        SelectableLabel descText = new SelectableLabel(desc);
        JLabel label = new JLabel(icon);
        if (getComponentOrientation().isLeftToRight()) {
            label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));
        } else {
            label.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));
        }
        JPanel temp = new JPanel(new BorderLayout());
        temp.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        temp.add(label, BorderLayout.LINE_START);
        temp.add(descText);
        topPanel.add(temp, BorderLayout.NORTH);
        packSpecial();
    }


    protected void setGoodNameValue() {
        renameDIP.setShowIcon(false);
        renameDIP.setToolTipText(null);
        okButton.setEnabled(true);
    }


    /**
     * Sets the name displayed in this dialog.
     *
     * @param name The name to display.
     * @see #getFileName()
     */
    public void setFileName(String name) {
        nameField.setText(name);
        nameField.requestFocusInWindow();
        nameField.selectAll();
        if (name == null) {
            okButton.setEnabled(false);
        }
    }


    /**
     * Sets the label text for the name field.
     *
     * @param label The new label text.
     */
    public void setNameLabel(String label) {
        nameLabel.setText(label);
    }


    @Override
    public void setVisible(boolean visible) {
        if (visible && !descAdded) {
            addDescPanel();
            descAdded = true;
        }
        super.setVisible(visible);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("OK".equals(command)) {
            setVisible(false);
        } else if ("Cancel".equals(command)) {
            escapePressed();
        }
    }


    /**
     * Listens for events in this dialog.
     */
    private class Listener implements DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

        private void handleDocumentEvent() {

            if (nameField.getDocument().getLength() == 0) {
                setBadNameValue("empty");
                return;
            }

            String text = nameField.getText();
            String error = nameChecker.isValid(text);
            if (error != null) {
                setBadNameValue(error);
                return;
            }

            setGoodNameValue();

        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            handleDocumentEvent();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            handleDocumentEvent();
        }

    }


}
