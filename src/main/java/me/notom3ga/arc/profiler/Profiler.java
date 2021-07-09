package me.notom3ga.arc.profiler;

import me.notom3ga.arc.Arc;
import me.notom3ga.arc.proto.ArcProto;
import me.notom3ga.arc.util.Logger;
import me.notom3ga.arc.profiler.async.AsyncProfiler;
import me.notom3ga.arc.profiler.async.Feature;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class Profiler {
    private Path output;
    private AsyncProfiler profiler;
    private boolean debugSymbols = false;

    public Path getOutput() {
        return this.output;
    }

    public boolean hasDebugSymbols() {
        return this.debugSymbols;
    }

    public void setup() throws IOException {
        URL profilerResource = Arc.class.getClassLoader().getResource("arcAsyncProfiler.so");
        assert profilerResource != null;

        Path profilerPath = Files.createTempFile("arc-", "-asyncProfiler.so.tmp");
        profilerPath.toFile().deleteOnExit();

        try (InputStream stream = profilerResource.openStream()) {
            Files.copy(stream, profilerPath, StandardCopyOption.REPLACE_EXISTING);
        }

        profiler = AsyncProfiler.getInstance(profilerPath.toAbsolutePath().toString());
        debugSymbols = profiler.check(Feature.DEBUG_SYMBOLS);
    }

    public void start() throws IOException {
        this.output = Files.createTempFile("arc-", "-output.jfr.tmp");
        this.output.toFile().deleteOnExit();

        if (!debugSymbols) {
            Logger.warn("Debug symbols not found, memory profiling disabled.");
        }

        String alloc = debugSymbols ? "alloc=8192," : "";
        String output = profiler.execute("start,event=wall," + alloc + "interval=5ms,threads,filter,jstackdepth=1024,jfr,file=" + this.output.toAbsolutePath());
        profiler.addThread(((CraftServer) Bukkit.getServer()).getServer().serverThread);
        Thread.getAllStackTraces().keySet().forEach(profiler::addThread);

        if ((!output.contains("Started ") || !output.contains(" profiling")) && !output.contains("Profiling started")) {
            throw new IOException("Failed to start arc profiler: " + output.trim());
        }
    }

    public void stop() {
        profiler.stop();
    }
}
