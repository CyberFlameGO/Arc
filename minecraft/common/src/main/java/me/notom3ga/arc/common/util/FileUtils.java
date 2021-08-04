package me.notom3ga.arc.common.util;

import me.notom3ga.arc.common.ArcPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    public static Path copyFromJar(String name, Path output) throws IOException {
        InputStream stream = ArcPlugin.class.getClassLoader().getResourceAsStream(name);
        if (stream == null) {
            throw new IllegalArgumentException(name + " was not found in the classloader.");
        }

        Files.copy(stream, output);
        return output;
    }
}
