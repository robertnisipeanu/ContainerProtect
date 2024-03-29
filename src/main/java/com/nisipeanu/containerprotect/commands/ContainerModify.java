package com.nisipeanu.containerprotect.commands;

import com.nisipeanu.containerprotect.PluginMain;
import com.nisipeanu.containerprotect.data.ProtectionType;
import com.nisipeanu.containerprotect.protection.TileProtection;
import com.nisipeanu.containerprotect.sdk.ContainerAllowedManagerImplementation;
import org.bukkit.Bukkit;
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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class ContainerModify implements CommandExecutor, Listener {

    private final PluginMain plugin;
    private final CommandActionCache activeCmdCache;

    public ContainerModify(PluginMain plugin, CommandActionCache activeCmdCache) {
        this.plugin = plugin;
        this.activeCmdCache = activeCmdCache;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (this.activeCmdCache.contains(player, ContainerCacheCommand.MODIFY)) {
            this.activeCmdCache.remove(player);
            player.sendMessage(ChatColor.DARK_AQUA + "Exited cmodify mode!");
            return true;
        }

        if (args.length != 1) return false;

        this.activeCmdCache.add(player, ContainerCacheCommand.MODIFY, args[0].toLowerCase(Locale.ROOT));
        player.sendMessage(ChatColor.DARK_AQUA + "Please click your protection to give access to "
                + ChatColor.YELLOW + args[0]
                + "\n" + ChatColor.DARK_AQUA + "To cancel, write " + ChatColor.YELLOW + "/cmodify");

        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Only accept interaction from offhand in exceptional circumstances (when fired somehow for a container)
        if (e.getHand() == EquipmentSlot.OFF_HAND && !(e.getClickedBlock() instanceof Container)) return;

        // Only accept if clicked block is not null
        if (e.getClickedBlock() == null || !(e.getClickedBlock().getState() instanceof TileState)) return;

        // If not in edit mode, return
        if (!this.activeCmdCache.contains(e.getPlayer(), ContainerCacheCommand.MODIFY)) return;

        // Get protection data for block
        var protection = new TileProtection(plugin, e.getClickedBlock().getState());

        // If block is not protectable, return
        if (!protection.canBeProtected()) return;

        // Cancel the event
        e.setCancelled(true);

        var targetName = (String) this.activeCmdCache.get(e.getPlayer(), ContainerCacheCommand.MODIFY);
        this.activeCmdCache.remove(e.getPlayer());

        // If block is not protected, tell the player
        if (protection.getProtectionLevel() != ProtectionType.PRIVATE) {
            e.getPlayer().sendMessage(ChatColor.DARK_RED + "This " + protection.getDisplayName() + " is not registered");
            return;
        }

        if (!protection.getOwner().equals(e.getPlayer())) {
            e.getPlayer().sendMessage(ChatColor.DARK_RED + "You do not own that "
                    + e.getClickedBlock().getType().toString() + "!");
            return;
        }

        if (targetName.contains(":")) {
            // Find target as third party implementation

            var targetArgs = targetName.split(":", 2);
            var implementation = (new ContainerAllowedManagerImplementation()).getImplementationByPrefix(targetArgs[0]);
            if (implementation == null) {
                e.getPlayer().sendMessage(ChatColor.YELLOW + targetArgs[0] + ChatColor.DARK_RED + " is not a valid option!");
                return;
            }

            try {
                protection.addAdditionalAllowed(implementation.serializeValue(targetArgs[1]));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                e.getPlayer().sendMessage(ChatColor.DARK_RED + "There was an error while processing your request!");
                ex.printStackTrace();
                return;
            }
        } else {

            // Find target as OfflinePlayer
            var target = Arrays.stream(Bukkit.getOfflinePlayers())
                    .filter(offlinePlayer -> offlinePlayer.getName() != null
                            && offlinePlayer.getName().equalsIgnoreCase(targetName)).findFirst().orElse(null);
            // If target not found, show user a message and return
            if (target == null) {
                e.getPlayer().sendMessage(ChatColor.DARK_RED + "Player " + ChatColor.YELLOW + targetName
                        + ChatColor.DARK_RED + " not found");
                return;
            }

            // Update the protection
            protection.addAllowedList(target);
        }

        protection.save();

        e.getPlayer().sendMessage(ChatColor.GREEN + "Registered rights for " + ChatColor.YELLOW + targetName);

    }
}
