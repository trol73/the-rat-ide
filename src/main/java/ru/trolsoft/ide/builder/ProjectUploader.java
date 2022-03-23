package ru.trolsoft.ide.builder;

import org.fife.rtext.RText;
import org.fife.rtext.plugins.buildoutput.BuildOutputWindow;
import org.fife.rtext.plugins.project.model.Project;
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
    }

    private void uploadMakeFileProject() {
    }

    private String buildUploadCommand(String hexPath) {
        // avrdude -s -c usbasp -p $CHIP -U "flash:w:build/ic-tester-main.hex:a"
        return "avrdude -s -c usbasp -p " + project.getDevice() + " -U \"flash:w:" + hexPath + ":a\"";
    }
}
