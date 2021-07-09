package me.notom3ga.arc;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.notom3ga.arc.commmand.GcCommand;
import me.notom3ga.arc.commmand.HelpCommand;
import me.notom3ga.arc.commmand.ProfilerCommand;
import me.notom3ga.arc.config.Config;
import me.notom3ga.arc.profiler.ProfilingManager;
import me.notom3ga.arc.monitor.CpuMonitor;
import me.notom3ga.arc.util.Logger;
import net.minecraft.commands.Commands;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Arc extends JavaPlugin {
    private static Arc instance;

    public static Arc getInstance() {
        return instance;
    }

    private Executor executor;

    @Override
    public void onEnable() {
        instance = this;
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
        } catch (ClassNotFoundException e) {
            Logger.severe("Arc requires Paper for one of its forks to run, disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-XX:+DebugNonSafepoints")) {
            Logger.warn("Please use the flags '-XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints' for optimal profiling.");
        }

        Config.load(this);
        executor = Executors.newSingleThreadExecutor();

        ((CraftServer) getServer()).getServer().getCommands().getDispatcher().register(Commands.literal("arc")
                .requires(context -> context.hasPermission(4, "arc.command"))
                .then(Commands.literal("gc")
                        .executes(context -> command(() -> GcCommand.execute(context.getSource().getBukkitSender())))
                )
                .then(Commands.literal("profiler")
                        .executes(context -> command(() -> ProfilerCommand.execute(context.getSource().getBukkitSender(), "")))
                        .then(Commands.argument("option", StringArgumentType.word())
                                .suggests((context, builder) -> builder
                                        .suggest("start", new LiteralMessage("Start the profiler"))
                                        .suggest("stop", new LiteralMessage("Stop the profiler"))
                                        .buildFuture())
                                .executes(context -> command(() -> ProfilerCommand.execute(context.getSource().getBukkitSender(), context.getArgument("option", String.class))))
                        )
                )
                .executes(context -> command(() -> HelpCommand.execute(context.getSource().getBukkitSender())))
        );

        CpuMonitor.ensureInitialzation();
    }

    @Override
    public void onDisable() {
        if (ProfilingManager.isProfiling()) {
            try {
                ProfilingManager.stop(false);
            } catch (IOException ignore) {
            }
        }
        instance = null;
    }

    private int command(Runnable command) {
        executor.execute(command);
        return 0;
    }
}
