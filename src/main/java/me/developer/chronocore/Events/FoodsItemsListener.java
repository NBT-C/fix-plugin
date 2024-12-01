package me.developer.chronocore.Events;

import me.developer.chronocore.ChronoCore;
import me.developer.chronocore.Utils.PlayerDataManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class FoodsItemsListener implements Listener {

    private final PlayerDataManager playerDataManager;
    private final ChronoCore plugin;

    public FoodsItemsListener(PlayerDataManager playerDataManager, ChronoCore plugin) {
        this.playerDataManager = playerDataManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        String itemName = item.getItemMeta().getDisplayName();
        UUID uuid = player.getUniqueId();

        for (String key : plugin.getConfig().getConfigurationSection("Timer_System.Foods.Items").getKeys(false)) {
            String configName = plugin.getConfig().getString("Timer_System.Foods.Items." + key + ".Item-Name");
            String configMaterial = plugin.getConfig().getString("Timer_System.Foods.Items." + key + ".Item-Material");
            int timeToAdd = plugin.getConfig().getInt("Timer_System.Foods.Items." + key + ".Time-To-Add");
            boolean messageEnabled = plugin.getConfig().getBoolean("Timer_System.Foods.Items." + key + ".Add-Timer-Message.Enable");
            String message = plugin.getConfig().getString("Timer_System.Foods.Items." + key + ".Add-Timer-Message.Message");

            if (itemName.equals(ChatColor.translateAlternateColorCodes('&', configName)) &&
                    item.getType() == Material.valueOf(configMaterial)) {

                long currentHdf = playerDataManager.getPlayerNeededHours(uuid);
                long newTimer = currentHdf + (timeToAdd * 60L);
                playerDataManager.setPlayerNeededHours(uuid, newTimer);

                if (messageEnabled) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            message.replace("%prefix%", ChronoCore.getInstance().prefix)
                                    .replace("%extra-timer%", String.valueOf(timeToAdd))));
                }
                break;
            }
        }
    }
}