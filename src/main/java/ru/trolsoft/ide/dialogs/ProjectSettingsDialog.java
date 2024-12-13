package ru.trolsoft.ide.dialogs;

import org.fife.rtext.RText;
import org.fife.rtext.plugins.project.RenameDialog;
import org.fife.rtext.plugins.project.model.Project;
import org.fife.rtext.plugins.project.model.ProjectType;
import org.fife.rtext.plugins.project.tree.checkers.ProjectNameChecker;
import org.fife.ui.rtextfilechooser.RTextFileChooser;
import org.fife.ui.utils.UIUtil;
import org.fife.ui.widgets.FSATextField;
import ru.trolsoft.ide.therat.AvrRatDevicesUtils;
import ru.trolsoft.ide.utils.ProjectUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class ProjectSettingsDialog extends RenameDialog implements ActionListener {

    private static final ResourceBundle MSG = ResourceBundle.getBundle("ru.trolsoft.ide.ProjectSettingsDialog");

    private final Project project;
    private final boolean createNew;
    private JComboBox<String> cbType;
    private JComboBox<String> cbDevice;
    private JComboBox<String> cbEncoding;
    private FSATextField edtMainFile;
    private JLabel lblMainFile;
    private JButton btnBrowseMainFile;
    private RTextFileChooser chooser;


    public ProjectSettingsDialog(RText owner, Project project, boolean createNew) {
        super(owner, false, "Project", new ProjectNameChecker(project.getWorkspace()));
        this.project = project;
        this.createNew = createNew;
        setTitle(MSG.getString("Dialog.ProjectSettings.Title"));
        nameField.setText(project.getName());
        nameField.setEditable(createNew);
        if (!createNew) {
            setGoodNameValue();
        }
        setup();
        //super(owner, MSG.getString("Dialog.ProjectSettings.Title"));
        //createUI(owner);
    }

//    public ProjectSettingsDialog(Frame parent) {
//        super(parent, MSG.getString("Dialog.KeyStroke.Title"));
//        createUI();
//    }


    @Override
    protected Container createExtraContent() {
        cbType = new JComboBox<>(new String[] {"The Rat", "Builder", "Makefile", "Custom"});
        cbType.setActionCommand("Type");
        cbType.addActionListener(this);

        cbDevice = new JComboBox<>();
        cbDevice.addItem("<none>");
        for (String s : AvrRatDevicesUtils.getAllDevices()) {
            cbDevice.addItem(s);
        }
        cbDevice.setActionCommand("Device");
        cbDevice.addActionListener(this);

        UIUtil.fixComboOrientation(cbType);
        UIUtil.fixComboOrientation(cbDevice);

        cbEncoding = createEncodingComboBox();

        edtMainFile = new FSATextField();
        lblMainFile = new JLabel("Main file");
        btnBrowseMainFile = new JButton("Browse");
        btnBrowseMainFile.setActionCommand("BrowseMainFile");
        btnBrowseMainFile.addActionListener(this);

        Dimension dim = new Dimension(1, 1); // MUST have finite width!
        JPanel panel = new JPanel(new SpringLayout());
        panel.add(new JLabel("Type"));
        panel.add(cbType);
        panel.add(Box.createRigidArea(dim));

        panel.add(new JLabel("Device"));
        panel.add(cbDevice);
        panel.add(Box.createRigidArea(dim));

        panel.add(new JLabel("Default encoding"));
        panel.add(cbEncoding);
        panel.add(Box.createRigidArea(dim));

        panel.add(lblMainFile);
        panel.add(edtMainFile);
        panel.add(btnBrowseMainFile);

        org.fife.rsta.ui.UIUtil.makeSpringCompactGrid(panel,
                4, 3,  // rows, cols
                0, 10,     // initX, initY
                6, 6);     // xPad, yPad
        return panel;
    }

    private void setup() {
        try {
            cbType.setSelectedIndex(project.getType() == null ? 0 : project.getType().ordinal());
        } catch (IllegalArgumentException ignore) {}
        cbDevice.setSelectedItem(project.getDevice());
        cbEncoding.setSelectedItem(project.getEncoding());
        edtMainFile.setText(project.getMainFile());
    }

    private void save() {
        project.setType(getSelectedType());
        project.setDevice(Objects.requireNonNull(cbDevice.getSelectedItem()).toString());
        project.setEncoding(Objects.requireNonNull(cbEncoding.getSelectedItem()).toString());
        project.setMainFile(Objects.requireNonNull(edtMainFile.getText()));
        try {
            project.getWorkspace().save();
        } catch (IOException e) {
            ((RText)getOwner()).displayException(e);
            e.printStackTrace();
        }
    }

    private ProjectType getSelectedType() {
        return ProjectType.values()[cbType.getSelectedIndex()];
    }

    private JComboBox<String> createEncodingComboBox() {
        JComboBox<String> cbEncoding = new JComboBox<>();
        UIUtil.fixComboOrientation(cbEncoding);
        // Populate the combo box with all available encodings.
        Map<String, Charset> encodings = Charset.availableCharsets();
        for (String key : encodings.keySet()) {
            cbEncoding.addItem(key);
        }
        cbEncoding.setActionCommand("Encoding");
        cbEncoding.addActionListener(this);
        String defaultEncName = RTextFileChooser.getDefaultEncoding();
        String defaultEnc = Charset.forName(defaultEncName).name();
        cbEncoding.setSelectedItem(defaultEnc);
        return cbEncoding;
    }


    private void setVisibleMainFile(boolean visible) {
        edtMainFile.setVisible(visible);
        lblMainFile.setVisible(visible);
        btnBrowseMainFile.setVisible(visible);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("OK".equals(command)) {
//            canceled = false;
            save();
//            setVisible(false);
//        } else if ("Cancel".equals(command)) {
//            setVisible(false);
        } else if ("Type".equals(command)) {
            ProjectType type = getSelectedType();
            setVisibleMainFile(type != ProjectType.CUSTOM);
        } else if ("Device".equals(command)) {
        } else if ("Encoding".equals(command)) {
        } else if ("BrowseMainFile".equals(command)) {
            browseMainFile();
        }
        super.actionPerformed(e);
    }

    private void browseMainFile() {
        if (chooser == null) {
            chooser = new RTextFileChooser(false);
            ProjectUtils.projectGetFirstFolder(project).ifPresent((f) -> chooser.setCurrentDirectory(f));
        }
        int rc = chooser.showOpenDialog(this);
        if (rc == RTextFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            edtMainFile.setFileSystemAware(false);
            edtMainFile.setText(file.getAbsolutePath());
            edtMainFile.setFileSystemAware(true);
        }
    }
}
