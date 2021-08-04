package me.notom3ga.arc.profiler;

import me.notom3ga.arc.profiler.graph.GraphCollector;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

public interface ArcConfig {

    String url();

    String version();

    List<Path> configFiles();

    List<String> configHiddenTokens();

    LinkedHashMap<String, String> extraApplicationData();

    default int profilingInterval() {
        return 5;
    }

    int graphFrequency();

    List<GraphCollector> graphCollectors();
}
