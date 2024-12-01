package me.developer.chronocore.Commands;

import me.developer.chronocore.ChronoCore;
import me.developer.chronocore.Utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfoCommand implements CommandExecutor {

    private final ChronoCore plugin;

    public InfoCommand(ChronoCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(sender instanceof  Player)) {
            sender.sendMessage("You can't use players commands.");
            return false;
        }

        FileConfiguration config = plugin.getConfig();
        String prefix = ChronoCore.getInstance().prefix;

        List<String> usageMessages = config.getStringList("commands.Info-help");
        for (String message : usageMessages) {
            sender.sendMessage(ColorUtils.translateColors(message.replace("%prefix%", prefix)));
        }

        return true;
    }
}