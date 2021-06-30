package me.notom3ga.arc.util.http;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class BytebinClient extends AbstractHttpClient {
    private final String url;
    private final String agent;

    public BytebinClient(OkHttpClient client, String url, String agent) {
        super(client);
        this.url = url + (url.endsWith("/") ? "" : "/");
        this.agent = agent;
    }

    public String postContent(byte[] buf, MediaType type) throws IOException {
        RequestBody body = RequestBody.create(type, buf);
        Request.Builder builder = new Request.Builder()
                .url(this.url + "post")
                .header("User-Agent", this.agent)
                .header("Content-Encoding", "gzip");
        Request request = builder.post(body).build();

        try (Response response = makeRequest(request)) {
            String key = response.header("Location");
            if (key == null) {
                throw new IllegalStateException("Key not returned");
            }
            return key;
        }
    }
}
