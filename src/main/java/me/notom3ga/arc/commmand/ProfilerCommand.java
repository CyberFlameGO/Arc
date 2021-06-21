package me.notom3ga.arc.commmand;

import me.notom3ga.arc.profiler.ProfilingManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public class ProfilerCommand {

    public static int execute(CommandSender sender, String option) {
        switch (option.toLowerCase(Locale.ROOT)) {
            case "info" -> {
                return info(sender);
            }

            case "start" -> {
                return start(sender);
            }

            case "stop" -> {
                return stop(sender);
            }

            default -> {
                return HelpCommand.execute(sender);
            }
        }
    }

    private static int info(CommandSender sender) {
        // todo
        return 0;
    }

    private static int start(CommandSender sender) {
        if (!ProfilingManager.checkCompatibility().isBlank()) {
            sender.sendMessage(TextComponent.ofChildren(
                    Component.text("Error: ", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
                    Component.text(ProfilingManager.checkCompatibility(), TextColor.fromHexString("#F1FAEE"))
            ));
            return 1;
        }

        if (ProfilingManager.isProfiling()) {
            sender.sendMessage(TextComponent.ofChildren(
                    Component.text("Error: ", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
                    Component.text("A profile is already running.", TextColor.fromHexString("#F1FAEE"))
            ));
            return 1;
        }

        ProfilingManager.start();
        sender.sendMessage(TextComponent.ofChildren(
                Component.text("Arc", TextColor.fromHexString("#1D3557"), TextDecoration.BOLD),
                Component.text(" >> ", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
                Component.text("Started profiling.", TextColor.fromHexString("#F1FAEE"))
        ));
        return 0;
    }

    private static int stop(CommandSender sender) {
        if (!ProfilingManager.isProfiling()) {
            sender.sendMessage(TextComponent.ofChildren(
                    Component.text("Error: ", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
                    Component.text("A profile is not currently running.", TextColor.fromHexString("#F1FAEE"))
            ));
            return 1;
        }

        ProfilingManager.stop();
        sender.sendMessage(TextComponent.ofChildren(
                Component.text("Arc", TextColor.fromHexString("#1D3557"), TextDecoration.BOLD),
                Component.text(" >> ", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
                Component.text("Stopped profiling: <link>.", TextColor.fromHexString("#F1FAEE"))
        ));
        return 0;
    }
}
