package me.notom3ga.arc.util;

public class StringUtils {

    public static boolean containsAny(String string, String... toCheck) {
        for (String checking : toCheck) {
            if (string.contains(checking)) {
                return true;
            }
        }

        return false;
    }
}
