package me.developer.chronocore.Utils;

import org.bukkit.ChatColor;

@SuppressWarnings("deprecation")
public class ColorUtils {

    public static String translateColors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}