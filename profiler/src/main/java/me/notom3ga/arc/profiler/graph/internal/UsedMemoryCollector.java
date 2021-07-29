package me.notom3ga.arc.profiler.graph.internal;

import me.notom3ga.arc.profiler.graph.GraphCollector;
import me.notom3ga.arc.profiler.graph.constants.DataType;
import me.notom3ga.arc.profiler.graph.constants.Formats;

public class UsedMemoryCollector extends GraphCollector {

    public UsedMemoryCollector() {
        super("Used Memory", DataType.LONG, Formats.BYTES);
    }

    @Override
    public String report() {
        Runtime runtime = Runtime.getRuntime();
        return String.valueOf(runtime.totalMemory() - runtime.freeMemory());
    }
}
