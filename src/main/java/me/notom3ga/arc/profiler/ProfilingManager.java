package me.notom3ga.arc.profiler;

import java.lang.management.ManagementFactory;

public class ProfilingManager {
    private static boolean profiling = false;

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

        return "";
    }

    public static void start() {
        if (profiling) {
            throw new IllegalStateException("A profile is already running!");
        }

        profiling = true;
    }

    public static void stop() {
        if (!profiling) {
            throw new IllegalStateException("A profile is not currently running!");
        }

        ProfileExporter.exportProfile();
        profiling = false;
    }
}
