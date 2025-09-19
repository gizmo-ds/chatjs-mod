package dev.aika.chatjs.client.gui;

import dev.aika.chatjs.ChatJS;
import dev.aika.chatjs.api.OpenAIProvider;
import dev.aika.chatjs.server.ServerEvents;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ClothConfigScreen {
    @SuppressWarnings("UnstableApiUsage")
    public static Screen create(Screen parent) {
        final ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.chatjs.title"))
                .setSavingRunnable(ClothConfigScreen::save);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        final ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.chatjs.general"));

        general.addEntry(
                entryBuilder.startBooleanToggle(
                                Component.translatable("config.chatjs.general.apikey_not_set_warning"),
                                ChatJS.CONFIG.get("apikey_not_set_warning")
                        )
                        .setDefaultValue(getDefault("apikey_not_set_warning"))
                        .setSaveConsumer(saveValue("apikey_not_set_warning"))
                        .build()
        );

        final EnumListEntry<OpenAIProvider> provider = entryBuilder.startEnumSelector(
                        Component.translatable("config.chatjs.general.provider"),
                        OpenAIProvider.class,
                        OpenAIProvider.valueOf(ChatJS.CONFIG.get("provider"))
                )
                .setDefaultValue(() -> OpenAIProvider.valueOf(ChatJS.CONFIG.getDefault("provider")))
                .setSaveConsumer(value -> ChatJS.CONFIG.set("provider", value.name()))
                .setEnumNameProvider(
                        v -> Component.translatableWithFallback("config.chatjs.general.provider.@" + v.name(), v.name())
                )
                .build();
        general.addEntry(provider);

        final var customProvider = entryBuilder.startSubCategory(
                        Component.translatable("config.chatjs.general.custom_provider")
                )
                .setDisplayRequirement(Requirement.isValue(provider, OpenAIProvider.Custom))
                .setExpanded(true);
        customProvider.add(createStrFieldEntry(entryBuilder, "custom_provider.model"));
        customProvider.add(createStrFieldEntry(entryBuilder, "custom_provider.base_url"));
        customProvider.add(createStrFieldEntry(entryBuilder, "custom_provider.models_path"));
        customProvider.add(createStrFieldEntry(entryBuilder, "custom_provider.chat_completion_path"));
        general.addEntry(customProvider.build());

        final ConfigCategory httpClient = builder.getOrCreateCategory(Component.translatable("config.chatjs.http_client"));
        httpClient.addEntry(entryBuilder.startDoubleField(
                        Component.translatable("config.chatjs.http_client.rps"),
                        Double.parseDouble(ChatJS.CONFIG.get("http_client.rps").toString())
                )
                .setDefaultValue(() -> Double.parseDouble(ChatJS.CONFIG.getDefault("http_client.rps").toString()))
                .setSaveConsumer(saveValue("http_client.rps"))
                .build());
        httpClient.addEntry(entryBuilder.startDoubleField(
                        Component.translatable("config.chatjs.http_client.request_timeout"),
                        Double.parseDouble(ChatJS.CONFIG.get("http_client.request_timeout").toString())
                )
                .setDefaultValue(() -> Double.parseDouble(ChatJS.CONFIG.getDefault("http_client.request_timeout").toString()))
                .setSaveConsumer(saveValue("http_client.request_timeout"))
                .build());

        return builder.build();
    }

    private static <T> Supplier<T> getDefault(String path) {
        return () -> ChatJS.CONFIG.getDefault(path);
    }

    private static <T> Consumer<T> saveValue(String path) {
        return value -> ChatJS.CONFIG.set(path, value);
    }

    private static StringListEntry createStrFieldEntry(ConfigEntryBuilder entryBuilder, String path) {
        return entryBuilder.startStrField(
                        Component.translatable("config.chatjs.general." + path),
                        ChatJS.CONFIG.get(path)
                )
                .setDefaultValue(getDefault(path))
                .setSaveConsumer(saveValue(path))
                .build();
    }

    private static void save() {
        ChatJS.CONFIG.save();
        ServerEvents.clientInit();
    }
}
