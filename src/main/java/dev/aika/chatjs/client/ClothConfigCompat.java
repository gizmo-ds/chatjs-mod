package dev.aika.chatjs.client;

import dev.aika.chatjs.api.PlatformAPI;
import dev.aika.chatjs.client.gui.ClothConfigScreen;
import dev.aika.chatjs.client.gui.MissingClothConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;

public final class ClothConfigCompat {
    public static Screen setup(ModContainer ignoredModContainer, Screen parent) {
        if (PlatformAPI.isModLoaded("cloth_config")) return ClothConfigScreen.create(parent);
        else return new MissingClothConfigScreen(parent);
    }
}
