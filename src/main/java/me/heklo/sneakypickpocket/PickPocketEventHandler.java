package me.heklo.sneakypickpocket;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PickPocketEventHandler implements Listener {
    public Map<Player, Player> currentPickPockets;
    SneakyPickPocket plugin;

    public PickPocketEventHandler(SneakyPickPocket plugin)
    {
        this.plugin = plugin;
        currentPickPockets = new HashMap<>();
    }


    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player thief = event.getPlayer();
        if (event.getHand() == EquipmentSlot.HAND && event.getRightClicked() instanceof Player)
        {
            Player target = (Player) event.getRightClicked();
            if (canPickPocket(thief, target))
            {
                initiatePickPocket(thief, target);
				event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        Player thief = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if(inventory == null) { return; }

        // If pickpocketing and on top slot.
        if (event.getView().getTitle().equals(plugin.PICK_POCKET_TITLE) && inventory.equals(event.getView().getTopInventory()))
        {
            if(currentPickPockets.containsKey(thief))
            {
                Player target = currentPickPockets.get(thief);
                int chosenSlot = event.getSlot();
                PlayerInventory targetInventory = target.getInventory();
                ItemStack item = targetInventory.getItem(chosenSlot);
                Random random = new Random();
                if(random.nextFloat() < plugin.FAILURE_CHANCE) // If fail
                {
                    String thiefFailMessage = "&cFailed to pick pocket, player was alerted!";
                    String targetFailMessage = "&cSomeone tried to pick your pocket!";
                    thiefFailMessage = ChatColor.translateAlternateColorCodes('&', thiefFailMessage);
                    targetFailMessage = ChatColor.translateAlternateColorCodes('&', targetFailMessage);
                    thief.sendMessage(thiefFailMessage);
                    target.sendMessage(targetFailMessage);
                    thief.closeInventory();
                }
                else if(item == null) // If succeed but empty
                {
                    ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                    ItemMeta meta = redPane.getItemMeta();
                    meta.setDisplayName("Empty");
                    meta.setLore(Arrays.asList("This slot is empty!"));
                    redPane.setItemMeta(meta);
                    inventory.setItem(chosenSlot, redPane);
                }
                else // If succeed and full.
                {
                    targetInventory.setItem(chosenSlot, null);
                    thief.getInventory().addItem(item);
                    String thiefSuccessMessage = "&bSuccessfully picked pocket!";
                    thiefSuccessMessage = ChatColor.translateAlternateColorCodes('&', thiefSuccessMessage);
                    thief.sendMessage(thiefSuccessMessage);
                    thief.closeInventory();
                }
            }
            else
            {
                thief.closeInventory();
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Check if the event is for the target's inventory
        if (event.getView().getTitle().equals(plugin.PICK_POCKET_TITLE))
        {
            currentPickPockets.remove(event.getPlayer());
        }
    }

    public void initiatePickPocket(Player thief, Player target)
    {
        currentPickPockets.put(thief, target);
        Inventory inventory = createInventory();
        thief.openInventory(inventory);
    }
    public boolean canPickPocket(Player thief, Player target)
    {
        GameMode targetGameMode = target.getGameMode();
        if (!thief.isSneaking()) { return false; }
        if (!isBehind(thief, target)) { return false; }
        if (!thief.hasPermission(plugin.PICK_POCKET_PERMISSION_USE)) { return false; }
        if (target.hasPermission(plugin.PICK_POCKET_PERMISSION_BYPASS)) { return false; }
        if (targetGameMode == GameMode.CREATIVE || targetGameMode == GameMode.SPECTATOR) { return false; }
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard"))
        {
            if (!WorldGuardHelper.canPickPocket(target)) { return false; }
        }
        return true;
    }

    private boolean isBehind(Player thief, Player target) {
        Location thiefLoc = thief.getLocation();
        Location targetLoc = target.getLocation();
        Vector targetDirection = targetLoc.getDirection();
        Vector thiefOffset = targetLoc.subtract(thiefLoc).toVector();
        int angle = (int) Math.abs(thiefOffset.angle(targetDirection) * 180 / Math.PI);
        double distance = thiefOffset.length();
        return angle < plugin.ANGLE_THRESHOLD && distance <= plugin.DISTANCE_THRESHOLD;
    }

    public Inventory createInventory()
    {
        Inventory inventory = Bukkit.createInventory(null, 36, plugin.PICK_POCKET_TITLE);
        for(int i=0; i<inventory.getSize(); i++)
        {
            ItemStack whitePane = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
            ItemMeta meta = whitePane.getItemMeta();
            meta.setDisplayName("???");
            meta.setLore(Arrays.asList("Click here to try stealing an item!"));
            whitePane.setItemMeta(meta);
            inventory.setItem(i, whitePane);
        }
        return inventory;
    }
}
