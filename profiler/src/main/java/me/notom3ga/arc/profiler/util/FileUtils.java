package me.notom3ga.arc.profiler.util;

import java.nio.file.Path;

public class FileUtils {

    public static String getFileExtension(Path path) {
        String name = path.getFileName().toString();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf);
    }
}
