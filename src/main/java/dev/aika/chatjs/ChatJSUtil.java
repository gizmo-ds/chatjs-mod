package dev.aika.chatjs;

import dev.aika.chatjs.api.OpenAIProvider;
import dev.aika.chatjs.server.SecretManager;
import lombok.experimental.UtilityClass;

public final class ChatJSUtil {
    public static OpenAIProvider getProvider() {
        OpenAIProvider provider = OpenAIProvider.valueOf(ChatJS.CONFIG.get("provider"));
        if (provider == OpenAIProvider.Custom) {
            provider = OpenAIProvider.Custom.withCustomValues(
                    ChatJS.CONFIG.get("custom_provider.model"),
                    ChatJS.CONFIG.get("custom_provider.base_url"),
                    ChatJS.CONFIG.get("custom_provider.models_path"),
                    ChatJS.CONFIG.get("custom_provider.chat_completion_path")
            );
        }
        return provider;
    }

    public static String getApiKey() {
        String apikey = SecretManager.INSTANCE.getProperty("apikey");
        if (apikey == null || apikey.isEmpty()) {
            ChatJS.LOGGER.warn("apikey not set");
            return null;
        }
        return apikey;
    }
}
