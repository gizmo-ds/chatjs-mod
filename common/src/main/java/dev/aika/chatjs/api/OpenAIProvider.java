package dev.aika.chatjs.api;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

@Getter
public enum OpenAIProvider {
    // https://platform.openai.com/docs/models
    OpenAI("gpt-4o-mini", "https://api.openai.com/v1"),
    // https://docs.x.ai/docs/models
    Grok("grok-3-mini", "https://api.x.ai/v1"),
    // Not recommended to use `deepseek-reasoner`
    DeepSeek("deepseek-chat", "https://api.deepseek.com/v1"),
    // https://www.volcengine.com/docs/82379/1330310
    DouBao("deepseek-v3-250324", "https://ark.cn-beijing.volces.com/api/v3"),
    // https://ai.google.dev/gemini-api/docs/models/gemini
    Gemini("gemini-2.0-flash", "https://generativelanguage.googleapis.com/v1beta/openai"),
    // https://platform.moonshot.cn/docs/pricing/chat
    Kimi("moonshot-v1-8k", "https://api.moonshot.cn/v1"),
    // https://help.aliyun.com/zh/model-studio/developer-reference/compatibility-of-openai-with-dashscope#7f9c78ae99pwz
    Qwen("qwen-turbo", "https://dashscope.aliyuncs.com/compatible-mode/v1"),
    Custom(null, null) {
        @Override
        public OpenAIProvider withCustomValues(@NotNull String model, @NonNull String baseURL) {
            return this.withCustomValues(model, baseURL, defaultModelsPath, defaultChatCompletionPath);
        }

        @Override
        public OpenAIProvider withCustomValues(@NonNull String model, @NonNull String baseURL,
                                               @NonNull String modelsPath, @NonNull String chatCompletionPath) {
            this.defaultModel = model;
            if (baseURL.endsWith("/")) baseURL = baseURL.substring(0, baseURL.length() - 1);
            this.baseURL = baseURL;
            if (!modelsPath.isEmpty()) this.modelsPath = modelsPath;
            if (!chatCompletionPath.isEmpty()) this.chatCompletionPath = chatCompletionPath;
            return this;
        }
    };

    private static final String defaultModelsPath = "/models";
    private static final String defaultChatCompletionPath = "/chat/completions";

    protected String baseURL;
    protected String defaultModel;
    protected String modelsPath;
    protected String chatCompletionPath;

    OpenAIProvider(String model, String baseURL) {
        this(model, baseURL, defaultModelsPath, defaultChatCompletionPath);
    }

    OpenAIProvider(String model, String baseURL, String modelsPath, String chatCompletionPath) {
        this.defaultModel = model;
        this.baseURL = baseURL;
        this.modelsPath = modelsPath;
        this.chatCompletionPath = chatCompletionPath;
    }

    public OpenAIProvider withCustomValues(@NonNull String model, @NonNull String baseURL) {
        throw new UnsupportedOperationException("Only Custom provider can be configured");
    }

    public OpenAIProvider withCustomValues(@NonNull String model, @NonNull String baseURL,
                                           @NonNull String modelsPath, @NonNull String chatCompletionPath) {
        throw new UnsupportedOperationException("Only Custom provider can be configured");
    }
}
