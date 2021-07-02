package me.notom3ga.arc.profiler;

import me.notom3ga.arc.Arc;
import me.notom3ga.arc.profiler.monitor.CpuMonitor;
import me.notom3ga.arc.proto.ArcProto;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphTask extends BukkitRunnable {
    private final List<ArcProto.Profile.Graph.GraphData> data = new ArrayList<>();

    public List<ArcProto.Profile.Graph.GraphData> getData() {
        return this.data;
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();

        long[] times = ((CraftServer) Bukkit.getServer()).getServer().tickTimes5s.getTimes();
        double mspt = ((double) Arrays.stream(times).sum() / (double) times.length) * 1.0E-6D;

        this.data.add(ArcProto.Profile.Graph.GraphData.newBuilder()
                .setId("mspt")
                .setName("MSPT")
                .setTime(time)
                .setData(Math.round(mspt * 100D) / 100D)
                .build()
        );
        this.data.add(ArcProto.Profile.Graph.GraphData.newBuilder()
                .setId("tps")
                .setName("TPS")
                .setTime(time)
                .setData(Math.min(20, Math.round(Bukkit.getServer().getTPS()[0] * 100) / 100))
                .build()
        );
        this.data.add(ArcProto.Profile.Graph.GraphData.newBuilder()
                .setId("players")
                .setName("Player Count")
                .setTime(time)
                .setData(Bukkit.getOnlinePlayers().size())
                .build()
        );

        Bukkit.getScheduler().runTask(Arc.getInstance(), () -> {
            int entityCount = 0;
            int chunkCount = 0;
            int blockEntityCount = 0;
            for (World world : Bukkit.getWorlds()) {
                entityCount += world.getEntityCount();
                chunkCount += world.getChunkCount();
                blockEntityCount += world.getTileEntityCount();
            }

            this.data.add(ArcProto.Profile.Graph.GraphData.newBuilder()
                    .setId("entities")
                    .setName("Entity Count")
                    .setTime(time)
                    .setData(entityCount)
                    .build()
            );
            this.data.add(ArcProto.Profile.Graph.GraphData.newBuilder()
                    .setId("chunks")
                    .setName("Chunk Count")
                    .setTime(time)
                    .setData(chunkCount)
                    .build()
            );
            this.data.add(ArcProto.Profile.Graph.GraphData.newBuilder()
                    .setId("block_entities")
                    .setName("Block Entity Count")
                    .setTime(time)
                    .setData(blockEntityCount)
                    .build()
            );
        });

        this.data.add(ArcProto.Profile.Graph.GraphData.newBuilder()
                .setId("process_cpu")
                .setName("Process CPU")
                .setTime(time)
                .setData(CpuMonitor.getBean().getProcessCpuLoad())
                .build()
        );
        this.data.add(ArcProto.Profile.Graph.GraphData.newBuilder()
                .setId("system_cpu")
                .setName("System CPU")
                .setTime(time)
                .setData(CpuMonitor.getBean().getSystemCpuLoad())
                .build()
        );

        Runtime runtime = Runtime.getRuntime();
        this.data.add(ArcProto.Profile.Graph.GraphData.newBuilder()
                .setId("memory")
                .setName("Memory")
                .setTime(time)
                .setData(runtime.totalMemory() - runtime.freeMemory())
                .build()
        );
    }
}
