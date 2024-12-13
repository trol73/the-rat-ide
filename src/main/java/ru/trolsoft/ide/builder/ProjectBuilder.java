package ru.trolsoft.ide.builder;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.fife.rtext.RText;
import org.fife.rtext.plugins.buildoutput.BuildOutputWindow;
import org.fife.rtext.plugins.buildoutput.BuildTask;
import org.fife.rtext.plugins.project.model.Project;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.jetbrains.annotations.NotNull;
import ru.trolsoft.compiler.generator.ListingRec;
import ru.trolsoft.ide.plugins.codelisting.CodeListingWindow;
import ru.trolsoft.ide.therat.AvrRatDevicesUtils;
import ru.trolsoft.ide.utils.StringUtils;
import ru.trolsoft.therat.RatKt;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ProjectBuilder {
    private final RText rtext;
    private final Project project;
    private final BuildOutputWindow window;

    private Component focusOwner;

//    public interface RatListingHandler {
//        kotlin.Unit handle(ru.trolsoft.compiler.generator.ListingRec rec);
//    }

    public ProjectBuilder(RText rtext, Project project, BuildOutputWindow window) {
        this.rtext = rtext;
        this.project = project;
        this.window = window;
    }

    public void build(String activeFilePath) {
        focusOwner = rtext.getFocusOwner();
        window.clearConsoles();
        if (project == null) {
            return;
        }
        switch (project.getType()) {
            case RAT -> buildRatProject();
            case I8085_RAT -> build8085RatProject();
            case BUILDER -> buildMakeBuilderProject();
            case MAKEFILE -> buildMakeFileProject();
            default -> window.prompt("Unsupported project type " + project.getType());
        }
//        window.execute("cat " + activeFilePath);
    }

    private void restoreFocus() {
        SwingUtilities.invokeLater(() -> {
            if (focusOwner != null) {
                focusOwner.requestFocus();
                focusOwner = null;
            }
        });
    }

    private void buildRatProject() {
        boolean listingVisible = rtext.getCodeListingPlugin().isCodeListingWindowVisible();
        var listingWindow = rtext.getCodeListingWindow();
        if (listingVisible) {
            listingWindow.clear();
        }
        window.execute(new BuildTask() {
            @Override
            public void run() {
                String path = project.getMainFile();
                window.prompt("Compile " + path + "\n");
                String devPath = AvrRatDevicesUtils.getDevFolder().getAbsolutePath();
                String encodingsPath = AvrRatDevicesUtils.getEncodingsFolder().getAbsolutePath();
                List<String> defines = new ArrayList<>();
                PrintStream out = getOutStream();
                PrintStream err = getErrStream();

                var codeListWindow = rtext.getCodeListingWindow();
                if (codeListWindow != null) {
                    codeListWindow.clear();
                }
                if (listingVisible) {
                    listingWindow.setSyntax(detectListingSyntax(path));
                }
                try {
                    RatKt.compileProject(null, path, devPath, encodingsPath, defines, out, err, listingVisible ? listingHandler(listingWindow) : null);
                } catch (kotlin.NotImplementedError e) {
                    throw new RuntimeException("Wrong operation", e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (listingVisible) {
                    String syntax = detectListingSyntax(path);
                    listingWindow.initText(syntax);
                    SwingUtilities.invokeLater(() -> {
                        var textArea = rtext.getMainView().getCurrentTextArea();
                        listingWindow.activateLineFor(textArea, textArea.getLine());
                    });
                }
                //window.prompt("Done.\n");
                restoreFocus();
            }
        });
    }

    @NotNull
    private static String detectListingSyntax(String path) {
        if (path.endsWith(".8080") || path.endsWith(".8085")) {
            return SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_8085;
        } else {
            return SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_AVR;
        }
    }

    private void build8085RatProject() {
        boolean listingVisible = rtext.getCodeListingPlugin().isCodeListingWindowVisible();
        var listingWindow = rtext.getCodeListingWindow();
        if (listingVisible) {
            listingWindow.clear();
        }
        window.execute(new BuildTask() {
            @Override
            public void run() {
                String path = project.getMainFile();
                window.prompt("Compile " + path + "\n");
                String devPath = AvrRatDevicesUtils.getDevFolder().getAbsolutePath();
                String encodingsPath = AvrRatDevicesUtils.getEncodingsFolder().getAbsolutePath();
                List<String> defines = new ArrayList<>();
                PrintStream out = getOutStream();
                PrintStream err = getErrStream();

                var codeListWindow = rtext.getCodeListingWindow();
                if (codeListWindow != null) {
                    codeListWindow.clear();
                }
                try {
                    RatKt.compileProject(null, path, devPath, encodingsPath, defines, out, err, listingVisible ? listingHandler(listingWindow) : null);
                } catch (kotlin.NotImplementedError e) {
                    throw new RuntimeException("Wrong operation", e);
                }
                if (listingVisible) {
                    String syntax = detectListingSyntax(path);
                    listingWindow.initText(syntax);
                }
                //window.prompt("Done.\n");
                restoreFocus();
            }
        });
    }

    private Function1<ListingRec, Unit> listingHandler(CodeListingWindow listingWindow) {
        return rec ->
        {
            var loc = rec.getLocation();
            var asm = rec.getAsmCode();

            var is8085 = listingWindow.getSyntax().equals(SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_8085);
            var addr = is8085 ? StringUtils.wordToHexStr(rec.getOffset()) : StringUtils.dwordToHexStr(rec.getOffset());
            var dump = rec.hexDumpStr(is8085 ? 9 : 12);
            var str = (asm.endsWith(":") ? "" : addr + ": " + dump + ' ') + asm;
            listingWindow.add(loc.getFileName(), loc.getLine(), str);
            //System.out.println(rec);
            return Unit.INSTANCE;
        };
    }

    private void buildMakeFileProject() {
        window.clearConsoles();
        String path = project.getMainFile();
        File pwd = new File(path).getParentFile();
        window.prompt("Compile " + project.getName() + "\n");
        window.execute("make -f " + project.getMainFile(), pwd);
        restoreFocus();
    }

    private void buildMakeBuilderProject() {
        boolean listingVisible = rtext.getCodeListingPlugin().isCodeListingWindowVisible();
        var listingWindow = rtext.getCodeListingWindow();
        if (listingVisible) {
            listingWindow.clear();
        }
        String path = project.getMainFile();
        File pwd = new File(path).getParentFile();
        window.prompt("Compile " + project.getName() + "\n");
        window.execute("builder", pwd, () -> {
            if (listingVisible) {
                listingWindow.loadRatGccListing(pwd.getAbsolutePath() + "/build/avr-rat.lss");
                var textArea = rtext.getMainView().getCurrentTextArea();
                listingWindow.activateLineFor(textArea, textArea.getLine());
            }
        });
        restoreFocus();
    }

}
