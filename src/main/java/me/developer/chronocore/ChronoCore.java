package me.developer.chronocore;

import lombok.Getter;
import me.developer.chronocore.Commands.InfoCommand;
import me.developer.chronocore.Commands.ReloadConfigCommand;
import me.developer.chronocore.Commands.TimeLeftCommand;
import me.developer.chronocore.Events.FoodsItemsListener;
import me.developer.chronocore.Events.PlayerDeathListener;
import me.developer.chronocore.Events.PlayerJoinListener;
import me.developer.chronocore.Events.SmeltMeatListener;
import me.developer.chronocore.Utils.PlayerDataManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ChronoCore extends JavaPlugin {

    private PlayerJoinListener playerJoinListener;
    private static ChronoCore instance;
    private @Getter Map<UUID, Long> joinTimes = new HashMap<>();
    private @Getter Map<UUID, Long> joinTimesFixed = new HashMap<>();
    public String prefix = this.getConfig().getString("prefix");
    private @Getter PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        getLogger().info("Plugin has been Enabled.");


        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        reloadConfig();

        this.playerDataManager = new PlayerDataManager(this);
        registerCMDS();
        registerEvents();
        instance = this;

        this.playerJoinListener = new PlayerJoinListener(new PlayerDataManager(this), this);
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
        getServer().getPluginManager().registerEvents(new SmeltMeatListener(this), this);
    }

    public static ChronoCore getInstance() {
        return instance;
    }


    public PlayerJoinListener getJoinListener() {
        return playerJoinListener;
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin has been Disabled.");
        saveConfig();
    }
}