package me.notom3ga.arc.util;

public class StringUtil {

    public static boolean containsAny(String string, String... toCheck) {
        for (String checking : toCheck) {
            if (string.contains(checking)) {
                return true;
            }
        }

        return false;
    }

    public static String substringBefore(String str, String separator) {
        if (!str.isEmpty() && separator != null) {
            if (separator.length() == 0) {
                return "";
            } else {
                int pos = str.indexOf(separator);
                return pos == -1 ? str : str.substring(0, pos);
            }
        } else {
            return str;
        }
    }
}
