package me.developer.chronocore.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataManager {
    private final File file;
    private final FileConfiguration config;

    public PlayerDataManager(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "PlayerData.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerNeededHours(UUID uuid, long time) {
        config.set("PlayerData.Players." + uuid.toString() + ".needed", time);
        save();
    }
    public void setAchievedSeconds(UUID uuid, long time) {
        config.set("PlayerData.Players." + uuid.toString() + ".achieved", time);
        save();
    }

    public long getPlayerNeededHours(UUID uuid) {
        return config.getLong("PlayerData.Players." + uuid.toString() + ".needed", 3601);
    }
    public long getAchievedSeconds(UUID uuid) {
        return config.getLong("PlayerData.Players." + uuid.toString() + ".achieved", 0);
    }

    public boolean hasPlayerTime(UUID uuid) {
        return config.contains("PlayerData.Players." + uuid + ".needed");
    }
}