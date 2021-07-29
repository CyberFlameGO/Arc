package me.notom3ga.arc.profiler.async;

import me.notom3ga.arc.profiler.Arc;
import me.notom3ga.arc.profiler.exception.ProfilerException;
import one.profiler.AsyncProfiler;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

public class AsyncProfilerIntegration {
    private static final String OS = "linux";
    private static final String ARCH = "amd64";

    private AsyncProfiler profiler;
    private Path outputFile;
    private boolean running;

    public List<String> load() throws IOException {
        String os = System.getProperty("os.name");
        String arch = System.getProperty("os.arch");
        if (!OS.equalsIgnoreCase(os) || !ARCH.equalsIgnoreCase(arch)) {
            throw new UnsupportedOperationException("Arc only supports Linux x86_64. Currently running: " + os + " " + arch);
        }

        Path profilerFile = Files.createTempFile("arc-", "-libasyncProfiler.so.tmp");
        profilerFile.toFile().deleteOnExit();

        try (InputStream stream = Objects.requireNonNull(Arc.class.getClassLoader().getResource("libasyncProfiler.so")).openStream()) {
            Files.copy(stream, profilerFile, StandardCopyOption.REPLACE_EXISTING);
        }

        profiler = AsyncProfiler.getInstance(profilerFile.toAbsolutePath().toString());

        if (!ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-XX:+DebugNonSafepoints")) {
            return List.of("Missing flags '-XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints'. Add these flags for optimal performance");
        }
        return List.of();
    }

    public void start(Arc arc) throws IOException, ProfilerException {
        if (!arc.isInitialized()) {
            throw new IllegalStateException("Arc has not been initialized.");
        }

        if (running) {
            throw new IllegalStateException("Arc profiler is already running.");
        }

        outputFile = Files.createTempFile("arc-", ".jfr.tmp");
        outputFile.toFile().deleteOnExit();

        String output = profiler.execute("start,event=itimer,alloc=8192,interval=" + arc.config().profilingInterval()
                + "ms,threads,filter,jstackdepth=1024,jfr,file=" + this.outputFile.toString());
        Thread.getAllStackTraces().keySet().forEach(profiler::addThread);

        if ((!output.contains("Started ") || !output.contains(" profiling")) && !output.contains("Profiling started")) {
            throw new ProfilerException("Failed to start the profiler: " + output.trim());
        }

        running = true;
    }

    public Path stop() {
        if (!running) {
            throw new IllegalStateException("Arc profiler is not currently running.");
        }

        running = false;
        profiler.stop();
        return this.outputFile;
    }
}
