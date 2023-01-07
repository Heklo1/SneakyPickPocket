package me.heklo.sneakypickpocket;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WorldGuardHelper
{
    public static StateFlag PICK_POCKET_FLAG;
    public static final String PICK_POCKET_FLAG_STRING = "pickpocket";

    public static void registerWorldGuardFlags()
    {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try
        {
            StateFlag flag = new StateFlag(PICK_POCKET_FLAG_STRING, false);
            registry.register(flag);
            PICK_POCKET_FLAG = flag;
            Bukkit.getLogger().info("[SneakyPickPocket] Successfully registered wg flag '" + PICK_POCKET_FLAG_STRING + "'");
        }
        catch (FlagConflictException e) {
            Flag<?> existing = registry.get(PICK_POCKET_FLAG_STRING);
            Bukkit.getLogger().warning("[SneakyPickPocket] Error: Unable to register '" + PICK_POCKET_FLAG_STRING + "'");
            if (existing instanceof StateFlag)
            {
                PICK_POCKET_FLAG = (StateFlag) existing;
            }
        }
    }

    public static boolean canPickPocket(Player player)
    {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        ApplicableRegionSet regionSet = query.getApplicableRegions(localPlayer.getLocation());
        return regionSet.testState(localPlayer, PICK_POCKET_FLAG);
    }
}
