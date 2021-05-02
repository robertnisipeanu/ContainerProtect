package ro.ggez.containerprotect.commands;

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
import ro.ggez.containerprotect.PluginMain;
import ro.ggez.containerprotect.data.ProtectionType;
import ro.ggez.containerprotect.protection.TileProtection;

import java.util.ArrayList;

public class ContainerPrivate implements CommandExecutor, Listener {

    private final PluginMain plugin;

    public ContainerPrivate(PluginMain plugin) {
        this.plugin = plugin;
    }

    private ArrayList<Player> activeCmd = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (activeCmd.contains(player)) {
            activeCmd.remove(player);
            player.sendMessage("Exited cprivate mode!");
            return true;
        }

        activeCmd.add(player);
        player.sendMessage("Click on a container to register it on your name!");

        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Only accept if clicked block is not null
        if (e.getClickedBlock() == null || !(e.getClickedBlock().getState() instanceof TileState)) return;

        // If not in private mode, return
        if (!activeCmd.contains(e.getPlayer())) return;

        // Get protection data for block
        var protection = new TileProtection(plugin, e.getClickedBlock().getState());

        // If block is not protectable, return
        if (!protection.canBeProtected()) return;

        // Cancel the event
        e.setCancelled(true);
        activeCmd.remove(e.getPlayer());

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
