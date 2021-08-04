package me.notom3ga.arc.profiler.graph;

import me.notom3ga.arc.profiler.ArcConfig;
import me.notom3ga.arc.profiler.graph.internal.AllocatedMemoryCollector;
import me.notom3ga.arc.profiler.graph.internal.ProcessCpuCollector;
import me.notom3ga.arc.profiler.graph.internal.SystemCpuCollector;
import me.notom3ga.arc.profiler.graph.internal.UsedMemoryCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GraphCollectors {
    private final List<GraphCollector> collectors = new ArrayList<>();
    private final Timer timer = new Timer();
    private boolean running = false;

    public GraphCollectors() {
        collectors.add(new AllocatedMemoryCollector());
        collectors.add(new ProcessCpuCollector());
        collectors.add(new SystemCpuCollector());
        collectors.add(new UsedMemoryCollector());
    }

    public List<GraphCollector> allCollectors() {
        return this.collectors;
    }

    public void start(ArcConfig config) {
        if (running) {
            throw new IllegalStateException("Arc graph data is already running.");
        }

        collectors.addAll(config.graphCollectors());
        timer.schedule(new GraphDataTask(), 0, config.graphFrequency());
        running = true;
    }

    public void stop() {
        if (!running) {
            throw new IllegalStateException("Arc graph data is not currently running.");
        }

        running = false;
        timer.cancel();
    }

    private class GraphDataTask extends TimerTask {

        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            collectors.forEach(collector -> collector.data().put(currentTime, collector.report()));
        }
    }
}
