package me.developer.chronocore;

import me.developer.chronocore.Commands.InfoCommand;
import me.developer.chronocore.Commands.ReloadConfigCommand;
import me.developer.chronocore.Commands.TimeLeftCommand;
import me.developer.chronocore.Events.FoodsItemsListener;
import me.developer.chronocore.Events.PlayerDeathListener;
import me.developer.chronocore.Events.PlayerJoinListener;
import me.developer.chronocore.Events.SmeltMeatListener;
import me.developer.chronocore.Utils.PlayerDataManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChronoCore extends JavaPlugin {

    private PlayerJoinListener playerJoinListener;
    private static ChronoCore instance;
    public String prefix = this.getConfig().getString("prefix");

    @Override
    public void onEnable() {
        getLogger().info("Plugin has been Enabled.");

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        reloadConfig();
        registerCMDS();
        registerEvents();
        callTheStartTimerTask();
        instance = this;

        this.playerJoinListener = new PlayerJoinListener(new PlayerDataManager(this), this);
    }

    private void registerCMDS(){
        PlayerDataManager playerDataManager = new PlayerDataManager(this);

        // Players Commands [HERE]
        getCommand("timeleft").setExecutor(new TimeLeftCommand(playerDataManager, this));
        getCommand("info").setExecutor(new InfoCommand(this));
        // Operators Commands [HERE]
        getCommand("cc").setExecutor(new ReloadConfigCommand(this));
    }

    private void registerEvents(){
        PlayerDataManager playerDataManager = new PlayerDataManager(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerDataManager, this), this);
        getServer().getPluginManager().registerEvents(new FoodsItemsListener(playerDataManager, this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(playerDataManager, this), this);
        getServer().getPluginManager().registerEvents(new SmeltMeatListener(this), this);
    }

    public static ChronoCore getInstance() {
        return instance;
    }

    private void callTheStartTimerTask() {
        PlayerDataManager playerDataManager = new PlayerDataManager(this);
        PlayerJoinListener playerJoinListener = new PlayerJoinListener(playerDataManager, this);
        playerJoinListener.startTimerTask();
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