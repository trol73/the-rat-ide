package ru.trolsoft.templatebuilder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


public class TemplateFile extends TemplateItem {
    private String name;
    private String source;
    private String content;
    private boolean binary = false;
    private boolean replaceProperties = true;
    private boolean preprocess = false;

    private static final int YES = 1;
    private static final int NO = 2;
    private static final int REQUEST = 3;
    private static int replaceExistingFiles = REQUEST;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public void setReplaceProperties(boolean replaceProperties) {
        this.replaceProperties = replaceProperties;
    }

    public void setPreprocess(boolean preprocess) {
        this.preprocess = preprocess;
    }

    public String getFullName() {
        return getProperties().getBasePath() + File.separatorChar + getProperties().replaceProperties(name);
    }

    public String getFullSourceName() {
        return getProperties().replaceProperties(source);
    }

    public void create() throws IOException {
        if (!isIfConditionTrue()) {
            return;
        }
        String fileName = getFullName();
        if (!fileCreateRequest(fileName)) {
            return;
        }
        Utils.createDirsForFile(fileName);
        getProperties().setProcessedFileName(fileName);
//        TemplateBuilder.con.info("  Create file: ", fileName).nl();
        try (RandomAccessFile outFile = new RandomAccessFile(fileName, "rw")) {

            if (!new File(getFullSourceName()).exists()) {
                TemplateBuilder.error("Can't find source file - " + getFullSourceName());
            }
            if (binary) {
                if (getSource() != null)
                    createBinary(outFile);
            } else {
                createText(outFile);
            }
        }
        getProperties().setProcessedFileName("");
    }

    private void createText(RandomAccessFile outFile) throws IOException {
        if (content != null && !content.isBlank()) {
            if (replaceProperties) {
                content = TemplateBuilder.getProperties().replaceProperties(content);
            }
            if (preprocess) {
                //content = Preprocessor.preprocess(content, TemplateBuilder.getProperties());
            }
            outFile.writeBytes(content);
        }
        if (source != null && source.length() > 0) {
            String s = Utils.readTextFile(getFullSourceName());
            if (replaceProperties) {
                s = TemplateBuilder.getProperties().replaceProperties(s);
            }
            if (preprocess) {
                //s = Preprocessor.preprocess(s, TemplateBuilder.getProperties());
            }
            outFile.writeBytes(s);
        }
    }

    private void createBinary(RandomAccessFile outFile) throws IOException {
        try (RandomAccessFile inFile = new RandomAccessFile(getFullSourceName(), "r")) {
            byte[] bytes = new byte[10240];

            int bytesRead;
            do {
                bytesRead = inFile.read(bytes);
                if (bytesRead > 0) {
                    outFile.write(bytes, 0, bytesRead);
                }
            } while (bytesRead > 0);
        }
    }


    private boolean fileCreateRequest(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) {
            return true;
        }

        boolean delete;
        if (replaceExistingFiles == YES) {
            delete = true;
        } else if (replaceExistingFiles == NO) {
            delete = false;
        } else {
            while (true) {
                String answer = TemplateBuilder.input("File already exist, overwrite it (" + filePath + ") [Yes/No/All]? ");
                if ("yes".equalsIgnoreCase(answer) || "y".equalsIgnoreCase(answer)) {
                    delete = true;
                    break;
                } else if ("no".equalsIgnoreCase(answer) || "n".equalsIgnoreCase(answer)) {
                    delete = false;
                    break;
                } else if ("all".equalsIgnoreCase(answer) || "a".equalsIgnoreCase(answer)) {
                    replaceExistingFiles = YES;
                    delete = true;
                    break;
                }
                TemplateBuilder.print("  please, press 'yes', 'no' or 'all': ");
            } // while
        }
        if (delete) {
            f.delete();
            return true;
        }
        return false;
    }


}
