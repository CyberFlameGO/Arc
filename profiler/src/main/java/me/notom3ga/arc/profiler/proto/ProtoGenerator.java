package me.notom3ga.arc.profiler.proto;

import me.notom3ga.arc.profiler.ArcConfig;
import me.notom3ga.arc.profiler.graph.GraphCollectors;
import me.notom3ga.arc.profiler.util.FileUtils;
import me.notom3ga.arc.profiler.util.StringUtils;
import me.notom3ga.arc.proto.Proto;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProtoGenerator {
    private static final SystemInfo system = new SystemInfo();

    public static Proto.Profile.Profiler generateProfiler(Path output, GraphCollectors collectors) {
        List<Proto.Profile.Profiler.Graph.Category> categories = new ArrayList<>();
        collectors.allCollectors().forEach(collector -> {
            List<Proto.Profile.Profiler.Graph.Category.DataPoint> dataPoints = new ArrayList<>();
            collector.data().forEach((time, data) -> dataPoints.add(Proto.Profile.Profiler.Graph.Category.DataPoint.newBuilder()
                    .setTime(time)
                    .setData(data)
                    .build()));

            categories.add(Proto.Profile.Profiler.Graph.Category.newBuilder()
                    .setName(collector.name())
                    .setDataType(collector.type())
                    .setFormat(collector.format())
                    .addAllData(dataPoints)
                    .build()
            );
        });

        return Proto.Profile.Profiler.newBuilder()
                .setGraph(Proto.Profile.Profiler.Graph.newBuilder()
                        .addAllCategories(categories)
                        .build()
                )
                .build();
    }

    public static Proto.Profile.Application generateApplication(ArcConfig config) {
        List<Proto.Profile.Application.Config> configs = new ArrayList<>();
        for (Path path : config.configFiles()) {
            try {
                configs.add(readConfig(path, config.configHiddenTokens()));
            } catch (FileNotFoundException ignore) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Proto.Profile.Application.newBuilder()
                .setUptime(ManagementFactory.getRuntimeMXBean().getUptime())
                .setVersion(config.version())
                .addAllConfigFiles(configs)
                .putAllExtras(config.extraApplicationData())
                .build();
    }

    private static Proto.Profile.Application.Config readConfig(Path path, List<String> hiddenTokens) throws IOException {
        if (!Files.exists(path) || Files.isRegularFile(path)) {
            throw new FileNotFoundException(path.toAbsolutePath() + " is not valid");
        }

        String contents;
        switch (FileUtils.fileExtension(path)) {
            case "properties":
            case "air": {
                StringBuilder builder = new StringBuilder();
                Files.lines(path, StandardCharsets.UTF_8).forEach(line -> {
                    if (!line.trim().startsWith("#") && StringUtils.containsNone(StringUtils.substringBefore(line.trim(), "=")
                            .trim(), hiddenTokens.toArray(String[]::new))) {
                        if (builder.length() != 0) {
                            builder.append("\n");
                        }
                        builder.append(line);
                    }
                });
                contents = builder.toString();
                break;
            }

            case "yml": {
                StringBuilder builder = new StringBuilder();
                Files.lines(path, StandardCharsets.UTF_8).forEach(line -> {
                    if (!line.trim().startsWith("#") && StringUtils.containsNone(StringUtils.substringBefore(line.trim(), ":")
                            .trim(), hiddenTokens.toArray(String[]::new))) {
                        if (builder.length() != 0) {
                            builder.append("\n");
                        }
                        builder.append(line);
                    }
                });
                contents = builder.toString();
                break;
            }

            default: {
                throw new IllegalArgumentException(FileUtils.fileExtension(path) + " is not a supported file type");
            }
        }

        return Proto.Profile.Application.Config.newBuilder()
                .setFile(path.getFileName().toString())
                .setContent(contents)
                .build();
    }

    public static Proto.Profile.OS generateOperatingSystem() {
        OperatingSystem os = system.getOperatingSystem();
        return Proto.Profile.OS.newBuilder()
                .setManufacturer(os.getManufacturer())
                .setFamily(os.getFamily())
                .setVersion(os.getVersionInfo().toString())
                .setBits(os.getBitness())
                .build();
    }

    public static Proto.Profile.Hardware generateHardware() {
        HardwareAbstractionLayer hardware = system.getHardware();

        CentralProcessor processor = hardware.getProcessor();
        GlobalMemory memory = hardware.getMemory();

        return Proto.Profile.Hardware.newBuilder()
                .setProcessor(Proto.Profile.Hardware.Processor.newBuilder()
                        .setModel(processor.getProcessorIdentifier().getName())
                        .setCores(processor.getPhysicalProcessorCount())
                        .setThreads(processor.getLogicalProcessorCount())
                        .setFrequency(processor.getMaxFreq())
                        .build()
                )
                .setMemory(Proto.Profile.Hardware.Memory.newBuilder()
                        .setPhysical(memory.getTotal())
                        .setSwap(memory.getVirtualMemory().getSwapTotal())
                        .build()
                )
                .build();
    }

    public static Proto.Profile.Java generateJava() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        List<Proto.Profile.Java.GarbageCollection> gcs = new ArrayList<>();
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            double averageTime = 0.0;
            long averageFrequency = 0;

            try {
                averageTime = (double) (gc.getCollectionTime() / gc.getCollectionCount());
                averageFrequency = (uptime - gc.getCollectionTime()) / gc.getCollectionCount();
            } catch (ArithmeticException ignore) {
            }

            gcs.add(Proto.Profile.Java.GarbageCollection.newBuilder()
                    .setName(gc.getName())
                    .setTotal(gc.getCollectionCount())
                    .setTime(averageTime)
                    .setFrequency(averageFrequency)
                    .build()
            );
        }

        return Proto.Profile.Java.newBuilder()
                .setJvm(Proto.Profile.Java.JVM.newBuilder()
                        .setVersion(System.getProperty("java.version"))
                        .setVendor(System.getProperty("java.vendor"))
                        .setVm(System.getProperty("java.vm.name"))
                        .setRuntimeName(System.getProperty("java.runtime.name"))
                        .setRuntimeVersion(System.getProperty("java.runtime.version"))
                        .addAllFlags(ManagementFactory.getRuntimeMXBean().getInputArguments())
                        .build()
                )
                .addAllGcs(gcs)
                .build();
    }
}
