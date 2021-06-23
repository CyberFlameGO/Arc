package me.notom3ga.arc.commmand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GcCommand {
    private static final DecimalFormat DF = new DecimalFormat("#.##");

    public static void execute(CommandSender sender) {
        List<TextComponent> report = new ArrayList<>();
        report.add(TextComponent.ofChildren(
                Component.newline(),
                Component.text(" ---- ---- ", TextColor.fromHexString("#F1FAEE")),
                Component.text(" >> ", TextColor.fromHexString("#1D3557")),
                Component.text("Server GC", TextColor.fromHexString("#1D3557"), TextDecoration.BOLD),
                Component.text(" << ", TextColor.fromHexString("#1D3557")),
                Component.text(" ---- ---- ", TextColor.fromHexString("#F1FAEE"))
        ));

        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();

        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            double averageTime = 0.0;
            long averageFrequency = 0;

            try {
                averageTime = (double) (gc.getCollectionTime() / gc.getCollectionCount());
                averageFrequency = (uptime - gc.getCollectionTime()) / gc.getCollectionCount();
            } catch (ArithmeticException ignore) {
            }

            report.add(TextComponent.ofChildren(
                    Component.newline(),
                    Component.newline(),
                    Component.text("    "),
                    Component.text(gc.getName() + ":", TextColor.fromHexString("#E63946"), TextDecoration.BOLD),
                    Component.newline(),
                    Component.text("      "),
                    Component.text(gc.getCollectionCount(), TextColor.fromHexString("#457B9D")),
                    Component.text(" total collections", TextColor.fromHexString("#A8DADC"), TextDecoration.ITALIC),
                    Component.newline(),
                    Component.text("      "),
                    Component.text(DF.format(averageTime) + "ms", TextColor.fromHexString("#457B9D")),
                    Component.text(" every ", TextColor.fromHexString("#A8DADC"), TextDecoration.ITALIC),
                    Component.text(formatTime(averageFrequency), TextColor.fromHexString("#457B9D"))
            ));
        }

        report.add(TextComponent.ofChildren(
                Component.newline(),
                Component.newline(),
                Component.text(" ---- ---- ", TextColor.fromHexString("#F1FAEE")),
                Component.text(" >> ", TextColor.fromHexString("#F1FAEE")),
                Component.text("---- ----", TextColor.fromHexString("#F1FAEE"), TextDecoration.BOLD),
                Component.text(" << ", TextColor.fromHexString("#F1FAEE")),
                Component.text(" ---- ---- ", TextColor.fromHexString("#F1FAEE")),
                Component.newline()

        ));

        Object[] objects = report.toArray();
        sender.sendMessage(TextComponent.ofChildren(Arrays.copyOf(objects, objects.length, TextComponent[].class)));
    }

    private static String formatTime(long millis) {
        if (millis <= 0) {
            return "0s";
        }

        long second = millis / 1000;
        long minute = second / 60;
        second = second % 60;

        StringBuilder sb = new StringBuilder();
        if (minute != 0) {
            sb.append(minute).append("m");
        } else if (second != 0) {
            sb.append(second).append("s");
        } else {
            sb .append(millis).append("ms");
        }

        return sb.toString().trim();
    }
}
