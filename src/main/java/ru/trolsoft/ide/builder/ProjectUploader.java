package ru.trolsoft.ide.builder;

import org.fife.rtext.RText;
import org.fife.rtext.plugins.buildoutput.BuildOutputWindow;
import org.fife.rtext.plugins.project.model.Project;
import org.fife.ui.utils.OS;
import ru.trolsoft.ide.utils.StringUtils;

import java.io.File;

public class ProjectUploader {
    private final RText rtext;
    private final Project project;
    private final BuildOutputWindow window;

    public ProjectUploader(RText rtext, Project project, BuildOutputWindow window) {
        this.rtext = rtext;
        this.project = project;
        this.window = window;
    }

    public void upload() {
        switch (project.getType()) {
            case AVR_RAT -> uploadAvrRatProject();
            case BUILDER -> uploadMakeBuilderProject();
            case MAKEFILE -> uploadMakeFileProject();
        }        
    }

    private void uploadAvrRatProject() {
        String path = project.getMainFile();
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        String hexPath = StringUtils.removeFileExt(path) + ".hex";
        String cmd = buildUploadCommand(hexPath);
        window.prompt("Upload " + hexPath + "\n");
        window.prompt(cmd + "\n");
        window.execute(cmd, file.getParentFile());
    }

    private void uploadMakeBuilderProject() {
        String path = project.getMainFile();
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        String mainFileName = MakeBuilderProject.getName(file);
        if (mainFileName == null) {
            window.prompt("Undefined firmware/executabe file");
            return;
        }
        String buildFiePath = file.getParent() + File.separator + "build" + File.separator + mainFileName;
        File hexFileInBuild = new File(buildFiePath + ".hex");
        if (hexFileInBuild.exists() && hexFileInBuild.isFile()) {
            String cmd = buildUploadCommand(hexFileInBuild.getAbsolutePath());
            window.prompt("Upload " + hexFileInBuild.getAbsolutePath()  + "\n");
            window.prompt(cmd + "\n");
            window.execute(cmd, hexFileInBuild.getParentFile());
            return;
        }
        File executableFile = new File(file.getParent() + File.separator + mainFileName);
        if (executableFile.isFile() && executableFile.exists()) {
            executeFile(executableFile);
        }
    }

    private void executeFile(File executableFile) {
        if (OS.get() == OS.MAC_OS_X) {
            window.prompt("Execute " + executableFile.getAbsolutePath()  + "\n");
            String command = "cd " + executableFile.getParentFile().getAbsolutePath() + " && " + executableFile.getAbsolutePath();
            String cmd = "osascript -e 'tell app \"Terminal\" \nactivate\ndo script \"%CMD%\" \nend tell'"
                    .replaceAll("%CMD%", command);
            window.execute(cmd, executableFile.getParentFile());
        } else {
            // TODO
        }
    }

    private void uploadMakeFileProject() {
    }

    private String buildUploadCommand(String hexPath) {
        return "avrdude -c usbasp -p " + project.getDevice() + " -U \"flash:w:" + hexPath + ":a\"";
    }

}
