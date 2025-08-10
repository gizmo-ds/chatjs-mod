package dev.aika.chatjs.fabric.client.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.aika.chatjs.client.gui.MissingClothConfigScreen;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClothConfigCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (Platform.isModLoaded("cloth-config2"))
                return dev.aika.chatjs.compat.ClothConfigCompat.ConfigScreen(parent);
            return new MissingClothConfigScreen(parent);
        };
    }
}
