package me.notom3ga.arc.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

public class Logger {

    private static void log(TextComponent message) {
        Bukkit.getServer().sendMessage(TextComponent.ofChildren(
                Component.text("[Arc] ", TextColor.fromHexString("#0A9396")),
                message
        ));
    }

    public static void info(String message) {
        log(TextComponent.ofChildren(
                Component.text("[INFO] ", NamedTextColor.GREEN),
                LegacyComponentSerializer.legacyAmpersand().deserialize(message)
        ));
    }

    public static void warn(String message) {
        log(TextComponent.ofChildren(
                Component.text("[WARN] ", NamedTextColor.GOLD),
                LegacyComponentSerializer.legacyAmpersand().deserialize(message)
        ));
    }

    public static void severe(String message) {
        log(TextComponent.ofChildren(
                Component.text("[SEVERE] ", NamedTextColor.DARK_RED),
                LegacyComponentSerializer.legacyAmpersand().deserialize(message)
        ));
    }
}
