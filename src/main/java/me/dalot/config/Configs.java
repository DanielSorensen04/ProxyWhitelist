package me.dalot.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import lombok.Getter;
import me.dalot.ProxyWhitelist;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Configs {

    @Getter
    private static ConfigModel config;
    @Getter
    private static Set<UUID> whitelisted = new HashSet<>();
    private static Path configFile;
    private static Path whitelistFile;

    public static void loadConfigs(ProxyWhitelist proxyWhitelist) {
        configFile = Path.of(proxyWhitelist.getDataDirectory() + "/config.toml");
        whitelistFile = Path.of(proxyWhitelist.getDataDirectory() + "/whitelisted.json");

        if (!proxyWhitelist.getDataDirectory().toFile().exists()) {
            proxyWhitelist.getDataDirectory().toFile().mkdir();
        }

        if (!configFile.toFile().exists()) {
            try (InputStream in = ProxyWhitelist.class.getResourceAsStream("/config.toml")) {
                Files.copy(in, configFile);
            } catch (Exception e) {
                proxyWhitelist.getLogger().error("Fejl ved loading af config.toml");
                e.printStackTrace();
            }
        }
        config = new Toml().read(configFile.toFile()).to(ConfigModel.class);

        if (whitelistFile.toFile().exists()) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(whitelistFile.toFile()), "UTF8")) {
                Type whitelistSetType = new TypeToken<HashSet<UUID>>() {
                }.getType();
                whitelisted = new Gson().fromJson(inputStreamReader, whitelistSetType);
            } catch (Exception e) {
                proxyWhitelist.getLogger().error("Fejl ved loading af whitelisted.json");
                e.printStackTrace();
            }
        }
    }

    public static void saveConfig(ProxyWhitelist proxyWhitelist) {
        try {
            new TomlWriter().write(config, configFile.toFile());
        } catch (Exception e) {
            proxyWhitelist.getLogger().error("Fejl ved lagring af config.toml");
            e.printStackTrace();
        }
    }

    public static void saveWhitelist(ProxyWhitelist proxyWhitelist) {
        try {
            FileWriter fileWriter = new FileWriter(whitelistFile.toFile());
            new Gson().toJson(whitelisted, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            proxyWhitelist.getLogger().error("Fejl ved lagring af whitelisted.json");
            e.printStackTrace();
        }
    }

}
