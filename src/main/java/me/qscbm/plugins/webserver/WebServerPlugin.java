package me.qscbm.plugins.webserver;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class WebServerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("content-type.json",false);
        fileConfiguration = getConfig();
        contentTypesFile = getDataFolder().toPath().resolve("content-type.json").toFile();
        INSTANCE = this;
        qsWebServer = new QsWebServer(getConfiguration().getInt("Settings.port"));
        try {
            qsWebServer.start();
            String path = getConfiguration().getString("Settings.path");
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        getLogger().info("WebServer已启动");
    }

    public static QsWebServer qsWebServer;

    private static FileConfiguration fileConfiguration;

    public static FileConfiguration getConfiguration() {
        return fileConfiguration;
    }

    private static File contentTypesFile;

    public static File getContentTypesFile() {
        return contentTypesFile;
    }
    @Override
    public void onDisable() {
        getLogger().info("WebServer已关闭");
    }

    private static WebServerPlugin INSTANCE;

    public static WebServerPlugin getInstance() {
        return INSTANCE;
    }
}
