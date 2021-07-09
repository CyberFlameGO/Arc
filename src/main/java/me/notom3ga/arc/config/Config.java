package me.notom3ga.arc.config;

import me.notom3ga.arc.Arc;
import me.notom3ga.arc.util.Logger;
import me.notom3ga.arc.util.NullUtil;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Config {
    public static String URL;

    public static List<String> CONFIGS = List.of("server.properties", "bukkit.yml", "spigot.yml", "paper.yml", "tuinity.yml", "purpur.yml", "airplane.air");
    public static List<String> HIDDEN_TOKENS;

    public static void load(Arc plugin) {
        plugin.saveDefaultConfig();
        YamlConfiguration config = (YamlConfiguration) plugin.getConfig();

        URL = config.getString("profiler.url", URL);
        HIDDEN_TOKENS = config.getStringList("profiler.config.hidden-tokens");

        if (!config.getStringList("profiler.config.additional-files").isEmpty()) {
            CONFIGS.addAll(config.getStringList("profiler.config.additional-files"));
        }
    }
}
