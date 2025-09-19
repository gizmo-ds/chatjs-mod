package dev.aika.chatjs.server;

import dev.aika.chatjs.ChatJS;
import dev.aika.chatjs.ChatJSUtil;
import dev.aika.chatjs.command.ChatJSCommands;
import dev.aika.chatjs.kubejs.OpenAIWrapper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class ServerEvents {
    private static final Logger log = ChatJS.LOGGER;
    private static final Marker marker = MarkerFactory.getMarker("ServerEvents");

    public static void clientInit() {
        OpenAIWrapper.setClient(client -> client.setProvider(ChatJSUtil.getProvider())
                .setApiKey(ChatJSUtil.getApiKey())
                .setRPS(((Number) ChatJS.CONFIG.get("http_client.rps")).doubleValue())
                .setTimeout(((Number) ChatJS.CONFIG.get("http_client.request_timeout")).doubleValue()));
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ChatJSCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        File secretFile = new File(event.getServer().getWorldPath(LevelResource.ROOT).toFile(), ChatJS.MOD_ID + ".secret.properties");
        try {
            SecretManager.INSTANCE.load(secretFile);
            clientInit();
        } catch (IOException e) {
            log.error(marker, "Failed to load secret file", e);
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        try {
            SecretManager.INSTANCE.save();
            OpenAIWrapper.setApiKey(null);
        } catch (IOException e) {
            log.error(marker, "Failed to save secret file", e);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if ((boolean) ChatJS.CONFIG.get("apikey_not_set_warning") && SecretManager.INSTANCE.getProperty("apikey") == null) {
                // Check player permissions
                if (Objects.requireNonNull(player.getServer()).isSingleplayer() || player.hasPermissions(2)) {
                    player.sendSystemMessage(Component.translatable("message.chatjs.apikey_not_set_warning"));
                }
            }
        }
    }
}
