package com.nisipeanu.containerprotect;

import com.nisipeanu.containerprotect.commands.*;
import com.nisipeanu.containerprotect.listeners.ProtectionPreventInteractListener;
import com.nisipeanu.containerprotect.sdk.ContainerAllowedManager;
import com.nisipeanu.containerprotect.sdk.ContainerAllowedManagerImplementation;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import com.nisipeanu.containerprotect.config.ConfigGenerator;
import com.nisipeanu.containerprotect.listeners.ProtectionAddListener;
import com.nisipeanu.containerprotect.listeners.ProtectionPreventDestroyListener;

public class PluginMain extends JavaPlugin {

    @Override
    public void onLoad() {
        Bukkit.getServicesManager().register(ContainerAllowedManager.class, new ContainerAllowedManagerImplementation(), this, ServicePriority.Normal);
    }

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(new ProtectionAddListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionPreventDestroyListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionPreventInteractListener(this), this);

        var commandActionCache = new CommandActionCache();

        var containerInfo = new ContainerInfo(this, commandActionCache);
        getServer().getPluginManager().registerEvents(containerInfo, this);
        getCommand("cinfo").setExecutor(containerInfo);

        var containerPrivate = new ContainerPrivate(this, commandActionCache);
        getServer().getPluginManager().registerEvents(containerPrivate, this);
        getCommand("cprivate").setExecutor(containerPrivate);

        var containerModify = new ContainerModify(this, commandActionCache);
        getServer().getPluginManager().registerEvents(containerModify, this);
        getCommand("cmodify").setExecutor(containerModify);

        var containerRemove = new ContainerRemove(this, commandActionCache);
        getServer().getPluginManager().registerEvents(containerRemove, this);
        getCommand("cremove").setExecutor(containerRemove);

        var containerReload = new ContainerReload(this);
        getCommand("creload").setExecutor(containerReload);

        var configGenerator = new ConfigGenerator(this);
        configGenerator.generate();

    }

}
