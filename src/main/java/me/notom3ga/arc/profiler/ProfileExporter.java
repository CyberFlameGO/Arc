package me.notom3ga.arc.profiler;

import com.destroystokyo.paper.PaperConfig;
import com.google.protobuf.util.JsonFormat;
import me.notom3ga.arc.Arc;
import me.notom3ga.arc.profiler.config.ServerConfigs;
import me.notom3ga.arc.proto.ArcProto;
import me.notom3ga.arc.util.Logger;
import org.bukkit.Bukkit;
import org.spigotmc.SpigotConfig;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.VirtualMemory;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileExporter {

    public static void exportProfile() {
        SystemInfo system = new SystemInfo();
        HardwareAbstractionLayer hardware = system.getHardware();
        OperatingSystem os = system.getOperatingSystem();

        CentralProcessor processor = hardware.getProcessor();
        CentralProcessor.ProcessorIdentifier processorIdentifier = processor.getProcessorIdentifier();

        GlobalMemory memory = hardware.getMemory();
        VirtualMemory virtualMemory = memory.getVirtualMemory();

        ArcProto.Profile.MCInfo.OnlineMode onlineMode = Bukkit.getOnlineMode() ? ArcProto.Profile.MCInfo.OnlineMode.ENABLED : ArcProto.Profile.MCInfo.OnlineMode.DISABLED;
        if (onlineMode == ArcProto.Profile.MCInfo.OnlineMode.DISABLED) {
            if (SpigotConfig.bungee && PaperConfig.bungeeOnlineMode) {
                onlineMode = ArcProto.Profile.MCInfo.OnlineMode.BUNGEE;
            }

            if (PaperConfig.velocitySupport && PaperConfig.velocityOnlineMode) {
                onlineMode = ArcProto.Profile.MCInfo.OnlineMode.VELOCITY;
            }
        }

        List<ArcProto.Profile.MCInfo.Config> configs = new ArrayList<>();
        for (String config : ServerConfigs.allConfigs) {
            try {
                String contents = ServerConfigs.getConfig(config);
                configs.add(ArcProto.Profile.MCInfo.Config.newBuilder()
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
                .setMinecraft(ArcProto.Profile.MCInfo.newBuilder()
                        .setVersion(ArcProto.Profile.MCInfo.Version.newBuilder()
                                .setFull(Bukkit.getVersion())
                                .setApi(Bukkit.getBukkitVersion())
                                .setMc(Bukkit.getMinecraftVersion())
                                .build()
                        )
                        .setOnlineMode(onlineMode)
                        .addAllConfigs(configs)
                        .build()
                )
                .build();

        File file = new File(Arc.getInstance().getDataFolder(), "profile-" + UUID.randomUUID());
        try {
            file.createNewFile();
            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(JsonFormat.printer().print(profile).getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
