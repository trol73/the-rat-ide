package ru.trolsoft.ide.utils;

import java.io.File;

public class StringUtils {

    public static int hash(String s) {
        // s[0]*31^(n-1) + s[1]*31^(n-2) + … + s[n-1]
        int hash = 0;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            hash = 31 * hash + s.charAt(i);
        }
        return hash;
    }

    public static int hash(char[] array, int start, int end) {
        int hash = 0;
        for (int i = start; i <= end; i++) {
            hash = 31 * hash + array[i];
        }
        return hash;
    }

    public static boolean isValidInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static String getFileExt(File f) {
        String name = f.getName();
        int pos = name.lastIndexOf('.');
        return pos >= 0 ? name.substring(pos+1) : "";
    }

    public static String getFileExt(String path) {
        int pos = path.lastIndexOf('.');
        return pos >= 0 ? path.substring(pos+1) : "";
    }

    public static String removeFileExt(String path) {
        int pos = path.lastIndexOf('.');
        return pos >= 0 ? path.substring(0, pos) : path;
    }
}
