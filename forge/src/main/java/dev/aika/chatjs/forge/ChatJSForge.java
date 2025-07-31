package dev.aika.chatjs.forge;

import dev.aika.chatjs.ChatJS;
import dev.aika.chatjs.forge.compat.ClothConfigCompat;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ChatJS.MOD_ID)
public final class ChatJSForge {
    public ChatJSForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(ChatJS.MOD_ID, bus);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ChatJSForge::clientInit);
        ChatJS.init();
    }

    public static void clientInit() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(ClothConfigCompat::setup));
    }
}
