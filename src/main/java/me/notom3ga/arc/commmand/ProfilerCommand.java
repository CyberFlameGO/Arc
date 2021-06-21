package me.notom3ga.arc.commmand;

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
        sender.sendMessage("arc profiler info!");
        return 0;
    }

    private static int start(CommandSender sender) {
        sender.sendMessage("arc profiler start!");
        return 0;
    }

    private static int stop(CommandSender sender) {
        sender.sendMessage("arc profiler stop!");
        return 0;
    }
}
