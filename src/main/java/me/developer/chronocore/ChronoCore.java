package me.developer.chronocore;

import lombok.Getter;
import me.developer.chronocore.Commands.InfoCommand;
import me.developer.chronocore.Commands.ReloadConfigCommand;
import me.developer.chronocore.Commands.TimeLeftCommand;
import me.developer.chronocore.Events.*;
import me.developer.chronocore.Recipe.ReviveRecipe;
import me.developer.chronocore.Utils.ColorUtils;
import me.developer.chronocore.Utils.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class ChronoCore extends JavaPlugin {

    @Getter
    private static ChronoCore instance;
    private @Getter Map<UUID, Long> joinTimes = new HashMap<>();
    private @Getter Map<UUID, Long> joinTimesFixed = new HashMap<>();
    private List<UUID> ghostsLol = new ArrayList<>();
    public String prefix = this.getConfig().getString("prefix");
    private @Getter PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        getLogger().info("Plugin has been Enabled.");

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        reloadConfig();

        this.playerDataManager = new PlayerDataManager(this);
        ReviveRecipe recipe = new ReviveRecipe(this, playerDataManager);

        recipe.loadRecipe();
        registerCMDS();
        registerEvents();
        instance = this;

        startTimeCheckTask();
    }

    private void registerCMDS(){

        // Players Commands [HERE]
        getCommand("timeleft").setExecutor(new TimeLeftCommand(this));
        getCommand("info").setExecutor(new InfoCommand(this));
        // Operators Commands [HERE]
        getCommand("cc").setExecutor(new ReloadConfigCommand(this));
    }

    private void registerEvents(){
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerDataManager, this), this);
        getServer().getPluginManager().registerEvents(new FoodsItemsListener(playerDataManager, this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(playerDataManager, this), this);
        getServer().getPluginManager().registerEvents(new DisableSpectatorsTeleportListener(), this);
        getServer().getPluginManager().registerEvents(new MobsDropsListener(this), this);
        getServer().getPluginManager().registerEvents(new SmeltMeatListener(this), this);
        getServer().getPluginManager().registerEvents(new ReviveRecipe(this, playerDataManager), this);
    }

    public void startTimeCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    ghostSwitchCheck(player);
                });
            }
        }.runTaskTimer(this, 0, 20);
    }

    public void ghostSwitchCheck(Player player){
        if (ghostsLol.contains(player.getUniqueId())) return;
        if (playerDataManager.getAchievedSeconds(player.getUniqueId()) >= playerDataManager.getPlayerNeededHours(player.getUniqueId())) {
            ghostsLol.add(player.getUniqueId());
            playerDataManager.setPlayerNeededHours(player.getUniqueId(), 0);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(ColorUtils.translateColors(
                    getConfig().getString("Timer_System.Turn_to_Ghost")
                            .replace("%prefix%", ChronoCore.getInstance().prefix)
            ));
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin has been Disabled.");
        saveConfig();
    }
}