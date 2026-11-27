package org.fife.rtext.plugins.project;

import org.fife.rtext.RText;
import org.fife.rtext.plugins.project.model.*;
import org.fife.rtext.plugins.project.tree.*;
import org.fife.ui.rtextfilechooser.RDirectoryChooser;
import org.fife.ui.rtextfilechooser.RTextFileChooser;
import org.fife.ui.utils.UIUtil;
import ru.trolsoft.ide.therat.AvrRatDevicesUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;

public class NewProjectDialog extends AbstractEnterFileNameDialog implements DocumentListener {

    private enum Template {
        NONE("None") {
            @Override
            public BuildTemplate build(String name, File root, String device, String mainFile) {
                return new BuildTemplate(root);
            }
        },
        AVR_RAT("AVR Rat") {
            @Override
            public BuildTemplate build(String name, File root, String device, String mainFile) {
                String defineCpu = !device.isBlank() && !device.startsWith("<") ? "#define CPU \"" + device + "\"\n" : "";
                return new BuildTemplate(root)
                        .withType(ProjectType.RAT)
                        .withFolders("src")
                        .withFile("src/" + mainFile + ".art", defineCpu +
                                """
                                #define F_CPU   16000000
                                
                                proc main() {
                                    SPH = r16 = high(RAMEND) ; set stack pointer
                                    SPL = r16 = low(RAMEND)
                                }
                                """);
            }
        },
        AVR_GCC("AVR GCC (C/Rat)") {
            @Override
            public BuildTemplate build(String name, File root, String device, String mainFile) {
                return new BuildTemplate(root)
                        .withType(ProjectType.BUILDER)
                        .withFolders("src")
                        .withFile("make.builder", """
                            name = '%NAME%'
                            
                            src = [
                               'src/*.c',
                            ]
                            
                            mcu = '%MCU%'
                            
                            frequency = 16*1000000
                            
                            defines = [
                               'F_CPU=16000000',
                               'DEBUG=0',
                            ]
                            
                            compiler_options = ['-g2  -Os']
                            
                            linker_options = []
                            
                            configurations = {}
                            """
                                .replaceAll("%NAME%", name)
                                .replaceAll("%MCU%", device.equals("<none>") ? "" : device))
                        .withFile("src/" + mainFile + ".c", """
                            #include <stddef.h>
                            #include <stdlib.h>
                            #include <stdbool.h>
                            #include <stdint.h>
                            #include <stdio.h>
                            #include <string.h>
                            
                            #include <avr/io.h>
                            #include <avr/interrupt.h>
                            #include <avr/pgmspace.h>
                            #include <avr/sleep.h>
                            #include <avr/eeprom.h>
                            #include <avr/wdt.h>
                            
                            #include <util/delay.h>
                            
                            void main() {
                            }
                            """);
            }
        },
        C_APPLICATION("C Application") {
            @Override
            public BuildTemplate build(String name, File root, String device, String mainFile) {
                return new BuildTemplate(root)
                        .withType(ProjectType.BUILDER)
                        .withFolders("src")
                        .withFile("make.builder",
                                """
                                name = '%NAME%'
                                                                
                                src = [
                                	'src/*.c',
                                ]

                                defines = [
                                ]

                                compiler = 'gcc'
                                #compiler = 'gcc-w64'
                                #compiler = 'gcc-i686'

                                compiler_options = ['-g2']

                                linker_options = []
             
                                libs = ''
                                """.replaceAll("%NAME%", name))
                        .withFile("src/" + mainFile + ".c", """
                                #include <stdio.h>
                                
                                int main(int argc, char **argv) {
                                    printf("Hello, World!");
                                }
                                """);
            }
        },
        RAT_8080("Rat 8080") {
            @Override
            public BuildTemplate build(String name, File root, String device, String mainFile) {
                return new BuildTemplate(root)
                        .withType(ProjectType.I8085_RAT)
                        .withFolders("src")
                        .withFile("src/" + mainFile + ".8080",
                                """                               
                                        proc main() {
                                            SP = 0x75FF
                                            nop
                                        }
                                        """);
            }
        },
        RAT_VIPER("Rat Viper") {
            @Override
            public BuildTemplate build(String name, File root, String device, String mainFile) {
                return new BuildTemplate(root)
                        .withType(ProjectType.VIPER_RAT)
                        .withFolders("src")
                        .withFile("src/" + mainFile + ".viper",
                                """
                                        #include "syscalls.viperh"
                                        
                                        proc main() {
                                            HALT
                                        }
                                        """)
                        .withFile("src/syscalls.viperh" ,
                        """
                                const KEY_UP    = 0x01
                                const KEY_DOWN  = 0x02
                                const KEY_LEFT  = 0x04
                                const KEY_RIGHT = 0x08
                                const KEY_ENTER = 0x10
                                
                                inline proc uartRead() -> (b: r0) {
                                    syscall 0x00
                                }
                                
                                inline proc uartTryRead() -> (b: r0) {
                                    syscall 0x01
                                }
                                
                                inline proc uartWrite(b: r0) {
                                    syscall 0x02
                                }
                                
                                inline proc setColor(color: RX) {
                                    syscall 0x03
                                }
                                
                                inline proc setBackground(color: RX) {
                                    syscall 0x04
                                }
                                
                                inline proc drawRect(x: r0, y: r1, width: r2, height: r3) {
                                    syscall 0x05
                                }
                                
                                inline proc fillRect(x: r0, y: r1, width: r2, height: r3) {
                                    syscall 0x06
                                }
                                
                                inline proc drawCircle(x: r0, y: r1, r: r2) {
                                    syscall 0x07
                                }
                                
                                inline proc fillCircle(x: r0, y: r1, r: r2) {
                                    syscall 0x08
                                }
                                
                                inline proc drawChar(x: r0, y: r1, ch: r2) {
                                    syscall 0x09
                                }
                                
                                inline proc fillChar(x: r0, y: r1, ch: r2) {
                                    syscall 0x0A
                                }
                                
                                inline proc drawString(x: r0, y: r1, s: RX) {
                                    syscall 0x0B
                                }
                                
                                inline proc fillString(x: r0, y: r1, s: RX) {
                                    syscall 0x0C
                                }
                                
                                inline proc keyRead() -> (key: r0) {
                                    syscall 0x0D
                                }

                                inline proc keyWait() -> (key: r0) {
                                    syscall 0x0E
                                }
                                """);
            }
        };

//        CPP_APPLICATION("C++ Application") {
//            @Override
//            public BuildTemplate build(String name, File root, String device, String mainFile) {
//                return null;
//            }
//        };

        private final String name;

        Template(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public abstract BuildTemplate build(
                String name,
                File root,
                String device,
                String mainFile
        );
    }

    private static class BuildTemplate {
        final File root;
        final List<File> folders = new ArrayList<>();
        final Map<File, String> files = new HashMap<>();

        ProjectType type;
        File mainFile;

        BuildTemplate(File root) {
            this.root = root;
        }

        BuildTemplate withFolders(String ...folders) {
            for (String f : folders) {
                this.folders.add(local(f));
            }
            return this;
        }

        BuildTemplate withFile(String name, String content) {
            if (mainFile == null) {
                mainFile = local(name);
            }
            files.put(local(name), content);
            return this;
        }

        BuildTemplate withType(ProjectType type) {
            this.type = type;
            return this;
        }

        private File local(String name) {
            return new File(root.getAbsolutePath() + File.separator + name);
        }
    }

    private final Workspace workspace;
    private final RText rtext;

    private final WorkspaceRootTreeNode workspaceRootNode;

    private JComboBox<Template> cbType;
    private JTextField edtLocation;

    private JComboBox<String> cbDevice;
    private JComboBox<String> cbDefaultEncoding;
    private JTextField edtMainFile;

    /**
     * Constructor.
     *
     * @param owner     The rtext window that owns this dialog.
     * @param checker   The validator for the entered file name.
     */
    public NewProjectDialog(RText owner, NameChecker checker, Workspace workspace, WorkspaceRootTreeNode workspaceRootNode) {
        super(owner, true, checker);
        this.workspace = workspace;
        this.rtext = owner;
        this.workspaceRootNode = workspaceRootNode;

        Icon icon = owner.getIconGroup().getIcon("application");
        setDescription(icon, Messages.getString("NewProjectDialog.Desc"));
        setTitle(Messages.getString("NewProjectDialog.Title"));
        setFileName(null); // Move focus from desc SelectableLabel to field.

        edtLocation.setText(determineDefaultLocation());
    }

    @Override
    protected Container createExtraContent() {
        JPanel panel = new JPanel(new SpringLayout());

        JLabel lblType = new JLabel("From template");
        cbType = new JComboBox<>(Template.values());
        cbType.addActionListener(this);
        lblType.setLabelFor(cbType);
        panel.add(lblType);
        panel.add(cbType);
        panel.add(Box.createHorizontalBox());

        JLabel lblLocation = new JLabel("Parent directory");
        edtLocation = new JTextField(40);
        edtLocation.getDocument().addDocumentListener(this);
        lblLocation.setLabelFor(edtLocation);
        JButton dirBrowseButton = new JButton("Browse");
        dirBrowseButton.setActionCommand("Browse");
        dirBrowseButton.addActionListener(this);

        panel.add(lblLocation);
        panel.add(edtLocation);
        panel.add(dirBrowseButton);

        JLabel lblDevice = new JLabel("Device");
        cbDevice = new JComboBox<>();
        cbDevice.addActionListener(this);
        cbDevice.addItem("<none>");
        for (String s : AvrRatDevicesUtils.getAllDevices()) {
            cbDevice.addItem(s);
        }
        cbDevice.setActionCommand("Device");
        cbDevice.addActionListener(this);
        lblDevice.setLabelFor(cbDevice);
        panel.add(lblDevice);
        panel.add(cbDevice);
        panel.add(Box.createHorizontalBox());

        JLabel lblEncoding = new JLabel("Default encoding");
        cbDefaultEncoding = createEncodingComboBox();
        lblEncoding.setLabelFor(cbDefaultEncoding);
        panel.add(lblEncoding);
        panel.add(cbDefaultEncoding);
        panel.add(Box.createHorizontalBox());

        JLabel lblMainFile = new JLabel("Main file name");
        edtMainFile = new JTextField();
        lblMainFile.setLabelFor(edtMainFile);
        panel.add(lblMainFile);
        panel.add(edtMainFile);
        panel.add(Box.createHorizontalBox());

        UIUtil.makeSpringCompactGrid(panel, 5, 3, 5, 5, 5, 5);
        setComponentsEnabled();
        return panel;
    }

    private String determineDefaultLocation() {
        var mainFiles = new ArrayList<File>();
        workspace.getProjectIterator().forEachRemaining(prj -> {
               if (prj != null && prj.getMainFile() != null && !prj.getMainFile().isBlank()) mainFiles.add(new File(prj.getMainFile()));
            }
        );
        String defaultPath = System.getProperty("user.home");
        if (mainFiles.isEmpty()) {
            return defaultPath;
        }

        for (int i = 0; i < mainFiles.size(); i++) {
            File f = mainFiles.get(i);
            while (!f.isDirectory()) {
                f = f.getParentFile();
            }
            mainFiles.set(i, f);
        }
        File root = mainFiles.get(0);

        for (int i = 1; i < mainFiles.size(); i++) {
            File f = mainFiles.get(i);
            while (!f.getAbsolutePath().startsWith(root.getAbsolutePath()) && root.getAbsolutePath().length() > defaultPath.length()) {
                root = root.getParentFile();
            }
        }
        return root.getAbsolutePath().length() > defaultPath.length() ? root.getAbsolutePath() : defaultPath;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("OK".equals(command)) {
            Template type = getTemplateType();
            String name = nameField.getText();
            File root = new File(edtLocation.getText() + File.separator + name);
            String device = getDevice();
            String mainFile = edtMainFile.getText();
            if (mainFile.isBlank()) {
                mainFile = "main";
            }
            BuildTemplate template = type.build(name, root, device, mainFile);
            setVisible(false);
            createProject(template);
        } else if (e.getSource().equals(cbType)) {
            setComponentsEnabled();
            if (getTemplateType() != Template.NONE) {
                edtMainFile.setText(nameField.getText());
            } else {
                edtMainFile.setText("");
            }
        } else if ("Browse".equals(command)) {
            RDirectoryChooser dc = new RDirectoryChooser(this, null, false);
            String dirName = edtLocation.getText().trim();
            if (!dirName.isEmpty()) {
                File dir = new File(dirName);
                dc.setChosenDirectory(dir);
            }
            dc.setVisible(true);
            String chosenDir = dc.getChosenDirectory();
            if (chosenDir != null) {
                File dir = new File(chosenDir);
                if (dir.isDirectory() && !chosenDir.equals(edtLocation.getText())) {
                    edtLocation.setText(chosenDir);
                }
            }
        }
    }

    private void createProject(BuildTemplate template) {
        String name = getFileName();
        if (name == null) {
            return;
        }
        Project project = new Project(workspace, name);

        project.setType(template.type);
        project.setDevice(getDevice());
        project.setEncoding(Objects.requireNonNull(cbDefaultEncoding.getSelectedItem()).toString());
        if (template.mainFile != null) {
            project.setMainFile(Objects.requireNonNull(template.mainFile.getAbsolutePath()));
        }

        ProjectPlugin plugin = rtext.getProjectPlugin();
        workspace.addProject(project);
        ProjectTreeNode projectNode = new ProjectTreeNode(plugin, project);
        plugin.insertTreeNodeInto(projectNode, workspaceRootNode);

        // Ensure Workspace root node is expanded when first plugin is added.
        plugin.getTree().expandPath(new TreePath(workspaceRootNode.getPath()));

        for (File dir : template.folders) {
            dir.mkdirs();
            FolderProjectEntry entry = new FolderProjectEntry(project, dir);
            project.addEntry(entry);
            FolderProjectEntryTreeNode childNode = new FolderProjectEntryTreeNode(plugin, entry);
            //childNode.setFilterInfo(chooser.getFilterInfo());
            plugin.insertTreeNodeInto(childNode, projectNode);
        }
        for (File f : template.files.keySet()) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)))) {
                writer.write(template.files.get(f));
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!template.folders.contains(f.getParentFile())) {
                ProjectEntry entry = new FileProjectEntry(project, f);
                project.addEntry(entry);
                FileProjectEntryTreeNode childNode = new FileProjectEntryTreeNode(plugin, entry);
                plugin.insertTreeNodeInto(childNode, projectNode);
            }
            rtext.openFile(f);
        }
    }

    private void setComponentsEnabled() {
        Template template = getTemplateType();
        boolean isNone = template == Template.NONE;
        edtLocation.setEnabled(!isNone);
        cbDevice.setEnabled(!isNone && template != Template.C_APPLICATION);
        edtMainFile.setEnabled(!isNone);
    }

    private Template getTemplateType() {
        return Template.values()[cbType.getSelectedIndex()];
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

    private void projectNameChanged() {
        if (getTemplateType() != Template.NONE) {
            edtMainFile.setText(nameField.getText());
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        projectNameChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        projectNameChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        projectNameChanged();
    }

    private String getDevice() {
        Object devo = cbDevice.getSelectedItem();
        if (devo == null) {
            return "";
        }
        String dev = devo.toString();
        return dev.equalsIgnoreCase("<none>") ? "" : dev;
    }


    @Override
    protected void addDescPanel() {

    }
}
