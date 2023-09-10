package ru.trolsoft.ide.gcc;

import org.fife.rtext.RText;
import org.fife.rtext.RTextEditorPane;
import org.fife.rtext.plugins.buildoutput.BuildOutputWindow;
import org.fife.rtext.plugins.project.model.Project;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import ru.trolsoft.ide.builder.MakeBuilderProject;
import ru.trolsoft.ide.utils.ProjectUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class AvrGccMapUtils {
    static class FunctionRec {
        String name;
        int offset;
        int size;

        FunctionRec(String name, int offset, int size) {
            this.name = name;
            this.offset = offset;
            this.size = size;
        }

        @Override
        public String toString() {
            return name + ": " + Integer.toString(offset, 16) + " " + size;
        }
    }

    public static List<FunctionRec> loadMapForObjectFile(String mapFile, String objFile) {
        if (objFile == null) {
            return null;
        }
        var result = new ArrayList<FunctionRec>();
        try (
            FileReader fr = new FileReader(mapFile);
            BufferedReader reader = new BufferedReader(fr)
        ) {
            String name = null;
            boolean isCurrentObjFileBlock = false;
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.isEmpty()) {
                    name = null;
                    continue;
                }
                if (line.startsWith(".text")) {
                    isCurrentObjFileBlock = line.endsWith(objFile);
                }
                if (line.startsWith(".text.")) {
                    name = line.substring(".text.".length()).trim();
                    continue;
                }
                if (name == null && !isCurrentObjFileBlock) {
                    continue;
                }
                if (line.startsWith("0x00") && line.endsWith(objFile)) {
                    var st = new StringTokenizer(line, " ", false);
                    String offsetStr = st.nextToken();
                    String lengthStr = st.nextToken();
                    result.add(new FunctionRec(
                            name,
                            Integer.parseInt(offsetStr.substring(2), 16),
                            Integer.parseInt(lengthStr.substring(2), 16)
                    ));
                } else if (line.startsWith("0x00") && isCurrentObjFileBlock) {
                    var st = new StringTokenizer(line, " ", false);
                    String offsetStr = st.nextToken();
                    String procName = st.nextToken();
                    result.add(new FunctionRec(
                            procName,
                            Integer.parseInt(offsetStr.substring(2), 16),
                            -1
                    ));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    static int findOffsetInAvrGccListing(String lstFile, int offset) {
        String offsetStr = Integer.toString(offset, 16);
        while (offsetStr.length() < 8) {
            offsetStr = '0' + offsetStr;
        }
        offsetStr += " <";
        try (
                FileReader fr = new FileReader(lstFile);
                BufferedReader reader = new BufferedReader(fr)
        ) {
            int num = 0;
            while (true) {
                num++;
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith(offsetStr) && line.endsWith(">:")) {
                    return num;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }


    public static void printMapForSource(RTextEditorPane editor, RText rtext) {
        List<FunctionRec> mapData = getMapDataForSourceFile(editor, rtext);
        if (mapData == null) {
            return;
        }
        BuildOutputWindow window = rtext.getBuildOutputWindow();
        window.clearConsoles();
        int sum = 0;
        for (var itm : mapData) {
            String s = itm.name;
            while (s.length() < 30) {
                s += ' ';
            }
            s += " " + itm.size + "\n";
            sum += itm.size;
            window.prompt(s);
        }
        window.prompt("-----------\n");
        window.prompt("TOTAL                          " + sum);
    }

    private static List<FunctionRec> getMapDataForSourceFile(RTextEditorPane editor, RText rtext) {
        Project project = ProjectUtils.getProjectForCurrentFile(rtext);
        if (project == null || editor == null) {
            return null;
        }
        String currentFilePath = editor.getFileFullPath();
        String outFile = MakeBuilderProject.getOutputFilePathFor(project, currentFilePath);
        String mapFile = MakeBuilderProject.getMapFilePath(project);
        return loadMapForObjectFile(mapFile, outFile);
    }

    public static void showFunctionSize(RTextEditorPane editor, RText rtext) {
        List<FunctionRec> mapData = getMapDataForSourceFile(editor, rtext);
        if (mapData == null) {
            return;
        }
        String text = getLineStr(editor, editor.getLine());
        if (text == null || text.isBlank()) {
            return;
        }
        text = text.replace("\t", " ").replace("  ", " ").replace(" (", "(");
        for (var itm : mapData) {
            if (text.contains(itm.name + "(")) {
                BuildOutputWindow window = rtext.getBuildOutputWindow();
                window.clearConsoles();
                window.prompt(itm.name + " size is " + itm.size);
            }
        }
    }

    public static void showGccListing(RTextEditorPane editor, RText rtext) {
        List<FunctionRec> mapData = getMapDataForSourceFile(editor, rtext);
        if (mapData == null || mapData.isEmpty()) {
            return;
        }
        String text = getLineStr(editor, editor.getLine());
        if (text == null || text.isBlank()) {
            return;
        }

        text = text.replace("\t", " ").replace("  ", " ").replace(" (", "(");
        for (var itm : mapData) {
            if (text.contains(itm.name + "(")) {
                Project project = ProjectUtils.getProjectForCurrentFile(rtext);
                String mapFile = MakeBuilderProject.getMapFilePath(project);
                String lssFile = mapFile.substring(0, mapFile.length()-3) + "lss";
                int lineNum = findOffsetInAvrGccListing(lssFile, itm.offset);
                rtext.openFile(lssFile, lineNum, 1);
//System.err.println(itm.offset + " -> " + lineNum);
//                BuildOutputWindow window = rtext.getBuildOutputWindow();
//                window.clearConsoles();
//                window.prompt(itm.name + " size is " + itm.size);
            }
        }
    }

    private static String getLineStr(RSyntaxTextArea textArea, int line) {
        try {
            int posStart = textArea.getLineStartOffset(line - 1);
            int posEnd = textArea.getLineEndOffset(line - 1);
            int len = posEnd - posStart;
            if (len > 2048) {
                return null;
            }
            return textArea.getDocument().getText(posStart, len);
        } catch (Exception ignore) {
            return null;
        }
    }
}
