package dev.aika.chatjs.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import dev.aika.chatjs.ChatJS;
import dev.architectury.platform.Platform;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.*;
import java.nio.file.Files;

public class ChatJSConfig {
    private static final Logger log = ChatJS.LOGGER;
    private static final Marker marker = MarkerFactory.getMarker("ChatJSConfig");

    private final File configFile = new File(Platform.getConfigFolder().toFile(), ChatJS.MOD_ID + ".common.toml");
    private final TomlParser parser = new TomlParser();
    private final TomlWriter writer = new TomlWriter();
    private final CommentedConfig defaultConfig;
    private CommentedConfig config;

    public ChatJSConfig() {
        try (InputStream stream = getClass().getResourceAsStream("/assets/" + ChatJS.MOD_ID + "/default-config.toml")) {
            defaultConfig = parser.parse(stream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the default config", e);
        }
    }

    @SneakyThrows public ChatJSConfig load() {
        if (!configFile.exists()) save(defaultConfig);

        config = parser.parse(new FileReader(configFile));
        log.info("Loaded config");
        return this;
    }

    @SneakyThrows public void save() {
        if (config == null) {
            throw new IllegalStateException("Config is null");
        }
        save(config);
        log.info(marker, "Saved config");
    }

    @SneakyThrows private void save(CommentedConfig conf) {
        StringWriter sw = new StringWriter();
        writer.write(conf, sw);
        Files.write(configFile.toPath(), sw.toString().getBytes());
    }

    public <T> T get(String path) {
        if (defaultConfig != null && !config.contains(path)) return getDefault(path);
        if (config == null) return null;
        return config.get(path);
    }

    public <T> T getDefault(String path) {
        if (defaultConfig == null) return null;
        return defaultConfig.get(path);
    }

    public <T> void set(String path, T value) {
        if (config == null) return;
        config.set(path, value);
    }
}
