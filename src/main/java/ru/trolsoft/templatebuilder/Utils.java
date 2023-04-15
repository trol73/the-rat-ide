package ru.trolsoft.templatebuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class Utils {

    private static String homePath;

    public static void createDirsForFile(String fileName) {
        try {
            File f = new File(fileName);
            File canonFile = f.getCanonicalFile();
            File parent = canonFile.getParentFile();
            if (!parent.exists()) {
                parent.getAbsoluteFile().mkdirs();
            }
        } catch (IOException e) {
        }
    }


    public static String readTextFile(String fileName) throws IOException {
        FileReader fr = new FileReader(fileName);
        BufferedReader in = new BufferedReader(fr);

        String result = "";
        String line;
        boolean firstLine = true;
        while (true) {
            if (!firstLine) {
                result += '\n';
            } else {
                firstLine = false;
            }
            line = in.readLine();
            if (line == null) {
                break;
            }
            result += line;
        }
        in.close();
        fr.close();
        return result;
    }


    public static String getFileNameWithoutExtension(String fileName) {
        File file = new File(fileName);
        int whereDot = file.getName().lastIndexOf('.');
        if (whereDot > 0 && whereDot <= file.getName().length() - 2) {
            return file.getName().substring(0, whereDot);
        }
        return "";
    }


    public static String getFileName(String fileName) {
        return new File(fileName).getName();
    }


    public static String getFileExtension(String fileName) {
        File file = new File(fileName);
        int whereDot = file.getName().lastIndexOf('.');
        if (whereDot > 0 && whereDot <= file.getName().length() - 2) {
            return file.getName().substring(whereDot + 1);
        }
        return "";
    }

    public static String getFilePath(String fileName) {
        return new File(fileName).getParent();
    }

    public static String getHomePath() {
        if (homePath == null) {
            homePath = new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
            int index = homePath.lastIndexOf(File.separator);
            if (index >= 0) {
                homePath = homePath.substring(0, index);
            }
        }
        return homePath;
    }


    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }


    public static boolean copyFile(String src, String dest) {
        createDirsForFile(dest);
        try {
            copyFile(new File(src), new File(dest));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    public static void copyDir(String src, String dest) {
        File fSrc = new File(src);
        File[] childs = fSrc.listFiles();

        for (File f : childs) {
            if (f.isDirectory()) {
                String outDir = dest + File.separatorChar + f.getName();
//				System.out.println("[D] "+f + " -> "  + outDir);
                copyDir(f.toString(), outDir);
            } else {
                String outName = dest + File.separatorChar + f.getName();
//				System.out.println("[F] "+f + " -> " + outName);				
                copyFile(f.toString(), outName);
            }
        }
    }


}
