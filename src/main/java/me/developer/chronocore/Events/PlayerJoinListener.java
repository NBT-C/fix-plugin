package me.developer.chronocore.Events;

import lombok.Getter;
import me.developer.chronocore.ChronoCore;
import me.developer.chronocore.Utils.ColorUtils;
import me.developer.chronocore.Utils.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final PlayerDataManager playerDataManager;
    private final Map<UUID, Long> playerTimers = new HashMap<>();
    private final ChronoCore plugin;

    public PlayerJoinListener(PlayerDataManager playerDataManager, ChronoCore plugin) {
        this.playerDataManager = playerDataManager;
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String prefix = plugin.prefix;
        FileConfiguration config = plugin.getConfig();

        int defaultNeededTime = config.getInt("Timer_System.Default_Timer", 1) * 60 * 60;

        if (!playerDataManager.hasPlayerTime(uuid)) {
            playerDataManager.getPlayerNeededHours(uuid);
            playerDataManager.setAchievedSeconds(uuid, 0);
            playerTimers.put(uuid, (long) defaultNeededTime);
            playerDataManager.setPlayerNeededHours(uuid, defaultNeededTime);

            String firstJoinMessage = config.getString("Timer_System.First_Join_Message.Message")
                    .replace("%prefix%", prefix)
                    .replace("%time%", String.valueOf(defaultNeededTime / 3600));

            if (config.getBoolean("Timer_System.First_Join_Message.Enable", true)) {
                player.sendMessage(ColorUtils.translateColors(firstJoinMessage));
            }
        } else {
            long neededTime = playerDataManager.getPlayerNeededHours(uuid);
            long achivedTime = playerDataManager.getAchievedSeconds(uuid);

            playerTimers.put(uuid, neededTime);

            String normalJoinMessage = config.getString("Timer_System.Normal_Join_Message.Message")
                    .replace("%prefix%", prefix)
                    .replace("%time-remaining%", String.valueOf((neededTime - achivedTime) / 60));

            if (config.getBoolean("Timer_System.Normal_Join_Message.Enable", true)) {
                player.sendMessage(ColorUtils.translateColors(normalJoinMessage));
            }
        }

        ChronoCore.getInstance().getJoinTimes().put(uuid, System.currentTimeMillis());
        ChronoCore.getInstance().getJoinTimesFixed().put(uuid, System.currentTimeMillis() - (1000*playerDataManager.getAchievedSeconds(uuid)));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (ChronoCore.getInstance().getJoinTimes().containsKey(uuid)) {
            long joinTime = ChronoCore.getInstance().getJoinTimes().get(uuid);
            long timePlayed = (System.currentTimeMillis() - joinTime) / 1000;
            long achivedTime = playerDataManager.getAchievedSeconds(uuid);

            playerDataManager.setAchievedSeconds(uuid, achivedTime + timePlayed);
            playerTimers.remove(uuid);
            ChronoCore.getInstance().getJoinTimes().remove(uuid);

            playerDataManager.setAchievedSeconds(uuid, achivedTime + timePlayed);
        }
    }
}