package me.notom3ga.arc.profiler.config;

import com.google.common.io.Files;
import me.notom3ga.arc.config.Config;
import me.notom3ga.arc.util.StringUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

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
        String[] hiddenTokens = (String[]) Config.hiddenTokens.toArray();

        if (!file.exists()) {
            throw new IllegalArgumentException(config + " doesn't exist!");
        }

        switch (Files.getFileExtension(config)) {
            case "properties" -> {
                Properties properties = new Properties();
                try (FileInputStream stream = new FileInputStream(file)) {
                    properties.load(stream);
                }
                for (String hiddenToken : hiddenTokens) {
                    properties.remove(hiddenToken);
                }
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                properties.store(output, "");
                return Arrays.stream(output.toString().split("\n"))
                        .filter(line -> !line.startsWith("#") || !StringUtils.containsAny(line, hiddenTokens))
                        .collect(Collectors.joining("\n"));
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

            case "air" -> {
                return Files.readLines(file, StandardCharsets.UTF_8)
                        .stream()
                        .filter(line -> !line.trim().startsWith("#") || !StringUtils.containsAny(line.trim(), hiddenTokens))
                        .collect(Collectors.joining("\n"));
            }

            default -> throw new IllegalArgumentException(config + " is not a valid file type.");
        }
    }
}
