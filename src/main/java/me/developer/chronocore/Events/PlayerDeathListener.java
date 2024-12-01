package me.developer.chronocore.Events;

import me.developer.chronocore.ChronoCore;
import me.developer.chronocore.Utils.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class PlayerDeathListener implements Listener {

    private final PlayerDataManager playerDataManager;
    private final ChronoCore plugin;

    public PlayerDeathListener(PlayerDataManager playerDataManager, ChronoCore plugin) {
        this.playerDataManager = playerDataManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null && killer.isOnline()) {
            long timeToSteal = plugin.getConfig().getLong("Timer_System.Kill_Steal_Time", 30);
            UUID victimUUID = victim.getUniqueId();
            UUID killerUUID = killer.getUniqueId();

            long victimTime = playerDataManager.getPlayerNeededHours(victimUUID) / 60;
            long killerTime = playerDataManager.getPlayerNeededHours(killerUUID) / 60;

            if (victimTime > 0) {
                long stolenTime = Math.min(victimTime, timeToSteal);

                playerDataManager.setPlayerNeededHours(victimUUID, (victimTime - stolenTime) * 60);
                playerDataManager.setPlayerNeededHours(killerUUID, (killerTime + stolenTime) * 60);

                String victimMessage = plugin.getConfig().getString("Timer_System.Kill_Loss_Message",
                        "%prefix% &cYou lost %stolen-time% minute(s) to %killer%!");
                victimMessage = victimMessage.replace("%prefix%", ChronoCore.getInstance().prefix)
                        .replace("%stolen-time%", String.valueOf(stolenTime))
                        .replace("%killer%", killer.getName());
                victim.sendMessage(ChatColor.translateAlternateColorCodes('&', victimMessage));

                String killerMessage = plugin.getConfig().getString("Timer_System.Kill_Gain_Message",
                        "%prefix% &aYou gained %stolen-time% minute(s) from %victim%!");
                killerMessage = killerMessage.replace("%prefix%", ChronoCore.getInstance().prefix)
                        .replace("%stolen-time%", String.valueOf(stolenTime))
                        .replace("%victim%", victim.getName());
                killer.sendMessage(ChatColor.translateAlternateColorCodes('&', killerMessage));
            } else {
                victim.sendMessage(ChatColor.RED + "You had no time left to lose!");
            }
        } else {
            long timeToSteal = plugin.getConfig().getLong("Timer_System.Kill_Steal_Time", 30);
            UUID victimUUID = victim.getUniqueId();

            long victimTime = playerDataManager.getPlayerNeededHours(victimUUID) / 60;

            if (victimTime > 0) {
                long stolenTime = Math.min(victimTime, timeToSteal);
                playerDataManager.setPlayerNeededHours(victimUUID, (victimTime - stolenTime) * 60);

                String victimMessage = plugin.getConfig().getString("Timer_System.Kill_Loss_Message",
                        "%prefix% &cYou lost %stolen-time% minute(s) to %killer%!");
                victimMessage = victimMessage.replace("%prefix%", ChronoCore.getInstance().prefix)
                        .replace("%stolen-time%", String.valueOf(stolenTime))
                        .replace("%killer%", killer.getName());

                victim.sendMessage(ChatColor.translateAlternateColorCodes('&', victimMessage));
            }
        }
    }
}