package me.heklo.sneakypickpocket;

import org.bukkit.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SneakyPickPocket extends JavaPlugin implements Listener {

    // Global Finals
    public final String PICK_POCKET_TITLE = "PickPocket";
    public final String PICK_POCKET_PERMISSION_USE = "pickpocket.use";
    public final String PICK_POCKET_PERMISSION_RELOAD = "pickpocket.reload";
    public final String PICK_POCKET_PERMISSION_BYPASS = "pickpocket.bypass";
    public final String PICK_POCKET_RELOAD_COMMAND = "pickpocketreload";

    // Global Variables
    public int ANGLE_THRESHOLD;
    public double DISTANCE_THRESHOLD;
    public double FAILURE_CHANCE;

    @Override
    public void onEnable()
    {
        // Config
        this.saveDefaultConfig();
        updateConfigValues();

        // Registrations
        Bukkit.getPluginManager().registerEvents(new PickPocketEventHandler(this), this);
        getCommand(PICK_POCKET_RELOAD_COMMAND).setExecutor(new PickPocketCommandExecutor(this));
    }

    @Override
    public void onLoad() {
        if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null)
        {
            Bukkit.getLogger().info("[SneakyPickPocket] WorldGuard Detected - enabling custom flag.");
            WorldGuardHelper.registerWorldGuardFlags();
        }
        else
        {
            Bukkit.getLogger().warning("[SneakyPickPocket] WorldGuard Missing - players can pickpocket everywhere.");
        }
    }

    public void updateConfigValues()
    {
        ANGLE_THRESHOLD = getConfig().getInt("angle-threshold", 10);
        DISTANCE_THRESHOLD = getConfig().getDouble("distance-threshold", 1.5);
        FAILURE_CHANCE = getConfig().getDouble("failure-chance", 0.2);
    }








}
