package me.notom3ga.arc.profiler;

import com.destroystokyo.paper.PaperConfig;
import com.google.common.collect.Lists;
import me.notom3ga.arc.Arc;
import me.notom3ga.arc.config.Config;
import me.notom3ga.arc.profiler.config.ServerConfigs;
import me.notom3ga.arc.proto.ArcProto;
import me.notom3ga.arc.util.Logger;
import me.notom3ga.arc.util.compat.Compatibility;
import me.notom3ga.arc.util.http.BytebinClient;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.spigotmc.SpigotConfig;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.VirtualMemory;
import oshi.software.os.OperatingSystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class ProfilingManager {
    private static boolean profiling = false;
    private static Profiler profiler = null;

    public static boolean isProfiling() {
        return profiling;
    }

    public static Compatibility checkCompatibility() {
        if (!(System.getProperty("os.name").equalsIgnoreCase("linux") && System.getProperty("os.arch").equalsIgnoreCase("amd64"))) {
            return Compatibility.OS;
        }

        if (Arc.class.getClassLoader().getResource("arcAsyncProfiler.so") == null) {
            return Compatibility.PROFILER_NOT_FOUND;
        }

        return Compatibility.COMPATIBLE;
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

    public static String stop() throws IOException {
        return stop(true);
    }

    public static String stop(boolean upload) throws IOException {
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

            ArcProto.Profile.Info.Server.OnlineMode onlineMode = Bukkit.getOnlineMode()
                    ? ArcProto.Profile.Info.Server.OnlineMode.ENABLED : ArcProto.Profile.Info.Server.OnlineMode.DISABLED;
            if (onlineMode == ArcProto.Profile.Info.Server.OnlineMode.DISABLED) {
                if (SpigotConfig.bungee && PaperConfig.bungeeOnlineMode) {
                    onlineMode = ArcProto.Profile.Info.Server.OnlineMode.BUNGEE;
                }

                if (PaperConfig.velocitySupport && PaperConfig.velocityOnlineMode) {
                    onlineMode = ArcProto.Profile.Info.Server.OnlineMode.VELOCITY;
                }
            }

            List<ArcProto.Profile.Info.Server.Config> configs = new ArrayList<>();
            for (String config : Config.CONFIGS) {
                try {
                    String contents = ServerConfigs.getConfig(config);
                    configs.add(ArcProto.Profile.Info.Server.Config.newBuilder()
                            .setFile(config)
                            .setContent(contents)
                            .build()
                    );
                } catch (IllegalArgumentException ignored) {
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.severe("Failed to get contents of " + config);
                }
            }

            List<ArcProto.Profile.Info.Server.Plugin> plugins = new ArrayList<>();
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                plugins.add(ArcProto.Profile.Info.Server.Plugin.newBuilder()
                        .setName(plugin.getDescription().getName())
                        .setVersion(plugin.getDescription().getVersion())
                        .setAuthor(String.join(", ", plugin.getDescription().getAuthors()))
                        .build()
                );
            }

            long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
            List<ArcProto.Profile.Info.Server.GC> gcs = new ArrayList<>();
            for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
                double averageTime = 0.0;
                long averageFrequency = 0;

                try {
                    averageTime = (double) (gc.getCollectionTime() / gc.getCollectionCount());
                    averageFrequency = (uptime - gc.getCollectionTime()) / gc.getCollectionCount();
                } catch (ArithmeticException ignore) {
                }

                gcs.add(ArcProto.Profile.Info.Server.GC.newBuilder()
                        .setName(gc.getName())
                        .setTotal(gc.getCollectionCount())
                        .setTime(averageTime)
                        .setFrequency(averageFrequency)
                        .build()
                );
            }

            ArcProto.Profile profile = ArcProto.Profile.newBuilder()
                    .setProfiler(ArcProto.Profile.Profiler.newBuilder()
                            .setMemory(ArcProto.Profile.Profiler.Memory.newBuilder()
                                    .setDebugSymbols(profiler.hasDebugSymbols())
                                    .build()
                            )
                            .build()
                    )
                    .setInfo(ArcProto.Profile.Info.newBuilder()
                            .setSystem(ArcProto.Profile.Info.System.newBuilder()
                                    .setCpu(ArcProto.Profile.Info.System.CPU.newBuilder()
                                            .setModel(processorIdentifier.getName())
                                            .setCores(processor.getPhysicalProcessorCount())
                                            .setThreads(processor.getLogicalProcessorCount())
                                            .setFrequency(processor.getMaxFreq())
                                            .build()
                                    )
                                    .setMemory(ArcProto.Profile.Info.System.Memory.newBuilder()
                                            .setPhysical(memory.getTotal())
                                            .setSwap(virtualMemory.getSwapTotal())
                                            .setVirtual(virtualMemory.getVirtualMax())
                                            .build()
                                    )
                                    .setOs(ArcProto.Profile.Info.System.OS.newBuilder()
                                            .setManufacturer(os.getManufacturer())
                                            .setFamily(os.getFamily())
                                            .setVersion(os.getVersionInfo().toString())
                                            .setBitness(os.getBitness())
                                            .build()
                                    )
                                    .build()
                            )
                            .setServer(ArcProto.Profile.Info.Server.newBuilder()
                                    .setUptime(uptime)
                                    .setVersion(Bukkit.getVersion())
                                    .setOnlineMode(onlineMode)
                                    .addAllConfigs(configs)
                                    .addAllPlugins(plugins)
                                    .addAllGcs(gcs)
                                    .build()
                            )
                            .setJava(ArcProto.Profile.Info.Java.newBuilder()
                                    .setVersion(System.getProperty("java.version"))
                                    .setVendor(System.getProperty("java.vendor"))
                                    .setVm(System.getProperty("java.vm.name"))
                                    .setRuntimeName(System.getProperty("java.runtime.name"))
                                    .setRuntimeVersion(System.getProperty("java.runtime.version"))
                                    .addAllFlags(ManagementFactory.getRuntimeMXBean().getInputArguments())
                                    .build()
                            )
                            .build()
                    )
                    .build();

            return upload(profile);
        }

        profiler = null;
        profiling = false;
        return null;
    }

    private static final BytebinClient bytebin = new BytebinClient(new OkHttpClient(), "https://bytebin.lucko.me/", "Arc-Profiler");
    private static final MediaType type = MediaType.parse("application/x-arc-profiler");

    private static String upload(ArcProto.Profile profile) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (OutputStream out = new GZIPOutputStream(byteOut)) {
            profile.writeTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bytebin.postContent(byteOut.toByteArray(), type);
    }
}
