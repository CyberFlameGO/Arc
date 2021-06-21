package me.notom3ga.arc.profiler;

public class ProfilingManager {
    private static boolean profiling = false;

    public static boolean isProfiling() {
        return profiling;
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
