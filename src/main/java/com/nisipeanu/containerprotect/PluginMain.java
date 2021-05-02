package com.nisipeanu.containerprotect;

import com.nisipeanu.containerprotect.commands.*;
import com.nisipeanu.containerprotect.listeners.ProtectionPreventInteractListener;
import com.robertnisipeanu.containerprotect.commands.*;
import org.bukkit.plugin.java.JavaPlugin;
import ro.ggez.containerprotect.commands.*;
import com.nisipeanu.containerprotect.config.ConfigGenerator;
import com.nisipeanu.containerprotect.listeners.ProtectionAddListener;
import com.nisipeanu.containerprotect.listeners.ProtectionPreventDestroyListener;

public class PluginMain extends JavaPlugin {

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(new ProtectionAddListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionPreventDestroyListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionPreventInteractListener(this), this);

        var containerInfo = new ContainerInfo(this);
        getServer().getPluginManager().registerEvents(containerInfo, this);
        getCommand("cinfo").setExecutor(containerInfo);

        var containerPrivate = new ContainerPrivate(this);
        getServer().getPluginManager().registerEvents(containerPrivate, this);
        getCommand("cprivate").setExecutor(containerPrivate);

        var containerModify = new ContainerModify(this);
        getServer().getPluginManager().registerEvents(containerModify, this);
        getCommand("cmodify").setExecutor(containerModify);

        var containerRemove = new ContainerRemove(this);
        getServer().getPluginManager().registerEvents(containerRemove, this);
        getCommand("cremove").setExecutor(containerRemove);

        var containerReload = new ContainerReload(this);
        getCommand("creload").setExecutor(containerReload);

        var configGenerator = new ConfigGenerator(this);
        configGenerator.generate();

    }

}
