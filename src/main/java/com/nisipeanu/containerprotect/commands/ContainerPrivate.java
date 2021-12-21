package com.nisipeanu.containerprotect.commands;

import com.nisipeanu.containerprotect.PluginMain;
import com.nisipeanu.containerprotect.data.ProtectionType;
import com.nisipeanu.containerprotect.protection.TileProtection;
import org.bukkit.ChatColor;
import org.bukkit.block.TileState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ContainerPrivate implements CommandExecutor, Listener {

    private final PluginMain plugin;
    private final CommandActionCache activeCmdCache;

    public ContainerPrivate(PluginMain plugin, CommandActionCache activeCmdCache) {
        this.plugin = plugin;
        this.activeCmdCache = activeCmdCache;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (this.activeCmdCache.contains(player, ContainerCacheCommand.PRIVATE)) {
            this.activeCmdCache.remove(player);
            player.sendMessage("Exited cprivate mode!");
            return true;
        }

        this.activeCmdCache.add(player, ContainerCacheCommand.PRIVATE);
        player.sendMessage("Click on a container to register it on your name!");

        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Only accept if clicked block is not null
        if (e.getClickedBlock() == null || !(e.getClickedBlock().getState() instanceof TileState)) return;

        // If not in private mode, return
        if (!this.activeCmdCache.contains(e.getPlayer(), ContainerCacheCommand.PRIVATE)) return;

        // Get protection data for block
        var protection = new TileProtection(plugin, e.getClickedBlock().getState());

        // If block is not protectable, return
        if (!protection.canBeProtected()) return;

        // Cancel the event
        e.setCancelled(true);
        this.activeCmdCache.remove(e.getPlayer());

        // If block is already protected, tell the player
        if (protection.getProtectionLevel() != ProtectionType.NONE) {
            e.getPlayer().sendMessage(ChatColor.DARK_RED + "This " + protection.getDisplayName() + " is already protected");
            return;
        }

        // Set the player as owner of protection, remove any previous stored allowedlist
        protection.setOwner(e.getPlayer());
        protection.setAllowedList(new ArrayList<>());
        protection.setProtectionLevel(ProtectionType.PRIVATE);

        // Save changes to protection
        protection.save();

        // Tell player that the protection was registered
        e.getPlayer().sendMessage(ChatColor.GREEN + "Registered private " + protection.getDisplayName() + "!");
    }
}
