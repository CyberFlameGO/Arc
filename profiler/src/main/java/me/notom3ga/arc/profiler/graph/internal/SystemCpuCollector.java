package me.notom3ga.arc.profiler.graph.internal;

import me.notom3ga.arc.profiler.graph.GraphCollector;
import me.notom3ga.arc.profiler.graph.constants.DataType;
import me.notom3ga.arc.profiler.graph.constants.Formats;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class SystemCpuCollector extends GraphCollector {
    private final CentralProcessor processor = new SystemInfo().getHardware().getProcessor();

    public SystemCpuCollector() {
        super("System CPU", DataType.DOUBLE, Formats.PERCENT);
    }

    @Override
    public String report() {
        return String.valueOf(processor.getSystemLoadAverage(1)[0] / 100);
    }
}
