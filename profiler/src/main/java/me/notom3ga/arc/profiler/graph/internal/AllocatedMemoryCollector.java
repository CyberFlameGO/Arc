package me.notom3ga.arc.profiler.graph.internal;

import me.notom3ga.arc.profiler.graph.GraphCollector;
import me.notom3ga.arc.profiler.graph.constants.DataType;
import me.notom3ga.arc.profiler.graph.constants.Formats;

public class AllocatedMemoryCollector extends GraphCollector {

    public AllocatedMemoryCollector() {
        super("Allocated Memory", DataType.LONG, Formats.BYTES);
    }

    @Override
    public String report() {
        return String.valueOf(Runtime.getRuntime().totalMemory());
    }
}
