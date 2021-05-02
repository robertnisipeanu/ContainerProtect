package com.nisipeanu.containerprotect.listeners;

import com.nisipeanu.containerprotect.PluginMain;
import com.nisipeanu.containerprotect.checkers.DoubleChestChecker;
import com.nisipeanu.containerprotect.data.ProtectionType;
import com.nisipeanu.containerprotect.protection.TileProtection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.List;
import java.util.Objects;

public class ProtectionPreventDestroyListener implements Listener {
    private final PluginMain plugin;

    public ProtectionPreventDestroyListener(PluginMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {

        if (!(e.getBlock().getState() instanceof TileState)) return;

        var protection = new TileProtection(plugin, e.getBlock().getState());

        // If block can't be protected or is not protected, return
        if (protection.getProtectionLevel() == ProtectionType.NONE) return;

        // If it's not the owner or doesn't have admin permission, cancel the event
        if (!Objects.requireNonNull(protection.getOwner()).equals(e.getPlayer())
                && !e.getPlayer().hasPermission("containerprotect.admin.remove")) {
            e.getPlayer().sendMessage(ChatColor.DARK_RED + "You do not own this!");
            e.setCancelled(true);
            return;
        }

        // If it was double chest (it has an adjacent Protection, don't send a message to the player)
        if (DoubleChestChecker.getNeighboringChest(e.getBlock()) != null)
            return;

        // Tell the player that the protection was unregistered.
        // Do not need to manually delete it, because the data is stored on the block state, which
        // gets deleted with the block.
        e.getPlayer().sendMessage(ChatColor.DARK_RED + protection.getDisplayName() + " unregistered!");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent e) {

        List<BlockState> blocks = e.getBlocks();

        // Check for each modified block, if one is protected, then cancel the event
        for (BlockState block : blocks) {
            if (!(block instanceof TileState)) continue;

            var protection = new TileProtection(plugin, block);

            if (protection.getProtectionLevel() != ProtectionType.NONE) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {

        if (!(e.getBlock().getState() instanceof TileState)) return;

        var protection = new TileProtection(plugin, e.getBlock().getState());

        if (protection.getProtectionLevel() != ProtectionType.NONE) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent e) {

        if (!(e.getToBlock().getState() instanceof TileState)) return;

        if (e.getBlock().getType() == Material.WATER) {
            var protection = new TileProtection(plugin, e.getToBlock().getState());
            if (protection.getProtectionLevel() != ProtectionType.NONE) e.setCancelled(true);
        }

    }

    private boolean hasAnyProtectedBlock(List<Block> blocks) {

        for (var block : blocks) {
            if (!(block.getState() instanceof TileState)) continue;

            var protection = new TileProtection(plugin, block.getState());
            if (protection.getProtectionLevel() != ProtectionType.NONE) return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent e) {
        if (hasAnyProtectedBlock(e.getBlocks())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent e) {
        if (hasAnyProtectedBlock(e.getBlocks())) e.setCancelled(true);
    }

    private void removeAnyProtectedBlock(List<Block> blocks) {
        blocks.removeIf(block -> block.getState() instanceof TileState
                && new TileProtection(plugin, block.getState()).getProtectionLevel() != ProtectionType.NONE);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent e) {
        removeAnyProtectedBlock(e.blockList());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent e) {
        removeAnyProtectedBlock(e.blockList());
    }

}
