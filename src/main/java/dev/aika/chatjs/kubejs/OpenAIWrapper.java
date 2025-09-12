package dev.aika.chatjs.kubejs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import dev.aika.chatjs.api.OpenAIClient;
import dev.aika.chatjs.api.OpenAIProvider;
import dev.aika.chatjs.api.resources.ModelObject;
import dev.latvian.mods.rhino.NativeObject;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class OpenAIWrapper {
    private static final OpenAIClient client = new OpenAIClient().setProvider(OpenAIProvider.OpenAI);
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @HideFromJS
    public static void setApiKey(String apiKey) {
        client.setApiKey(apiKey);
    }

    @HideFromJS
    public static void setClient(Consumer<OpenAIClient> consumer) {
        consumer.accept(client);
    }

    public static String currentProvider() {
        return client.getProvider().name();
    }

    public static String[] models() {
        List<ModelObject> list = client.models();
        return list.stream().map(v -> v.id).toArray(String[]::new);
    }

    public static class chat {
        private chat() {}

        public static JsonElement createCompletion(NativeObject body) {
            return client.chat.createCompletion(body);
        }

        public static JsonElement createCompletion(String body) {
            return client.chat.createCompletion(body);
        }
    }

    public static class deepseek {
        private deepseek() {}

        @SneakyThrows
        public static UserBalanceInfos userBalance() {
            HttpResponse<String> resp = client.send(client.getRequest()
                    .header("Accept", "application/json")
                    .uri(new URI("https://api.deepseek.com/user/balance")).GET()
                    .build());
            String body = resp.body();
            return gson.fromJson(body, UserBalanceInfos.class);
        }

        public record UserBalanceInfos(
                @SerializedName("is_available") Boolean isAvailable,
                @SerializedName("balance_infos") List<UserBalanceInfo> balanceInfos) {
            public record UserBalanceInfo(
                    String currency,
                    @SerializedName("total_balance") String totalBalance,
                    @SerializedName("granted_balance") String grantedBalance,
                    @SerializedName("topped_up_balance") String toppedUpBalance) {
            }
        }
    }
}