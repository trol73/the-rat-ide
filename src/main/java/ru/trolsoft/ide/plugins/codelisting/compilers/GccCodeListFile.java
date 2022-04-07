package ru.trolsoft.ide.plugins.codelisting.compilers;

import org.fife.rtext.plugins.project.model.Project;
import org.fife.rtext.plugins.project.model.ProjectType;

import java.io.*;

public class GccCodeListFile extends CodeListFile {

    public GccCodeListFile(Project project) {
        super(project);
    }

    @Override
    public File getListFile() {
        String mainFilePath = project.getMainFile();
        if (mainFilePath.isEmpty()) {
            return null;
        }
        File mainFile = new File(mainFilePath);
        if (!mainFile.exists()) {
            return null;
        }
        if (project.getType() == ProjectType.BUILDER) {
            String outputFileName = getOutputFileNameFromMakeBuilder(mainFilePath);
            if (outputFileName == null) {
                return null;
            }
            String path = mainFile.getParent() + "/build/" + outputFileName + ".lss";
            File f = new File(path);
            if (f.exists()) {
                return f;
            }
        }
        return null;
    }


    private String getOutputFileNameFromMakeBuilder(String path) {
        try (var reader = new BufferedReader(new FileReader(path))) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    return null;
                }
                line = line.trim();
                if (line.startsWith("name")) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String projectName = parts[1].trim();
                        if ((projectName.startsWith("'") && projectName.endsWith("'")) | (projectName.startsWith("\"") && projectName.endsWith("\""))) {
                            return projectName.substring(1, projectName.length()-1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
