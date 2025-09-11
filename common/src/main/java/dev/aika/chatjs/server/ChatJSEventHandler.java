package dev.aika.chatjs.server;

import com.mojang.brigadier.CommandDispatcher;
import dev.aika.chatjs.ChatJS;
import dev.aika.chatjs.ChatJSUtil;
import dev.aika.chatjs.command.ChatJSCommands;
import dev.aika.chatjs.kubejs.OpenAIWrapper;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.io.IOException;

public class ChatJSEventHandler {
    private static final Logger log = ChatJS.LOGGER;
    private static final Marker marker = MarkerFactory.getMarker("ChatJSEventHandler");

    public static void init() {
        LifecycleEvent.SERVER_BEFORE_START.register(ChatJSEventHandler::serverStarting);
        LifecycleEvent.SERVER_STOPPING.register(ChatJSEventHandler::serverStopping);
        CommandRegistrationEvent.EVENT.register(ChatJSEventHandler::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ignoredRegistry, Commands.CommandSelection ignoredSelection) {
        ChatJSCommands.register(dispatcher);
    }

    private static void serverStarting(MinecraftServer server) {
        File secretFile = new File(server.getWorldPath(LevelResource.ROOT).toFile(), ChatJS.MOD_ID + ".secret.properties");
        try {
            SecretManager.INSTANCE.load(secretFile);
            clientInit();
        } catch (IOException e) {
            log.error(marker, "Failed to load secret file", e);
        }
    }

    private static void serverStopping(MinecraftServer server) {
        try {
            SecretManager.INSTANCE.save();
            OpenAIWrapper.setApiKey(null);
        } catch (IOException e) {
            log.error(marker, "Failed to save secret file", e);
        }
    }

    public static void clientInit() {
        OpenAIWrapper.setClient(client -> {
            client.setProvider(ChatJSUtil.getProvider())
                    .setApiKey(ChatJSUtil.getApiKey())
                    .setRPS(((Number) ChatJS.CONFIG.get("http_client.rps")).doubleValue())
                    .setTimeout(((Number) ChatJS.CONFIG.get("http_client.request_timeout")).doubleValue());
        });
    }
}
