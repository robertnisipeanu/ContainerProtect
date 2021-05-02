package com.nisipeanu.containerprotect.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import com.nisipeanu.containerprotect.PluginMain;

public class ContainerReload implements CommandExecutor {
    private final PluginMain plugin;

    public ContainerReload(PluginMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Config reloaded successfully!");

        return true;
    }
}
