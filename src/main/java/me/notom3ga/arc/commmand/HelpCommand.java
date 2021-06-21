package me.notom3ga.arc.commmand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

public class HelpCommand {
    private static final TextComponent help = TextComponent.ofChildren(
            Component.newline(),
            Component.text(" ----- ----- ----- ", TextColor.fromHexString("#F1FAEE")),
            Component.text(" >> ", TextColor.fromHexString("#A8DADC")),
            Component.text("Arc", TextColor.fromHexString("#A8DADC"), TextDecoration.BOLD),
            Component.text(" << ", TextColor.fromHexString("#A8DADC")),
            Component.text(" ----- ----- ----- ", TextColor.fromHexString("#F1FAEE")),
            Component.newline(),
            Component.newline(),
            Component.text(" <|> ", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
            Component.text("arc profiler [ info | start | stop ]", TextColor.fromHexString("#457B9D"), TextDecoration.ITALIC),
            Component.newline(),
            Component.newline(),
            Component.text(" ----- ----- ----- ", TextColor.fromHexString("#F1FAEE")),
            Component.text(" >> ", TextColor.fromHexString("#F1FAEE")),
            Component.text("---", TextColor.fromHexString("#F1FAEE"), TextDecoration.BOLD),
            Component.text(" << ", TextColor.fromHexString("#F1FAEE")),
            Component.text(" ----- ----- ----- ", TextColor.fromHexString("#F1FAEE")),
            Component.newline()
    );

    public static int execute(CommandSender sender) {
        sender.sendMessage(help);
        return 0;
    }
}
