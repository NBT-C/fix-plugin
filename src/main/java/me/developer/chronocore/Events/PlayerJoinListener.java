package me.developer.chronocore.Events;

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

    public Map<UUID, Long> getPlayerTimers() {
        return playerTimers;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String prefix = plugin.prefix;
        FileConfiguration config = plugin.getConfig();

        int defaultTimer = config.getInt("Timer_System.Default_Timer", 12) * 60 * 60;

        if (!playerDataManager.hasPlayerTime(uuid)) {
            playerDataManager.setPlayerTime(uuid, defaultTimer);
            playerTimers.put(uuid, (long) defaultTimer);
            String firstJoinMessage = config.getString("Timer_System.First_Join_Message.Message")
                    .replace("%prefix%", prefix)
                    .replace("%time%", String.valueOf(defaultTimer / 3600));
            if (config.getBoolean("Timer_System.First_Join_Message.Enable", true)) {
                player.sendMessage(ColorUtils.translateColors(firstJoinMessage));
            }
        } else {
            long remainingTime = playerDataManager.getPlayerTime(uuid);
            playerTimers.put(uuid, remainingTime);
            String normalJoinMessage = config.getString("Timer_System.Normal_Join_Message.Message")
                    .replace("%prefix%", prefix)
                    .replace("%time-remaining%", String.valueOf(remainingTime / 60));
            if (config.getBoolean("Timer_System.Normal_Join_Message.Enable", true)) {
                player.sendMessage(ColorUtils.translateColors(normalJoinMessage));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (playerTimers.containsKey(uuid)) {
            playerDataManager.setPlayerTime(uuid, playerTimers.get(uuid));
            playerTimers.remove(uuid);
        }
    }

    public void startTimerTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            synchronized (playerTimers) {
                for (UUID uuid : playerTimers.keySet()) {
                    long remainingTime = playerTimers.get(uuid);

                    if (remainingTime > 0) {
                        playerTimers.put(uuid, remainingTime - 1);
                    } else {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null && player.isOnline()) {
                            player.sendMessage(ColorUtils.translateColors(
                                    plugin.getConfig().getString("Timer_System.Timer_Expired_Message")
                                            .replace("%prefix%", plugin.prefix)));
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                player.setHealth(0.0);
                                player.setGameMode(GameMode.SPECTATOR);
                            });
                        }
                        playerTimers.remove(uuid);
                    }
                }
            }

            for (Map.Entry<UUID, Long> entry : playerTimers.entrySet()) {
                playerDataManager.setPlayerTime(entry.getKey(), entry.getValue());
            }
        }, 0L, 20L);
    }
}