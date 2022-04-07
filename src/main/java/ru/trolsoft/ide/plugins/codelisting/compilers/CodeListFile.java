package ru.trolsoft.ide.plugins.codelisting.compilers;

import org.fife.rtext.plugins.project.model.Project;

import java.io.File;

public abstract class CodeListFile {
    protected final Project project;

    protected CodeListFile(Project project) {
        this.project = project;
    }

    abstract public File getListFile();
}
