package me.notom3ga.arc.profiler.graph.internal;

import com.sun.management.OperatingSystemMXBean;
import me.notom3ga.arc.profiler.graph.GraphCollector;
import me.notom3ga.arc.profiler.graph.constants.DataType;
import me.notom3ga.arc.profiler.graph.constants.Formats;

import java.lang.management.ManagementFactory;

public class ProcessCpuCollector extends GraphCollector {
    private final OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public ProcessCpuCollector() {
        super("Process CPU", DataType.DOUBLE, Formats.PERCENT);
    }

    @Override
    public String report() {
        return String.valueOf(bean.getProcessCpuLoad());
    }
}
