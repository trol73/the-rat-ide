package ru.trolsoft.ide.builder;

import org.fife.rtext.plugins.project.model.Project;
import org.fife.rtext.plugins.project.model.ProjectType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MakeBuilderProject {
    static String getName(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("name") && line.contains("=") && (line.contains("'") || line.contains("\""))) {
                    line = line.substring("name".length()).trim();
                    if (line.startsWith("=")) {
                        int index = line.indexOf('#');
                        if (index > 0) {
                            line = line.substring(1, index).trim();
                        } else {
                            line = line.substring(1).trim();
                        }
                        if ((line.startsWith("'") && line.endsWith("'")) || (line.startsWith("\"") && line.endsWith("\""))) {
                            return line.substring(1, line.length()-1);
                        }
                    }
                }
                line = reader.readLine();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static String getOutputFilePathFor(Project project, String srcPath) {
        if (project.getType() != ProjectType.BUILDER) {
            return null;
        }
        String rootPath = new File(project.getMainFile()).getParent();
        String filePath = srcPath.substring(rootPath.length());
        if (srcPath.startsWith(rootPath) && isSourceFile(srcPath)) {
            if (filePath.startsWith("/src/")) {
                filePath = rootPath + "/build" + filePath.substring(4);
                int lastDot = filePath.lastIndexOf('.');
                if (lastDot >= 0) {
                    filePath = filePath.substring(0, lastDot) + ".o";
                }
            }
            return filePath;
        }
        return null;
    }

    private static boolean isSourceFile(String path) {
        return path.endsWith(".c") || path.endsWith(".art");
    }

    public static String getMapFilePath(Project project) {
        if (project.getType() != ProjectType.BUILDER) {
            return null;
        }
        String projectName = getName(new File(project.getMainFile()));
        if (projectName == null) {
            return null;
        }
        return new File(project.getMainFile()).getParent() + "/build/" + projectName + ".map";
    }
}
