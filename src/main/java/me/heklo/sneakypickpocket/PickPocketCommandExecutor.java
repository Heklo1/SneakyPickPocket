package me.heklo.sneakypickpocket;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PickPocketCommandExecutor implements CommandExecutor
{
    SneakyPickPocket plugin;
    public PickPocketCommandExecutor(SneakyPickPocket plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(label.equals(plugin.PICK_POCKET_RELOAD_COMMAND))
        {
            if(sender.hasPermission(plugin.PICK_POCKET_PERMISSION_RELOAD))
            {
                String preMsg = "&6[SneakyPickPocket] &7Reloading config...";
                preMsg = ChatColor.translateAlternateColorCodes('&', preMsg);
                sender.sendMessage(preMsg);

                plugin.reloadConfig();
                plugin.updateConfigValues();

                String postMsg = "&6[SneakyPickPocket] &7Reloading config...";
                postMsg = ChatColor.translateAlternateColorCodes('&', postMsg);
                sender.sendMessage(postMsg);
            }
        }
        return false;
    }
}
