package dev.aika.chatjs.forge.compat;

import dev.aika.chatjs.client.MissingClothConfigScreen;
import dev.aika.chatjs.compat.ChatJSClothConfig;
import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ClothConfigCompat {
    public static Screen setup(Minecraft ignoredMinecraft, Screen parent) {
        if (Platform.isModLoaded("cloth_config")) return ChatJSClothConfig.ConfigScreen(parent);
        else return new MissingClothConfigScreen(parent);
    }
}
