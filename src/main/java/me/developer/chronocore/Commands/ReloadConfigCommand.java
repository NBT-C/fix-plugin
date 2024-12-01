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

public class ReloadConfigCommand implements CommandExecutor {

    private final ChronoCore plugin;

    public ReloadConfigCommand(ChronoCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        FileConfiguration config = plugin.getConfig();
        String prefix = ChronoCore.getInstance().prefix;
        String unknownCommandMessage = config.getString("messages.reload.Unknown-Command").replace("%prefix%", prefix);

        if (sender instanceof Player) {
            Player player = (Player) sender;
            String noPermissionMessage = config.getString("messages.reload.NO-PERMISSIONS").replace("%prefix%", prefix);
            if (!player.hasPermission(plugin.getConfig().getString("commands.ChronoCore-help.Permission"))) {
                player.sendMessage(ColorUtils.translateColors(noPermissionMessage));
                return false;
            }
        }

        if (args.length < 1) {
            List<String> usageMessages = config.getStringList("commands.ChronoCore-help.Usage");
            for (String message : usageMessages) {
                sender.sendMessage(ColorUtils.translateColors(message.replace("%prefix%", prefix)));
            }
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("reload")) {
            plugin.reloadConfig();
            plugin.saveConfig();
            sender.sendMessage(ColorUtils.translateColors(config.getString("messages.reload.Message").replace("%prefix%", prefix)));
        } else {
            sender.sendMessage(ColorUtils.translateColors(unknownCommandMessage));
        }

        return true;
    }
}