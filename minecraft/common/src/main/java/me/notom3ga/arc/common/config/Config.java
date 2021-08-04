package me.notom3ga.arc.common.config;

import me.notom3ga.arc.common.ArcPlugin;
import me.notom3ga.arc.common.util.FileUtils;
import me.notom3ga.arc.common.util.toml.TOML;

import java.io.IOException;
import java.nio.file.Path;

public class Config {
    private static TOML toml;

    public static void load(ArcPlugin plugin) throws IOException {
        Path config = FileUtils.copyFromJar("config.toml", plugin.pluginDirectory().resolve("config.toml"));

    }
}
