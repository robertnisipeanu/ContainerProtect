package ro.ggez.containerprotect.commands;

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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import ro.ggez.containerprotect.PluginMain;
import ro.ggez.containerprotect.data.ProtectionType;
import ro.ggez.containerprotect.protection.TileProtection;

import java.util.ArrayList;

public class ContainerInfo implements CommandExecutor, Listener {
    private final PluginMain plugin;

    public ContainerInfo(PluginMain plugin) {
        this.plugin = plugin;
    }

    private final ArrayList<Player> activeCmd = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (activeCmd.contains(player)) {
            activeCmd.remove(player);
            player.sendMessage(ChatColor.DARK_AQUA + "Exited cinfo!");
            return true;
        }

        activeCmd.add(player);
        player.sendMessage(ChatColor.DARK_AQUA + "Punch a protection to view information on it!");

        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !(event.getClickedBlock().getState() instanceof TileState)) return;

        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Only accept interaction from offhand in exceptional circumstances (when fired somehow for a container)
        if (event.getHand() == EquipmentSlot.OFF_HAND && !(event.getClickedBlock() instanceof Container)) {
            return;
        }

        if (!activeCmd.contains(event.getPlayer())) return;


        var protection = new TileProtection(plugin, event.getClickedBlock().getState());

        // If block is not protectable, return
        if (!protection.canBeProtected()) return;

        // If block is not registered as a private container, return
        if (protection.getProtectionLevel() == ProtectionType.NONE) {
            activeCmd.remove(event.getPlayer());
            event.getPlayer().sendMessage(ChatColor.DARK_RED + "This " +
                    protection.getDisplayName() +
                    " is not protected!");
            event.setCancelled(true);
            return;
        }


        // Build output message
        StringBuilder message =
                new StringBuilder(ChatColor.WHITE + "Owner: " +
                        ChatColor.GREEN + protection.getOwner().getName() + "(" +
                        protection.getOwner().getUniqueId().toString() + ")" + "\n\n"
                        + ChatColor.DARK_RED + "Access Control List" + ChatColor.WHITE + " (" +
                        protection.getAllowedList().size() + ")\n");

        for (var allowed : protection.getAllowedList()) {
            message.append(ChatColor.WHITE).append("- ").append(ChatColor.YELLOW).append(allowed.getName())
                    .append(" (").append(allowed.getUniqueId().toString()).append(")").append("\n");
        }

        // Remove player from /cinfo command mode
        activeCmd.remove(event.getPlayer());

        event.getPlayer().sendMessage(message.toString());
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        activeCmd.remove(e.getPlayer());
    }
}
