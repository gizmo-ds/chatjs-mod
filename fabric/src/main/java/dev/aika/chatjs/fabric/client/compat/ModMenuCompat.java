package dev.aika.chatjs.fabric.client.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.aika.chatjs.client.MissingClothConfigScreen;
import dev.aika.chatjs.compat.ChatJSClothConfig;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (Platform.isModLoaded("cloth-config2")) return ChatJSClothConfig.ConfigScreen(parent);
            return new MissingClothConfigScreen(parent);
        };
    }
}
