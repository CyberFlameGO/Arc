package me.notom3ga.arc.bukkit;

import me.notom3ga.arc.common.ArcPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.List;

public class ArcBukkit extends JavaPlugin implements ArcPlugin {

    @Override
    public Path pluginDirectory() {
        return getDataFolder().toPath();
    }

    @Override
    public void executeAsync(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    @Override
    public List<Path> configFiles() {
        return List.of(Path.of("bukkit.yml"), Path.of("spigot.yml"), Path.of("paper.yml"),
                Path.of("tuinity.yml"), Path.of("purpur.yml"), Path.of("airplane.air"));
    }
}
