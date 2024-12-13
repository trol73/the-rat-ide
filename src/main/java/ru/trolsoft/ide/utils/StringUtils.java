package ru.trolsoft.ide.utils;

import java.io.File;

public class StringUtils {

    private static final String[] STRING_OF_ZERO = {"", "0", "00", "000", "0000", "00000", "000000", "0000000", "00000000", "000000000", "0000000000"};

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

    public static String wordToHexStr(long val) {
        String result = Long.toHexString(val);
        int len = result.length();
        if (len > 4) {
            return result;
        }
        return STRING_OF_ZERO[4-len] + result;
    }
    public static String dwordToHexStr(long val) {
        String result = Long.toHexString(val);
        int len = result.length();
        if (len > 8) {
            return result;
        }
        return STRING_OF_ZERO[8-len] + result;
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
