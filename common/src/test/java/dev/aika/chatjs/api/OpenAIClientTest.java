package dev.aika.chatjs.api;

import dev.aika.chatjs.ChatJS;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;

import java.util.Map;

@SuppressWarnings("LoggingSimilarMessage")
public class OpenAIClientTest {
    private static final Logger log = ChatJS.LOGGER;

    @Test
    @EnabledIfEnvironmentVariable(named = "API_KEY", matches = ".+")
    public void testChatCompletion() {
        var apiKey = System.getenv("API_KEY");
        var client = new OpenAIClient()
                .setProvider(getProvider())
                .setApiKey(apiKey);
        Assertions.assertNotNull(client);

        Map<String, Object> body = Map.ofEntries(
                Map.entry("messages", new Map[]{
                        Map.of(
                                "role", "user",
                                "content", "Who are you?"
                        )
                })
        );
        var result = client.chat.createCompletion(body);
        Assertions.assertNotNull(result);
        log.info("result: {}", result);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "API_KEY", matches = ".+")
    public void testStreamChatCompletion() {
        var apiKey = System.getenv("API_KEY");
        var client = new OpenAIClient()
                .setProvider(getProvider())
                .setApiKey(apiKey);
        Assertions.assertNotNull(client);

        Map<String, Object> body = Map.ofEntries(
                Map.entry("messages", new Map[]{
                        Map.of(
                                "role", "user",
                                "content", "Who are you?"
                        )
                })
        );
        client.chat.createStreamCompletion(body,
                obj -> log.info("obj: {}", obj),
                err -> log.error("error", err));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "API_KEY", matches = ".+")
    public void testModels() {
        var apiKey = System.getenv("API_KEY");
        var provider = getProvider();
        var client = new OpenAIClient()
                .setProvider(provider)
                .setApiKey(apiKey);
        Assertions.assertNotNull(client);

        var result = client.models();
        Assertions.assertNotNull(result);
        log.info("result: {}", result);
    }

    private OpenAIProvider getProvider() {
        var p = System.getenv("PROVIDER");
        if (p.equals("Custom")) {
            return OpenAIProvider.Custom.withCustomValues(
                    System.getenv("PROVIDER_MODEL"),
                    System.getenv("PROVIDER_BASEURL")
            );
        } else if (!p.isEmpty()) {
            return OpenAIProvider.valueOf(p);
        }
        return OpenAIProvider.OpenAI;
    }
}
