package me.notom3ga.arc.profiler.config;

import com.google.common.io.Files;
import me.notom3ga.arc.config.Config;
import me.notom3ga.arc.util.StringUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerConfigs {
    public static final String[] allConfigs = new String[] {
            "server.properties",
            "bukkit.yml",
            "spigot.yml",
            "paper.yml",
            "tuinity.yml",
            "purpur.yml",
            "airplane.air"
    };

    public static String getConfig(String config) throws IOException {
        File file = new File(config);

        Object[] hiddenObjects = Config.HIDDEN_TOKENS.toArray();
        String[] hiddenTokens = Arrays.copyOf(hiddenObjects, hiddenObjects.length, String[].class);

        if (!file.exists()) {
            throw new IllegalArgumentException(config + " doesn't exist!");
        }

        switch (Files.getFileExtension(config)) {
            case "properties", "air" -> {
                StringBuilder builder = new StringBuilder();
                Files.readLines(file, StandardCharsets.UTF_8).forEach(line -> {
                    if (!line.trim().startsWith("#") && !StringUtils.containsAny(StringUtils.substringBefore(line.trim(), "=").trim(), hiddenTokens)) {
                        if (!builder.isEmpty()) {
                            builder.append("\n");
                        }
                        builder.append(line);
                    }
                });
                return builder.toString();
            }

            case "yml" -> {
                YamlConfiguration configuration = new YamlConfiguration();
                try {
                    configuration.load(file);
                } catch (InvalidConfigurationException e) {
                    throw new IOException(e);
                }
                configuration.options().header(null);
                for (String key : configuration.getKeys(true)) {
                    if (StringUtils.containsAny(key, hiddenTokens)) {
                        configuration.set(key, null);
                    }
                }
                return configuration.saveToString();
            }

            default -> throw new IllegalArgumentException(config + " is not a valid file type.");
        }
    }
}
