package me.notom3ga.arc.profiler;

import me.notom3ga.arc.Arc;
import one.profiler.AsyncProfiler;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Profiler {
    private Path output;
    private AsyncProfiler profiler;
    private long startTime = 0;

    public void setup() throws IOException {
        URL profilerResource = Arc.class.getClassLoader().getResource("arcAsyncProfiler.so");
        assert profilerResource != null;

        Path profilerPath = Files.createTempFile("arc-", "-asyncProfiler.so.tmp");
        profilerPath.toFile().deleteOnExit();

        try (InputStream stream = profilerResource.openStream()) {
            Files.copy(stream, profilerPath, StandardCopyOption.REPLACE_EXISTING);
        }

        profiler = AsyncProfiler.getInstance(profilerPath.toAbsolutePath().toString());
    }

    public void start() throws IOException {
        this.output = Files.createTempFile("arc-", "-output.jfr.tmp");
        this.output.toFile().deleteOnExit();

        String output = profiler.execute("start,event=wall,alloc=8192,interval=5ms,threads,filter,jstackdepth=1024,jfr,file=" + this.output.toAbsolutePath());
        profiler.addThread(((CraftServer) Bukkit.getServer()).getServer().serverThread);
        Thread.getAllStackTraces().keySet().forEach(profiler::addThread);

        if ((!output.contains("Started ") || !output.contains(" profiling")) && !output.contains("Profiling started")) {
            throw new IOException("Failed to start arc profiler: " + output.trim());
        }

        this.startTime = System.currentTimeMillis();
    }

    public void stop() {
        profiler.stop();
    }
}
