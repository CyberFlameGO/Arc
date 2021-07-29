package me.notom3ga.arc.profiler.graph;

import java.util.HashMap;
import java.util.Map;

public abstract class GraphCollector  {
    private final String name;
    private final String type;
    private final String format;
    private final Map<Long, String> data = new HashMap<>();

    public GraphCollector(String name, String type, String format) {
        this.name = name;
        this.type = type;
        this.format = format;
    }

    public String name() {
        return this.name;
    }

    public String type() {
        return this.type;
    }

    public String format() {
        return this.format;
    }

    public Map<Long, String> data() {
        return this.data;
    }

    public abstract String report();
}
