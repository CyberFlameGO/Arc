package me.notom3ga.arc.commmand;

import me.notom3ga.arc.Arc;
import me.notom3ga.arc.config.Config;
import me.notom3ga.arc.profiler.ProfilingManager;
import me.notom3ga.arc.util.compat.Compatibility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.Locale;

public class ProfilerCommand {

    public static void execute(CommandSender sender, String option) {
        switch (option.toLowerCase(Locale.ROOT)) {
            case "start" -> start(sender);
            case "stop" -> stop(sender);
            default -> HelpCommand.execute(sender);
        }
    }

    private static void start(CommandSender sender) {
        Compatibility compatibility = ProfilingManager.checkCompatibility();
        if (compatibility != Compatibility.COMPATIBLE) {
            sender.sendMessage(TextComponent.ofChildren(
                    Component.text("Error: ", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
                    Component.text(compatibility.getMessage(), TextColor.fromHexString("#F1FAEE"))
            ));
            return;
        }

        if (ProfilingManager.isProfiling()) {
            sender.sendMessage(TextComponent.ofChildren(
                    Component.text("Error: ", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
                    Component.text("A profile is already running.", TextColor.fromHexString("#F1FAEE"))
            ));
            return;
        }

        try {
            ProfilingManager.start();
        } catch (Exception e) {
            sender.sendMessage(TextComponent.ofChildren(
                    Component.text("Error: ", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
                    Component.text("Failed to start profiling, " + e.getLocalizedMessage(), TextColor.fromHexString("#F1FAEE"))
            ));
            return;
        }

        sender.sendMessage(TextComponent.ofChildren(
                Component.text("Arc", TextColor.fromHexString("#1D3557"), TextDecoration.BOLD),
                Component.text(" >> ", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
                Component.text("Started profiling, the profiler will stop in 10 minutes.", TextColor.fromHexString("#F1FAEE"))
        ));

        Bukkit.getScheduler().runTaskLaterAsynchronously(Arc.getInstance(), () -> stop(sender), 200);
    }

    private static void stop(CommandSender sender) {
        if (!ProfilingManager.isProfiling()) {
            sender.sendMessage(TextComponent.ofChildren(
                    Component.text("Error: ", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
                    Component.text("A profile is not currently running.", TextColor.fromHexString("#F1FAEE"))
            ));
            return;
        }

        try {
            String key = ProfilingManager.stop();
            String url = Config.URL + (Config.URL.endsWith("/") ? "" : "/") + key;

            sender.sendMessage(TextComponent.ofChildren(
                    Component.text("Arc", TextColor.fromHexString("#1D3557"), TextDecoration.BOLD),
                    Component.text(" >> ", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
                    Component.text("Stopped profiling: ", TextColor.fromHexString("#F1FAEE")),
                    Component.text(url, TextColor.fromHexString("#A8DADC"), TextDecoration.ITALIC).clickEvent(ClickEvent.openUrl(url)),
                    Component.text(".", TextColor.fromHexString("#F1FAEE"))
            ));
        } catch (IOException ignore) {
            sender.sendMessage(TextComponent.ofChildren(
                    Component.text("Error: ", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
                    Component.text("Failed to upload profiler contents.", TextColor.fromHexString("#F1FAEE"))
            ));
        }
    }
}
