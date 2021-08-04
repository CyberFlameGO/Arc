package me.notom3ga.arc.common;

import me.notom3ga.arc.common.config.Config;

import java.nio.file.Path;
import java.util.List;

public interface ArcPlugin {

    Path pluginDirectory();

    void executeAsync(Runnable runnable);

    List<Path> configFiles();

    default void enable() throws Exception {
        Config.load(this);
    }
}
