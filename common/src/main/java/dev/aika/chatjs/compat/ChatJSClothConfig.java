package dev.aika.chatjs.compat;

import dev.aika.chatjs.ChatJS;
import dev.aika.chatjs.ChatJSUtil;
import dev.aika.chatjs.api.OpenAIProvider;
import dev.aika.chatjs.kubejs.OpenAIWrapper;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

public class ChatJSClothConfig {
    private static final String SponsorUrl = "https://afdian.com/a/gizmo";

    public static Screen ConfigScreen(Screen parent) {
        final ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.chatjs.title"))
                .setSavingRunnable(ChatJSClothConfig::save);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        final ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.chatjs.general"));
        general.addEntry(sponsorDescription(entryBuilder));

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
        @SuppressWarnings("UnstableApiUsage") final var customProvider =
                entryBuilder.startSubCategory(
                                Component.translatable("config.chatjs.general.custom_provider")
                        )
                        .setDisplayRequirement(Requirement.isValue(provider, OpenAIProvider.Custom))
                        .setExpanded(true);
        customProvider.add(
                entryBuilder.startStrField(
                                Component.translatable("config.chatjs.general.custom_provider.model"),
                                ChatJS.CONFIG.get("custom_provider.model")
                        )
                        .setDefaultValue(OpenAIProvider.OpenAI.getDefaultModel())
                        .setSaveConsumer(value -> ChatJS.CONFIG.set("custom_provider.model", value))
                        .build()
        );
        customProvider.add(
                entryBuilder.startStrField(
                                Component.translatable("config.chatjs.general.custom_provider.base_url"),
                                ChatJS.CONFIG.get("custom_provider.base_url")
                        )
                        .setDefaultValue(OpenAIProvider.OpenAI.getBaseURL())
                        .setSaveConsumer(value -> ChatJS.CONFIG.set("custom_provider.base_url", value))
                        .build()
        );
        customProvider.add(
                entryBuilder.startStrField(
                                Component.translatable("config.chatjs.general.custom_provider.models_path"),
                                ChatJS.CONFIG.get("custom_provider.models_path")
                        )
                        .setDefaultValue("/models")
                        .setSaveConsumer(value -> ChatJS.CONFIG.set("custom_provider.models_path", value))
                        .build()
        );
        customProvider.add(
                entryBuilder.startStrField(
                                Component.translatable("config.chatjs.general.custom_provider.chat_completion_path"),
                                ChatJS.CONFIG.get("custom_provider.chat_completion_path")
                        )
                        .setDefaultValue("/chat/completions")
                        .setSaveConsumer(value -> ChatJS.CONFIG.set("custom_provider.chat_completion_path", value))
                        .build()
        );

        general.addEntry(customProvider.build());
        return builder.build();
    }

    private static void save() {
        ChatJS.CONFIG.save();
        OpenAIWrapper.setProvider(ChatJSUtil.getProvider());
    }

    public static TextListEntry sponsorDescription(ConfigEntryBuilder entryBuilder) {
        return entryBuilder.startTextDescription(
                Component.translatable("config.chatjs.sponsor.description",
                        Component.translatable("modmenu.nameTranslation.chatjs")
                                .withStyle(s -> s.withColor(ChatFormatting.AQUA).withBold(true)),
                        Component.translatable("config.chatjs.sponsor.description.afdian")
                                .withStyle(s -> s.withColor(ChatFormatting.DARK_PURPLE).withBold(true)
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(SponsorUrl)))
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, SponsorUrl)))
                )).build();
    }
}
