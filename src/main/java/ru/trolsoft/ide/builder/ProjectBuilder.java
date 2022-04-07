package ru.trolsoft.ide.builder;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.fife.rtext.RText;
import org.fife.rtext.plugins.buildoutput.BuildOutputWindow;
import org.fife.rtext.plugins.buildoutput.BuildTask;
import org.fife.rtext.plugins.project.model.Project;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import ru.trolsoft.compiler.generator.ListingRec;
import ru.trolsoft.ide.plugins.codelisting.CodeListingWindow;
import ru.trolsoft.ide.therat.AvrRatDevicesUtils;
import ru.trolsoft.ide.utils.StringUtils;
import ru.trolsoft.therat.RatKt;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ProjectBuilder {
    private final RText rtext;
    private final Project project;
    private final BuildOutputWindow window;

    public interface RatListingHandler {
        kotlin.Unit handle(ru.trolsoft.compiler.generator.ListingRec rec);
    }

    public ProjectBuilder(RText rtext, Project project, BuildOutputWindow window) {
        this.rtext = rtext;
        this.project = project;
        this.window = window;
    }

    public void build(String activeFilePath) {
        window.clearConsoles();
        switch (project.getType()) {
            case AVR_RAT -> buildAvrRatProject();
            case BUILDER -> buildMakeBuilderProject();
            case MAKEFILE -> buildMakeFileProject();
        }
//        window.execute("cat " + activeFilePath);
    }

    private void buildAvrRatProject() {
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
                String devPath = AvrRatDevicesUtils.getFolder().getAbsolutePath();
                List<String> defines = new ArrayList<>();
                PrintStream out = getOutStream();
                PrintStream err = getErrStream();

                var codeListWindow = rtext.getCodeListingWindow();
                if (codeListWindow != null) {
                    codeListWindow.clear();
                }
                RatKt.compileProject(path, devPath, defines, out, err, listingVisible ? listingHandler(listingWindow) : null);
                if (listingVisible) {
                    listingWindow.initText(SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_AVR);
                }
                window.prompt("Done.\n");
            }
        });
    }

    private Function1<ListingRec, Unit> listingHandler(CodeListingWindow listingWindow) {
        return rec ->
        {
            var loc = rec.getLocation();
            var asm = rec.getAsmCode();
            var str = (asm.endsWith(":") ? "" : StringUtils.dwordToHexStr(rec.getOffset()) + ": ") + asm;
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
        window.execute("builder", pwd);
        if (listingVisible) {
            listingWindow.loadRatGccListing(pwd.getAbsolutePath() + "/build/avr-rat.lss");
        }
    }

}
