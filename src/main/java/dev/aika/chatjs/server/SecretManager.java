package dev.aika.chatjs.server;

import dev.aika.chatjs.ChatJS;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public enum SecretManager {
    INSTANCE;

    private static final Logger log = ChatJS.LOGGER;
    private static final Marker marker = MarkerFactory.getMarker("SecretManager");

    private final Lock lock = new ReentrantLock();
    private final Properties properties = new Properties();
    public File secretFile;

    public String getProperty(String key) {
        lock.lock();
        String value = properties.getProperty(key);
        if (value == null || value.isEmpty()) {
            log.warn(marker, "Secret {} is null or empty", key);
        }
        lock.unlock();
        return value;
    }

    public void setProperty(String key, String value) {
        lock.lock();
        properties.setProperty(key.trim(), value.trim());
        lock.unlock();
    }

    public void load(File file) throws IOException {
        lock.lock();
        secretFile = file;
        if (!secretFile.exists()) Files.createFile(secretFile.toPath());
        properties.load(new FileInputStream(secretFile));
        lock.unlock();
    }

    public void load() throws IOException {
        if (secretFile == null) {
            throw new IOException("Secret file is null");
        }
        load(secretFile);
    }

    public void save() throws IOException {
        if (secretFile == null) {
            throw new IOException("Secret file is null");
        }
        lock.lock();
        properties.store(new FileOutputStream(secretFile), null);
        properties.clear();
        lock.unlock();
    }
}
