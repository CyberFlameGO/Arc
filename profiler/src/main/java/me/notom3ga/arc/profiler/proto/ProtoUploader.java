package me.notom3ga.arc.profiler.proto;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import me.notom3ga.arc.profiler.ArcConfig;
import me.notom3ga.arc.profiler.graph.GraphCollectors;
import me.notom3ga.arc.proto.Proto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.zip.GZIPOutputStream;

public class ProtoUploader {

    public static UploadResult upload(ArcConfig config, Path output, GraphCollectors collectors) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

        Proto.Profile profile = Proto.Profile.newBuilder()
                .setProfiler(ProtoGenerator.generateProfiler(output, collectors))
                .setApplication(ProtoGenerator.generateApplication(config))
                .setOperatingSystem(ProtoGenerator.generateOperatingSystem())
                .setHardware(ProtoGenerator.generateHardware())
                .setJava(ProtoGenerator.generateJava())
                .build();

        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try (OutputStream stream = new GZIPOutputStream(data)) {
            profile.writeTo(stream);
        }

        String response = client.send(HttpRequest.newBuilder()
                .uri(URI.create(config.url() + (config.url().endsWith("/") ? "" : "/") + "api/new"))
                .header("Content-Type", "application/x-arc-profiler")
                .header("User-Agent", "arc-profiler")
                .POST(HttpRequest.BodyPublishers.ofByteArray(data.toByteArray()))
                .build(), HttpResponse.BodyHandlers.ofString()).body();

        JsonObject json = Json.parse(response).asObject();
        return new UploadResult(json.getBoolean("error", true),
                json.getString("message", "The message/error value could not be found."));
    }

    public static class UploadResult {
        private final boolean error;
        private final String message;

        public UploadResult(boolean error, String message) {
            this.error = error;
            this.message = message;
        }

        public boolean hasError() {
            return this.error;
        }

        public String getMessage() {
            return this.message;
        }
    }
}
