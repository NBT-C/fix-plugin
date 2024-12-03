package me.developer.chronocore.Events;

import me.developer.chronocore.ChronoCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Random;

@SuppressWarnings("deprecation")
public class MobsDropsListener implements Listener {

    private final ChronoCore plugin;

    public MobsDropsListener(ChronoCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        EntityType entityType = entity.getType();
        FileConfiguration config = plugin.getConfig();
        Player killer = entity.getKiller();

        if (killer != null) {
            if (!config.contains("Mobs_Drops.Mobs." + entityType.name())) {
                plugin.getLogger().info("No drop configuration for mob: " + entityType.name());
                return;
            }

            if (!config.isConfigurationSection("Mobs_Drops.Mobs." + entityType.name() + ".Items")) {
                plugin.getLogger().warning("No items defined for mob: " + entityType.name());
                return;
            }

            Random random = new Random();
            for (String key : config.getConfigurationSection("Mobs_Drops.Mobs." + entityType.name() + ".Items").getKeys(false)) {
                String itemPath = "Mobs_Drops.Mobs." + entityType.name() + ".Items." + key;

                String itemName = ChatColor.translateAlternateColorCodes('&', config.getString(itemPath + ".Item-Name", "Unnamed Item"));
                Material material = Material.matchMaterial(config.getString(itemPath + ".Item-Material", "STONE"));
                int chance = config.getInt(itemPath + ".Chance-to-Drops", 0);

                if (material == null) {
                    plugin.getLogger().warning("Invalid material in config for " + entityType.name() + ": " + itemPath);
                    continue;
                }

                if (random.nextInt(100) < chance) {
                    ItemStack item = new ItemStack(material);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(itemName);
                        item.setItemMeta(meta);
                    }

                    entity.getWorld().dropItemNaturally(entity.getLocation(), item);
                }
            }
        }
    }
}