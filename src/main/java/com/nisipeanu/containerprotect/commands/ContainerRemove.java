package com.nisipeanu.containerprotect.commands;

import com.nisipeanu.containerprotect.PluginMain;
import com.nisipeanu.containerprotect.data.ProtectionType;
import com.nisipeanu.containerprotect.protection.TileProtection;
import org.bukkit.ChatColor;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ContainerRemove implements CommandExecutor, Listener {
    private final PluginMain plugin;

    public ContainerRemove(PluginMain plugin) {
        this.plugin = plugin;
    }

    ArrayList<Player> activeCmd = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (activeCmd.contains(player)) {
            activeCmd.remove(player);
            player.sendMessage(ChatColor.DARK_AQUA + "Exited cremove mode!");
            return true;
        }

        activeCmd.add(player);
        player.sendMessage(ChatColor.DARK_AQUA + "Please click your protection to remove it.\n To cancel, write "
                + ChatColor.YELLOW + "/cremove");
        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Only accept interaction from offhand in exceptional circumstances (when fired somehow for a container)
        if (e.getHand() == EquipmentSlot.OFF_HAND && !(e.getClickedBlock() instanceof Container)) return;

        // If no block was clicked, return
        if (e.getClickedBlock() == null || !(e.getClickedBlock().getState() instanceof TileState)) return;

        // If not in edit mode, return
        if (!activeCmd.contains(e.getPlayer())) return;

        // Get protection data for block
        var protection = new TileProtection(plugin, e.getClickedBlock().getState());

        // If block is not protectable, return
        if (!protection.canBeProtected()) return;

        // Cancel the event
        e.setCancelled(true);

        // Remove the player from listening list
        activeCmd.remove(e.getPlayer());

        // If block is not protected, tell the player
        if (protection.getProtectionLevel() == ProtectionType.NONE) {
            e.getPlayer().sendMessage(ChatColor.DARK_RED + "This " + protection.getDisplayName() +
                    " is not protected!");
            return;
        }

        // If the player is not owner of the protection and doesn't have admin permission
        // cancel the event
        if (!protection.getOwner().equals(e.getPlayer())
                && !e.getPlayer().hasPermission("containerprotect.admin.remove")) {
            e.getPlayer().sendMessage(ChatColor.DARK_RED + "You do not own this " +
                    protection.getDisplayName()
                    + "!"
            );
            return;
        }

        protection.setProtectionLevel(ProtectionType.NONE);
        protection.save();

        e.getPlayer().sendMessage(ChatColor.GREEN + "Removed protection from " +
                protection.getDisplayName() +
                " successfully."
        );

    }
}
