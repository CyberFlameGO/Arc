package me.notom3ga.arc.util.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AbstractHttpClient {
    protected final OkHttpClient client;

    protected AbstractHttpClient(OkHttpClient client) {
        this.client = client;
    }

    protected Response makeRequest(Request request) throws IOException {
        Response response = this.client.newCall(request).execute();
        if (!response.isSuccessful()) {
            response.close();
            throw new RuntimeException("Request was unsuccessful: " + response.code() + " - " + response.message());
        }
        return response;
    }
}
