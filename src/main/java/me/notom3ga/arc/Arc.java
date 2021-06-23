package me.notom3ga.arc;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.notom3ga.arc.commmand.GcCommand;
import me.notom3ga.arc.commmand.HelpCommand;
import me.notom3ga.arc.commmand.ProfilerCommand;
import me.notom3ga.arc.config.Config;
import me.notom3ga.arc.profiler.ProfilingManager;
import me.notom3ga.arc.util.Logger;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Arc extends JavaPlugin {
    private Executor executor;

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
        executor = Executors.newSingleThreadExecutor();

        ((CraftServer) getServer()).getServer().getCommands().getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("arc")
                .requires(context -> context.hasPermission(4, "arc.command"))
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("gc")
                        .executes(context -> command(() -> GcCommand.execute(context.getSource().getBukkitSender())))
                )
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("profiler")
                        .executes(context -> command(() -> ProfilerCommand.execute(context.getSource().getBukkitSender(), "")))
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("option", StringArgumentType.word())
                                .suggests((context, builder) -> builder
                                        .suggest("start", new LiteralMessage("Start the profiler"))
                                        .suggest("stop", new LiteralMessage("Stop the profiler"))
                                        .buildFuture())
                                .executes(context -> command(() -> ProfilerCommand.execute(context.getSource().getBukkitSender(), context.getArgument("option", String.class))))
                        )
                )
                .executes(context -> command(() -> HelpCommand.execute(context.getSource().getBukkitSender())))
        );
    }

    @Override
    public void onDisable() {
        if (ProfilingManager.isProfiling()) {
            ProfilingManager.stop(false);
        }
    }

    private int command(Runnable command) {
        executor.execute(command);
        return 0;
    }
}
