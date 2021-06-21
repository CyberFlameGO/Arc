package me.notom3ga.arc;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.notom3ga.arc.commmand.HelpCommand;
import me.notom3ga.arc.commmand.ProfilerCommand;
import me.notom3ga.arc.config.Config;
import me.notom3ga.arc.util.Logger;
import net.minecraft.commands.CommandListenerWrapper;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

public class Arc extends JavaPlugin {
    private static Arc instance;

    public static Arc getInstance() {
        return instance;
    }

    public Arc() {
        instance = this;
    }

    @Override
    public void onEnable() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
        } catch (ClassNotFoundException e) {
            Logger.severe("Arc requires Paper for one of its forks to run, disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Config.load(this);

        ((CraftServer) getServer()).getServer().getCommandDispatcher().a().register(LiteralArgumentBuilder.<CommandListenerWrapper>literal("arc")
                .requires(listener -> listener.hasPermission(4, "arc.command"))
                .then(LiteralArgumentBuilder.<CommandListenerWrapper>literal("profiler")
                        .executes(listener -> ProfilerCommand.execute(listener.getSource().getBukkitSender(), ""))
                        .then(RequiredArgumentBuilder.<CommandListenerWrapper, String>argument("option", StringArgumentType.word())
                                .suggests((context, builder) -> builder
                                        .suggest("info", new LiteralMessage("View info on the currently running profiler"))
                                        .suggest("start", new LiteralMessage("Start the profiler"))
                                        .suggest("stop", new LiteralMessage("Stop the profiler"))
                                        .buildFuture())
                                .executes(listener -> ProfilerCommand.execute(listener.getSource().getBukkitSender(), listener.getArgument("option", String.class)))
                        )
                )
                .executes(listener -> HelpCommand.execute(listener.getSource().getBukkitSender()))
        );
    }
}
