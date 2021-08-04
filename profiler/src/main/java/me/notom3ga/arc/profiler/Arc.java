package me.notom3ga.arc.profiler;

import me.notom3ga.arc.profiler.async.AsyncProfilerIntegration;
import me.notom3ga.arc.profiler.exception.ProfilerException;
import me.notom3ga.arc.profiler.graph.GraphCollectors;
import me.notom3ga.arc.profiler.proto.ProtoUploader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class Arc {
    private final ArcConfig config;
    private boolean initialized = false;
    private final AsyncProfilerIntegration profiler = new AsyncProfilerIntegration();
    private final GraphCollectors graph = new GraphCollectors();

    public Arc(ArcConfig config) {
        this.config = config;
    }

    public ArcConfig config() {
        return this.config;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public List<String> init() throws Exception {
        List<String> warnings = profiler.load();
        this.initialized = true;
        return warnings;
    }

    public void start() throws IOException, ProfilerException {
        profiler.start(this);
        graph.start(config);
    }

    public void stop() {
        try {
            stop(false);
        } catch (IOException | InterruptedException ignore) {
        }
    }

    public ProtoUploader.UploadResult stop(boolean upload) throws IOException, InterruptedException {
        graph.stop();
        Path output = profiler.stop();
        return upload ? ProtoUploader.upload(config, output, graph) : null; // todo - generate actual profile info
    }
}
