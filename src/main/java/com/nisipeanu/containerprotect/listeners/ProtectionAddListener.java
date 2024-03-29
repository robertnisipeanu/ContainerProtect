package com.nisipeanu.containerprotect.listeners;

import com.nisipeanu.containerprotect.PluginMain;
import com.nisipeanu.containerprotect.checkers.DoubleChestChecker;
import com.nisipeanu.containerprotect.data.ProtectionType;
import com.nisipeanu.containerprotect.protection.EntityProtection;
import com.nisipeanu.containerprotect.protection.TileProtection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.TileState;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPlaceEvent;

public class ProtectionAddListener implements Listener {

    private final PluginMain plugin;

    public ProtectionAddListener(PluginMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {

        if (!(e.getBlock().getState() instanceof TileState)) return;

        var protection = new TileProtection(plugin, e.getBlock().getState());

        // If this block type can't be protected, return
        if (!protection.canBeProtected()) {
            return;
        }

        // Check if placing this chest forms a double chest
        var neighboringChest = DoubleChestChecker.getNeighboringChest(e.getBlock());
        TileProtection neighborProtection = null;

        if (neighboringChest != null) {
            // Get the protection of the chest that was already placed when creating the double chest
            neighborProtection = new TileProtection(plugin, neighboringChest.getState());

            // If there is a protection on the other chest and the owner is different, cancel the event
            if (neighborProtection.getProtectionLevel() != ProtectionType.NONE
                    && !neighborProtection.getOwner().equals(e.getPlayer())) {
                e.getPlayer().sendMessage(ChatColor.DARK_RED + "You can't place this here!");
                e.setCancelled(true);
                return;
            }
        }

        // Create a ContainerProtectData with Owner being the player that placed the block
        if (neighborProtection != null && neighborProtection.getOwner() != null) {
            protection.setOwner(neighborProtection.getOwner());
            protection.setAllowedList(neighborProtection.getAllowedList());
            protection.setAdditionalAllowedList(neighborProtection.getAdditionalAllowedList());
            protection.setProtectionLevel(neighborProtection.getProtectionLevel());
        } else {
            protection.setOwner(e.getPlayer());
            protection.setProtectionLevel(ProtectionType.PRIVATE);
        }

        // Save data
        protection.save();

        // If taking permissions from neighbor chest, we do not need to tell the player again that the protection was created
        if (neighborProtection != null) return;

        // Tell player that the container was registered
        e.getPlayer().sendMessage(ChatColor.GREEN + "Registered private " + protection.getDisplayName() + "!");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityPlace(EntityPlaceEvent e) {

        if (e.getPlayer() == null) return;

        if (e.getEntityType() != EntityType.MINECART_HOPPER
                && e.getEntityType() != EntityType.MINECART_CHEST
                && e.getEntityType() != EntityType.MINECART_FURNACE)
            return;

        var protection = new EntityProtection(plugin, e.getEntity());

        protection.setOwner(e.getPlayer());
        protection.save();
    }

}
