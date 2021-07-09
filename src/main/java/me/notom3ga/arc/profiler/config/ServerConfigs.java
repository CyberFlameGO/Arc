package me.notom3ga.arc.profiler.config;

import co.aikar.timings.TimingsManager;
import me.notom3ga.arc.config.Config;
import me.notom3ga.arc.util.StringUtil;
import org.apache.logging.log4j.core.util.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class ServerConfigs {

    public static String getConfig(String config) throws IOException {
        File file = new File(config);

        String[] hiddenTokens = new ArrayList<String>(){{
            addAll(Config.HIDDEN_TOKENS);
            addAll(TimingsManager.hiddenConfigs);
        }}.toArray(String[]::new);

        if (!file.exists()) {
            throw new IllegalArgumentException(config + " doesn't exist!");
        }

        switch (FileUtils.getFileExtension(file)) {
            case "properties", "air" -> {
                StringBuilder builder = new StringBuilder();
                Files.lines(file.toPath(), StandardCharsets.UTF_8).forEach(line -> {
                    if (!line.trim().startsWith("#") && !StringUtil.containsAny(StringUtil.substringBefore(line.trim(), "=").trim(), hiddenTokens)) {
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
                    if (StringUtil.containsAny(key, hiddenTokens)) {
                        configuration.set(key, null);
                    }
                }
                return configuration.saveToString();
            }

            default -> throw new IllegalArgumentException(config + " is not a valid file type.");
        }
    }
}
