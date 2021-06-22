package me.notom3ga.arc.profiler;

import com.destroystokyo.paper.PaperConfig;
import me.notom3ga.arc.Arc;
import me.notom3ga.arc.profiler.config.ServerConfigs;
import me.notom3ga.arc.proto.ArcProto;
import me.notom3ga.arc.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.spigotmc.SpigotConfig;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.VirtualMemory;
import oshi.software.os.OperatingSystem;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class ProfilingManager {
    private static boolean profiling = false;
    private static Profiler profiler = null;

    public static boolean isProfiling() {
        return profiling;
    }

    public static String checkCompatibility() {
        if (!ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-XX:+DebugNonSafepoints")) {
            return "You must have the flags '-XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints' to use the Arc profiler.";
        }

        if (!(System.getProperty("os.name").equalsIgnoreCase("linux") && System.getProperty("os.arch").equalsIgnoreCase("amd64"))) {
            return "You must be on Linux x86_64 to use the Arc profiler.";
        }

        if (Arc.class.getClassLoader().getResource("arcAsyncProfiler.so") == null) {
            return "Could not find the async profiler in the Arc jar.";
        }

        return "";
    }

    public static void start() throws Exception {
        if (profiling || profiler != null) {
            throw new IllegalStateException("A profile is already running!");
        }

        profiling = true;
        profiler = new Profiler();
        try {
            profiler.setup();
            profiler.start();
        } catch (Exception e) {
            stop(false);
            throw e;
        }
    }

    public static void stop() {
        stop(true);
    }

    public static void stop(boolean upload) {
        if (!profiling || profiler == null) {
            throw new IllegalStateException("A profile is not currently running!");
        }

        profiler.stop();

        if (upload) {
            SystemInfo system = new SystemInfo();
            HardwareAbstractionLayer hardware = system.getHardware();
            OperatingSystem os = system.getOperatingSystem();

            CentralProcessor processor = hardware.getProcessor();
            CentralProcessor.ProcessorIdentifier processorIdentifier = processor.getProcessorIdentifier();

            GlobalMemory memory = hardware.getMemory();
            VirtualMemory virtualMemory = memory.getVirtualMemory();

            ArcProto.Profile.MinecraftInfo.OnlineMode onlineMode = Bukkit.getOnlineMode()
                    ? ArcProto.Profile.MinecraftInfo.OnlineMode.ENABLED : ArcProto.Profile.MinecraftInfo.OnlineMode.DISABLED;
            if (onlineMode == ArcProto.Profile.MinecraftInfo.OnlineMode.DISABLED) {
                if (SpigotConfig.bungee && PaperConfig.bungeeOnlineMode) {
                    onlineMode = ArcProto.Profile.MinecraftInfo.OnlineMode.BUNGEE;
                }

                if (PaperConfig.velocitySupport && PaperConfig.velocityOnlineMode) {
                    onlineMode = ArcProto.Profile.MinecraftInfo.OnlineMode.VELOCITY;
                }
            }

            List<ArcProto.Profile.MinecraftInfo.Config> configs = new ArrayList<>();
            for (String config : ServerConfigs.allConfigs) {
                try {
                    String contents = ServerConfigs.getConfig(config);
                    configs.add(ArcProto.Profile.MinecraftInfo.Config.newBuilder()
                            .setFile(config)
                            .setContent(contents)
                            .build()
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.severe("Failed to get contents of " + config);
                } catch (IllegalArgumentException ignored) {
                }
            }

            List<ArcProto.Profile.MinecraftInfo.Plugin> plugins = new ArrayList<>();
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                plugins.add(ArcProto.Profile.MinecraftInfo.Plugin.newBuilder()
                        .setName(plugin.getDescription().getName())
                        .setVersion(plugin.getDescription().getVersion())
                        .setAuthor(String.join(", ", plugin.getDescription().getAuthors()))
                        .build()
                );
            }

            ArcProto.Profile profile = ArcProto.Profile.newBuilder()
                    .setSystem(ArcProto.Profile.SystemInfo.newBuilder()
                            .setVm(ArcProto.Profile.SystemInfo.VMInfo.newBuilder()
                                    .setVersion(System.getProperty("java.version"))
                                    .setVendor(System.getProperty("java.vendor"))
                                    .setVm(System.getProperty("java.vm.name"))
                                    .setRuntimeName(System.getProperty("java.runtime.name"))
                                    .setRuntimeVersion(System.getProperty("java.runtime.version"))
                                    .addAllFlags(ManagementFactory.getRuntimeMXBean().getInputArguments())
                                    .build()
                            )
                            .setCpu(ArcProto.Profile.SystemInfo.CPU.newBuilder()
                                    .setModel(processorIdentifier.getName())
                                    .setCores(processor.getPhysicalProcessorCount())
                                    .setThreads(processor.getLogicalProcessorCount())
                                    .setFrequency(processor.getMaxFreq())
                                    .build()
                            )
                            .setMemory(ArcProto.Profile.SystemInfo.Memory.newBuilder()
                                    .setPhysical(memory.getTotal())
                                    .setSwap(virtualMemory.getSwapTotal())
                                    .setTotal(virtualMemory.getVirtualMax())
                                    .build()
                            )
                            .setOs(ArcProto.Profile.SystemInfo.OS.newBuilder()
                                    .setManufacturer(os.getManufacturer())
                                    .setFamily(os.getFamily())
                                    .setVersion(os.getVersionInfo().toString())
                                    .setBitness(os.getBitness())
                                    .build()
                            ).build()
                    )
                    .setMinecraft(ArcProto.Profile.MinecraftInfo.newBuilder()
                            .setVersion(ArcProto.Profile.MinecraftInfo.Version.newBuilder()
                                    .setFull(Bukkit.getVersion())
                                    .setApi(Bukkit.getBukkitVersion())
                                    .setMc(Bukkit.getMinecraftVersion())
                                    .build()
                            )
                            .setOnlineMode(onlineMode)
                            .addAllConfigs(configs)
                            .addAllPlugins(plugins)
                            .build()
                    )
                    .build();

            upload(profile);
        }

        profiler = null;
        profiling = false;
    }

    private static void upload(ArcProto.Profile profile) {

    }
}
