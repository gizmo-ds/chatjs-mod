package dev.aika.chatjs;

import dev.aika.chatjs.server.ServerEvents;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ChatJS.MOD_ID)
public class ChatJS {
    public static final String MOD_ID = "chatjs";
    public static final String MOD_NAME = "ChatJS";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static ChatJSConfig CONFIG;

    public ChatJS() {
        CONFIG = new ChatJSConfig().load();

        NeoForge.EVENT_BUS.register(new ServerEvents());
    }
}
