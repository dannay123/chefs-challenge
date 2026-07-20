package com.chefchallenge.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.chefchallenge.client.model.*;

import java.net.URI;
import java.net.http.*;
import java.util.List;

public class ApiClient {

    private static final String BASE = "http://localhost:8080/api/session";
    private final HttpClient   http   = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public StartSessionResponse startSession() throws Exception {
        HttpRequest req = post(BASE + "/start", "");
        return send(req, StartSessionResponse.class);
    }

    public HintResponse requestHint(String sessionId, String hintType) throws Exception {
        String body = mapper.writeValueAsString(new HintPayload(hintType));
        HttpRequest req = post(BASE + "/" + sessionId + "/hint", body);
        return send(req, HintResponse.class);
    }

    public SubmitResponse submitDish(String sessionId, List<String> ingredients,
                                     List<String> techniques) throws Exception {
        String body = mapper.writeValueAsString(new SubmitPayload(ingredients, techniques));
        HttpRequest req = post(BASE + "/" + sessionId + "/submit", body);
        return send(req, SubmitResponse.class);
    }

    public SubmitResponse sendTimeout(String sessionId) throws Exception {
        HttpRequest req = post(BASE + "/" + sessionId + "/timeout", "");
        return send(req, SubmitResponse.class);
    }

    private HttpRequest post(String url, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(body.isEmpty()
                        ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
    }

    private <T> T send(HttpRequest req, Class<T> cls) throws Exception {
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 400)
            throw new Exception("Server error " + resp.statusCode() + ": " + resp.body());
        return mapper.readValue(resp.body(), cls);
    }

    private static class HintPayload {
        public String hintType;
        HintPayload(String h) { this.hintType = h; }
    }

    private static class SubmitPayload {
        public List<String> ingredients;
        public List<String> techniques;
        SubmitPayload(List<String> i, List<String> t) { this.ingredients = i; this.techniques = t; }
    }
}
