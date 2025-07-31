package dev.aika.chatjs;

import dev.aika.chatjs.config.ChatJSConfig;
import dev.aika.chatjs.server.ChatJSEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChatJS {
    public static final String MOD_ID = "chatjs";
    public static final String MOD_NAME = "ChatJS";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static ChatJSConfig CONFIG;

    public static void init() {
        CONFIG = new ChatJSConfig().load();
        ChatJSEventHandler.init();
    }
}
