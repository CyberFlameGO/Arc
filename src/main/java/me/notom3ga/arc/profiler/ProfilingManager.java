package me.notom3ga.arc.profiler;

import me.notom3ga.arc.Arc;

import java.io.IOException;
import java.lang.management.ManagementFactory;

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

    public static void start() {
        if (profiling || profiler != null) {
            throw new IllegalStateException("A profile is already running!");
        }

        profiler = new Profiler();
        try {
            profiler.setup();
        } catch (IOException e) {
            e.printStackTrace();
            stop();
            return;
        }

        profiling = true;
    }

    public static void stop() {
        stop(true);
    }

    public static void stop(boolean upload) {
        if (!profiling || profiler == null) {
            throw new IllegalStateException("A profile is not currently running!");
        }

        if (upload) {
            ProfileExporter.exportProfile();
        }

        profiler = null;
        profiling = false;
    }
}
