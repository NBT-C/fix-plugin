package me.developer.chronocore.Commands;

import me.developer.chronocore.ChronoCore;
import me.developer.chronocore.Events.PlayerJoinListener;
import me.developer.chronocore.Utils.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class TimeLeftCommand implements CommandExecutor {

    private final ChronoCore plugin;

    public TimeLeftCommand(ChronoCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (!ChronoCore.getInstance().getJoinTimes().containsKey(uuid)) {
            player.sendMessage(ChatColor.RED + "ll7en ma d5lt ya 7mar REJOIN lw swet reload");
            return true;
        }

        long neededTime = ChronoCore.getInstance().getPlayerDataManager().getPlayerNeededHours(uuid);
        System.out.println(neededTime);
        long joinTime = ChronoCore.getInstance().getJoinTimesFixed().get(uuid);
        long elapsedTime = (System.currentTimeMillis() - joinTime) / 1000;
        long remainingTime = neededTime - elapsedTime;

        if (remainingTime <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("Timer_System.No_Time_Left_Message", "&cYou have no time left!")));
        } else {
            long totalSeconds = remainingTime;
            long seconds = totalSeconds % 60;
            long totalMinutes = totalSeconds / 60;
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;

            String message = plugin.getConfig().getString("Timer_System.Time_Left_Message",
                    "%prefix% &aYou have %hours% hour(s), %minutes% minute(s), and %seconds% second(s) left.");
            message = message.replace("%prefix%", ChronoCore.getInstance().prefix)
                    .replace("%hours%", String.valueOf(hours))
                    .replace("%minutes%", String.valueOf(minutes))
                    .replace("%seconds%", String.valueOf(seconds));

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        return true;
    }
}