package ro.ggez.containerprotect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import ro.ggez.containerprotect.PluginMain;

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

        if(activeCmd.contains(player)) {
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

    }
}
