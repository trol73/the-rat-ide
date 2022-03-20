package ru.trolsoft.ide.utils;

public class StringHashUtils {

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
}
