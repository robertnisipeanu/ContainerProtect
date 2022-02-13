package com.nisipeanu.containerprotect.listeners;

import com.nisipeanu.containerprotect.protection.TileProtection;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import com.nisipeanu.containerprotect.PluginMain;
import com.nisipeanu.containerprotect.data.ProtectionType;
import com.nisipeanu.containerprotect.protection.EntityProtection;
import com.nisipeanu.containerprotect.protection.Protection;

import java.util.Objects;


public class ProtectionPreventInteractListener implements Listener {

    private final PluginMain plugin;

    public ProtectionPreventInteractListener(PluginMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {

        // If no block was clicked, return
        if (e.getClickedBlock() == null || !(e.getClickedBlock().getState() instanceof TileState)) return;

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Get protection data for block
        var protection = new TileProtection(plugin, e.getClickedBlock().getState());

        // If block not protected, we can ignore the event
        if (protection.getProtectionLevel() != ProtectionType.PRIVATE) return;

        // If it's not owner/allowed or has admin permission, cancel the event
        if (!e.getPlayer().equals(protection.getOwner())
                && !protection.getAllowedList().contains(e.getPlayer())) {

            // If player has admin permission, tell him who is the owner of the container
            if (e.getPlayer().hasPermission("containerprotect.admin.interact")) {
                e.getPlayer().sendMessage(ChatColor.WHITE + "This " +
                        ChatColor.DARK_RED + protection.getDisplayName() +
                        ChatColor.WHITE + " is owned by " + ChatColor.DARK_RED + protection.getOwner().getName());
                return;
            }

            e.getPlayer().sendMessage(ChatColor.DARK_RED + "This "
                    + protection.getDisplayName() + " is locked with a magical spell");
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemInventoryMoveEvent(InventoryMoveItemEvent e) {
        Protection sourceProtection = null;
        Protection destinationProtection = null;

        if (e.getSource().getHolder() instanceof BlockState) {
            sourceProtection = new TileProtection(plugin, (BlockState) e.getSource().getHolder());
        } else if (e.getSource().getHolder() instanceof Entity) {
            sourceProtection = new EntityProtection(plugin, (Entity) e.getSource().getHolder());
        } else if (e.getSource().getHolder() instanceof DoubleChest) {
            var chest = ((DoubleChest) e.getSource().getHolder()).getRightSide();
            sourceProtection = new TileProtection(plugin, (BlockState) chest);
        }

        if (e.getDestination().getHolder() instanceof BlockState) {
            destinationProtection = new TileProtection(plugin, (BlockState) e.getDestination().getHolder());
        } else if (e.getDestination().getHolder() instanceof Entity) {
            destinationProtection = new EntityProtection(plugin, (Entity) e.getDestination().getHolder());
        } else if (e.getDestination().getHolder() instanceof DoubleChest) {
            var chest = ((DoubleChest) e.getDestination().getHolder()).getRightSide();
            destinationProtection = new TileProtection(plugin, (BlockState) chest);
        }

        // If neither of them can be protected, ignore
        if (sourceProtection == null && destinationProtection == null) {
            return;
        }

        // If none of them are protected, ignore
        if (
                sourceProtection != null && sourceProtection.getProtectionLevel() == ProtectionType.NONE
                && destinationProtection != null && destinationProtection.getProtectionLevel() == ProtectionType.NONE
        ) {
            return;
        }

        // If only one of them is protected and we have no information about who is the owner
        // of the other one, cancel the event
        if (
                (sourceProtection == null || destinationProtection == null)
                        && (
                        (sourceProtection != null && sourceProtection.getProtectionLevel() == ProtectionType.PRIVATE)
                                || (destinationProtection != null
                                && destinationProtection.getProtectionLevel() == ProtectionType.PRIVATE)
                )
        ) {
            e.setCancelled(true);
            return;
        }



        // If they do not have the same owner, cancel the event
        if (!Objects.equals(sourceProtection.getOwner(), destinationProtection.getOwner())) {
            e.setCancelled(true);
            return;
        }
    }

}
