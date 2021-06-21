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
    public static List<String> hiddenTokens = new ArrayList<>() {{
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

        if (!config.contains("config.hidden-tokens")) {
            config.set("config.hidden-tokens", hiddenTokens);
        }

        hiddenTokens = config.getStringList("config.hidden-tokens");

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.severe("Failed to load config, resetting to defaults");
        }
    }
}
