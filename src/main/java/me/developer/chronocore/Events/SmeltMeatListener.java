package me.developer.chronocore.Events;

import me.developer.chronocore.ChronoCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("deprecation")
public class SmeltMeatListener implements Listener {

    private final ChronoCore plugin;

    public SmeltMeatListener(ChronoCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        ItemStack smeltedItem = event.getSource();

        String timeMeatName = plugin.getConfig().getString("Timer_System.Foods.Items.Time_Meat.Item-Name", "&e&lTime &6&lMeat");
        String chronoMeatName = plugin.getConfig().getString("Timer_System.Foods.Items.Chrono_Meat.Item-Name", "&a&lChrono &2&lMeat");

        if (smeltedItem.getType() == Material.BEEF && smeltedItem.hasItemMeta()) {
            ItemMeta meta = smeltedItem.getItemMeta();
            if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', timeMeatName))) {
                ItemStack chronoMeat = new ItemStack(Material.COOKED_BEEF);
                ItemMeta chronoMeta = chronoMeat.getItemMeta();

                if (chronoMeta != null) {
                    chronoMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', chronoMeatName));
                    chronoMeat.setItemMeta(chronoMeta);
                }

                event.setResult(chronoMeat);
            }
        }
    }
}