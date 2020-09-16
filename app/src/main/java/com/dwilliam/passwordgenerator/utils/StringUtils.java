package com.dwilliam.passwordgenerator.utils;

import java.util.HashSet;
import java.util.Set;

public final class StringUtils {

    private StringUtils() {}

    public static boolean hasDuplicates(String text) {
        Set<Integer> set = new HashSet<>();
        int[] points = text.codePoints().toArray();

        for (int point : points) {
            if (!set.add(point)) return true;
        }

        return false;
    }

    public static String removeDuplicates(String text) {
        Set<Integer> set = new HashSet<>();
        int[] points = text.codePoints().toArray();
        StringBuilder output = new StringBuilder();

        for (int point : points) {
            if (set.add(point)) {
                output.appendCodePoint(point);
            }
        }

        return output.toString();
    }

}
