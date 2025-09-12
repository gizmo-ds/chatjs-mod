package dev.aika.chatjs.api;

import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.aika.chatjs.api.resources.ErrorResponse;
import dev.aika.chatjs.api.resources.ModelObject;
import dev.aika.chatjs.api.resources.ModelsResponse;
import dev.latvian.mods.kubejs.util.JSObjectType;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.kubejs.util.UtilsJS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.http.client.HttpResponseException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

@Accessors(chain = true)
@Setter
@Getter
public final class OpenAIClient {
    private OpenAIProvider provider = OpenAIProvider.OpenAI;
    private String model = provider.getDefaultModel();
    @Getter(AccessLevel.NONE)
    private String apiKey;
    private Double timeout = 10000d;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    public ChatAPI chat = new ChatAPI();
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private RateLimiter rl = RateLimiter.create(1000);

    public OpenAIClient setProvider(OpenAIProvider provider) {
        this.provider = provider;
        this.model = provider.getDefaultModel();
        return this;
    }

    public OpenAIClient setRPS(Double rps) {
        rl.setRate(rps);
        return this;
    }

    public HttpRequest.Builder getRequest() {
        return HttpRequest.newBuilder()
                .timeout(Duration.ofMillis((long) (timeout * 1000)))
                .header("Authorization", "Bearer " + apiKey);
    }

    @SneakyThrows
    public HttpResponse<String> send(HttpRequest req) {
        rl.acquire();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private <T> void checkError(HttpResponse<T> resp, String body) throws HttpResponseException {
        if (resp.statusCode() != 200) {
            String err;
            try {
                err = gson.fromJson(body, ErrorResponse.class).error().message();
            } catch (Exception e) {
                err = body;
            }
            throw new HttpResponseException(resp.statusCode(), err);
        }
    }

    private <T> void checkError(HttpResponse<T> resp) throws HttpResponseException {
        if (resp.statusCode() != 200) {
            throw new HttpResponseException(resp.statusCode(), "Failed to request");
        }
    }

    @SneakyThrows
    public List<ModelObject> models() {
        URI uri = new URI(provider.getBaseURL() + provider.getModelsPath());
        HttpResponse<String> response = httpClient.send(
                getRequest().uri(uri).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        checkError(response, body);
        ModelsResponse result = gson.fromJson(body, ModelsResponse.class);
        return result.data();
    }

    public class ChatAPI {
        private ChatAPI() {}

        @SneakyThrows
        public JsonElement createCompletion(Object body) {
            JsonObject obj = (JsonObject) gson.toJsonTree(body);
            return createCompletion(obj);
        }

        @SneakyThrows
        public JsonElement createCompletion(String body) {
            JsonObject obj = (JsonObject) JsonIO.parseRaw(body);
            return createCompletion(obj);
        }

        @SneakyThrows
        public JsonElement createCompletion(JsonObject body) {
            if (body.has("model")) body.remove("model");
            body.addProperty("model", model);
            if (body.has("stream")) body.remove("stream");
            body.addProperty("stream", false);
            String json = gson.toJson(body);
            URI uri = new URI(provider.getBaseURL() + provider.getChatCompletionPath());
            HttpResponse<String> response = httpClient.send(
                    getRequest()
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json; charset=utf-8")
                            .uri(uri).POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                            .build(),
                    HttpResponse.BodyHandlers.ofString());
            String _body = response.body();
            checkError(response, _body);
            return JsonIO.parseRaw(_body);
        }

        @SneakyThrows
        public void createStreamCompletion(Object body, Consumer<Object> onDataReceived, Consumer<Throwable> onError) {
            JsonObject obj = (JsonObject) gson.toJsonTree(body);
            if (obj.has("model")) obj.remove("model");
            obj.addProperty("model", model);
            if (obj.has("stream")) obj.remove("stream");
            obj.addProperty("stream", true);

            String json = gson.toJson(obj);
            URI uri = new URI(provider.getBaseURL() + provider.getChatCompletionPath());
            HttpResponse<Void> response = httpClient.send(
                    getRequest()
                            .header("Content-Type", "application/json; charset=utf-8")
                            .uri(uri).POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                            .build(),
                    HttpResponse.BodyHandlers.fromLineSubscriber(
                            new CompletionResponseSubscriber(onDataReceived, onError)
                    ));
            checkError(response);
        }

        public static class CompletionResponseSubscriber implements Flow.Subscriber<String> {
            private final Consumer<Object> onDataReceived;
            private final Consumer<Throwable> onError;
            private Flow.Subscription subscription;

            public CompletionResponseSubscriber(Consumer<Object> onDataReceived, Consumer<Throwable> onError) {
                this.onDataReceived = onDataReceived;
                this.onError = onError;
            }

            @Override public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1);
            }

            @Override public void onNext(String item) {
                try {
                    if (item.startsWith("data: ") && !item.contains("[DONE]")) {
                        String data = item.substring(6).trim();
                        if (!data.isEmpty()) {
                            onDataReceived.accept(UtilsJS.wrap(JsonIO.parseRaw(data), JSObjectType.MAP));
                        }
                    }
                    subscription.request(1);
                } catch (Exception e) {
                    onError.accept(e);
                    subscription.cancel();
                }
            }

            @Override public void onError(Throwable throwable) {
                onError.accept(throwable);
            }

            @Override public void onComplete() {}
        }
    }
}
