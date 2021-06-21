package me.notom3ga.arc.profiler;

import me.notom3ga.arc.Arc;
import one.profiler.AsyncProfiler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Profiler {
    private AsyncProfiler profiler;

    public void setup() throws IOException {
        URL profilerResource = Arc.class.getClassLoader().getResource("arcAsyncProfiler.so");
        assert profilerResource != null;

        Path profilerPath = Files.createTempFile("arc-", "-asyncProfiler.so.tmp");
        profilerPath.toFile().deleteOnExit();

        try (InputStream stream = profilerResource.openStream()) {
            Files.copy(stream, profilerPath, StandardCopyOption.REPLACE_EXISTING);
        }

        profiler = AsyncProfiler.getInstance(profilerPath.toAbsolutePath().toString());
    }
}
