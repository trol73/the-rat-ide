package ru.trolsoft.ide.builder;

import org.fife.rtext.RText;
import org.fife.rtext.plugins.buildoutput.BuildOutputWindow;
import org.fife.rtext.plugins.buildoutput.BuildTask;
import org.fife.rtext.plugins.project.model.Project;
import ru.trolsoft.ide.therat.AvrRatDevicesUtils;
import ru.trolsoft.therat.RatKt;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ProjectBuilder {
    private final RText rtext;
    private final Project project;
    private final BuildOutputWindow window;

    public ProjectBuilder(RText rtext, Project project, BuildOutputWindow window) {
        this.rtext = rtext;
        this.project = project;
        this.window = window;
    }

    public void build(String activeFilePath) {
        switch (project.getType()) {
            case AVR_RAT -> buildAvrRatProject();
            case BUILDER -> buildMakeBuilderProject();
            case MAKEFILE -> buildMakeFileProject();
        }
//        window.execute("cat " + activeFilePath);
    }

    private void buildAvrRatProject() {
        window.clearConsoles();
        window.execute(new BuildTask() {
            @Override
            public void run() {
                String path = project.getMainFile();
                window.prompt("Compile " + path + "\n");
                String devPath = AvrRatDevicesUtils.getFolder().getAbsolutePath();
                List<String> defines = new ArrayList<>();
                PrintStream out = getOutStream();
                PrintStream err = getErrStream();
                RatKt.compileProject(path, devPath, defines, out, err);
                window.prompt("Done.\n");
            }
        });
    }

    private void buildMakeFileProject() {
        window.clearConsoles();
        String path = project.getMainFile();
        File pwd = new File(path).getParentFile();
        window.prompt("Compile " + project.getName() + "\n");
        window.execute("make -f " + project.getMainFile(), pwd);
    }

    private void buildMakeBuilderProject() {
        String path = project.getMainFile();
        File pwd = new File(path).getParentFile();
        window.prompt("Compile " + project.getName() + "\n");
        window.execute("builder", pwd);
    }

}
