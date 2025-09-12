package dev.aika.chatjs;

import dev.aika.chatjs.client.ClothConfigCompat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ChatJS.MOD_ID, dist = Dist.CLIENT)
public final class ChatJSClient {
    public ChatJSClient(IEventBus ignoredEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ClothConfigCompat::setup);
    }
}
