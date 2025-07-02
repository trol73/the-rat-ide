package ru.trolsoft.ide.dialogs;

import com.jidesoft.hints.ListDataIntelliHints;
import org.fife.rtext.RText;
import org.fife.rtext.RTextUtilities;
import org.fife.rtext.plugins.project.model.ProjectEntry;
import org.fife.ui.EscapableDialog;
import org.fife.ui.ResizableFrameContentPane;
import org.fife.ui.utils.UIUtil;
import org.fife.ui.widgets.DecorativeIconPanel;
import org.fife.ui.widgets.FSATextField;
import ru.trolsoft.ide.utils.ProjectUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class GoToProjectFileDialog extends EscapableDialog implements ActionListener {
    private static final ResourceBundle MSG = ResourceBundle.getBundle("ru.trolsoft.ide.dialogs.GoToProjectFileDialog");

    private final RText app;
    private JButton btnOk;
    protected JTextField edtFile;

    private final List<String> fileHints = new ArrayList<>();
    private final Map<String, File> projectFiles = new HashMap<>();
    private final List<String> projectFolders = new ArrayList<>();

    public GoToProjectFileDialog(RText owner) {
        super(owner);
        app = owner;
        construct();
    }

    private void construct() {
        RText owner = (RText) getOwner();
        ComponentOrientation orientation = ComponentOrientation.getOrientation(getLocale());
        ResourceBundle bundle = owner.getResourceBundle();
        setTitle(MSG.getString("GoToProjectFileDialog.Title"));

        JPanel cp = new ResizableFrameContentPane(new BorderLayout());
        cp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(cp);


        // A panel containing the main content.
        String key = "FileName.Field.Label";
        JLabel lblName = new JLabel(MSG.getString(key));
        lblName.setDisplayedMnemonic(MSG.getString(key + ".Mnemonic").charAt(0));
        edtFile = new FSATextField(40);
        new ListDataIntelliHints<>(edtFile, fileHints) {
            @Override
            public void acceptHint(Object selected) {
                super.acceptHint(selected);
                openFile();
            }
        }.setCaseSensitive(false);
        var project = ProjectUtils.getProjectForCurrentFile(owner);
        if (project != null) {
            var it = project.getEntryIterator();
            while (it.hasNext()) {
                var entry = it.next();
                if (ProjectEntry.DIR_PROJECT_ENTRY.equals(entry.getType())) {
                    projectFolders.add(entry.getFile().getParentFile().getAbsolutePath() + File.separatorChar);
                    addFilesRecursive(entry.getFile());
                } else if (ProjectEntry.FILE_PROJECT_ENTRY.equals(entry.getType())) {
                    addFilesRecursive(entry.getFile());
                }
            }
        }

        lblName.setLabelFor(edtFile);
        DecorativeIconPanel filesDIP = new DecorativeIconPanel();
        Box box = new Box(BoxLayout.LINE_AXIS);
        box.add(lblName);
        box.add(Box.createHorizontalStrut(5));
        box.add(RTextUtilities.createAssistancePanel(edtFile, filesDIP));
        box.add(Box.createHorizontalGlue());
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(box, BorderLayout.NORTH);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(mainContentPanel, BorderLayout.SOUTH);

        // Make a panel containing the OK and Cancel buttons.
        btnOk = UIUtil.newButton(bundle, "OKButtonLabel", "OKButtonMnemonic");
        btnOk.setActionCommand("OK");
        btnOk.addActionListener(this);
        JButton cancelButton = UIUtil.newButton(bundle, "Cancel", "CancelMnemonic");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);

        // Put everything into a neat little package.
        cp.add(pnlTop, BorderLayout.NORTH);
        Container buttons = UIUtil.createButtonFooter(btnOk, cancelButton);
        cp.add(buttons, BorderLayout.SOUTH);
        JRootPane rootPane = getRootPane();
        rootPane.setDefaultButton(btnOk);
        setTitle(getTitle());
        setModal(true);
        applyComponentOrientation(orientation);
        packSpecial();
        setLocationRelativeTo(owner);
    }

    private void addFilesRecursive(File from) {
        if (from == null) {
            return;
        }
        if (from.isFile()) {
            addFileHint(from);
        } else {
            var list = from.listFiles();
            if (list != null) {
                for (var f : list) {
                    addFilesRecursive(f);
                }
            }
        }
    }

    private void addFileHint(File file) {
        var name = file.getName();
        if (fileHints.contains(name)) {
            var oldFile = projectFiles.get(name);
            fileHints.remove(name);
            projectFiles.remove(name);

            var oldName = name + " (" + getProjectFileLocation(oldFile) + ")";
            fileHints.add(oldName);
            projectFiles.put(oldName, oldFile);

            name += " (" + getProjectFileLocation(file) + ")";
        }
        fileHints.add(name);
        projectFiles.put(name, file);
    }

    private String getProjectFileLocation(File file) {
        var result = file.getAbsolutePath();
        for (String dir : projectFolders) {
            if (result.startsWith(dir)) {
                return result.substring(dir.length());
            }
        }
        return result;
    }

    /**
     * Packs this dialog, taking special care to not be too wide due to our <code>SelectableLabel</code>.
     */
    private void packSpecial() {
        pack();
        setSize(520, getHeight() + 60); // Enough for line wrapping
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnOk) {
            openFile();
        }
    }

    private void openFile() {
        var name = edtFile.getText();
        var file = projectFiles.get(name);
        if (name != null && file.exists()) {
            app.openFile(file);
            setVisible(false);
        }
    }

}
