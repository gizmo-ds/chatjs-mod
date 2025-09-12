package dev.aika.chatjs.api;

import lombok.experimental.UtilityClass;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

@UtilityClass
public final class PlatformAPI {
    public boolean isModLoaded(String modId) {
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }

    public Path getConfigFolder() {
        return FMLPaths.CONFIGDIR.get();
    }
}
