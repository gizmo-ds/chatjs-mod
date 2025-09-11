package dev.aika.chatjs.forge.compat;

import dev.aika.chatjs.client.gui.MissingClothConfigScreen;
import dev.aika.chatjs.client.gui.ClothConfigScreen;
import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ClothConfigCompat {
    public static Screen setup(Minecraft ignoredMinecraft, Screen parent) {
        if (Platform.isModLoaded("cloth_config")) return ClothConfigScreen.create(parent);
        else return new MissingClothConfigScreen(parent);
    }
}
