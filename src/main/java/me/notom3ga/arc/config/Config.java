package me.notom3ga.arc.config;

import me.notom3ga.arc.Arc;
import me.notom3ga.arc.util.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Config {
    public static String PROFILER_URL = "127.0.0.1:8080";
    public static List<String> HIDDEN_TOKENS = new ArrayList<>() {{
        add("server-ip");
        add("rcon");
        add("query");
        add("level-seed");
        add("database");
        add("seed-");
        add("settings.bungeecord-addresses");
        add("settings.velocity-support.secret");
        add("worldgen.seeds.populator");
        add("token");
    }};

    public static void load(Arc plugin) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration config = new YamlConfiguration();

        try {
            Files.createDirectories(plugin.getDataFolder().toPath());
            Files.createFile(file.toPath());
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            Logger.severe("Failed to load config, resetting to defaults");
            return;
        }

        config.options().copyDefaults(true);

        PROFILER_URL = config.getString("profiler.url", PROFILER_URL);
        if (!config.contains("profiler.hidden-tokens")) {
            config.set("profiler.hidden-tokens", HIDDEN_TOKENS);
        }
        HIDDEN_TOKENS = config.getStringList("profiler.hidden-tokens");

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.severe("Failed to load config, resetting to defaults");
        }
    }
}
