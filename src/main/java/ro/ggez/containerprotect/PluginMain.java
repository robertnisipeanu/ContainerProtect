package ro.ggez.containerprotect;

import org.bukkit.plugin.java.JavaPlugin;
import ro.ggez.containerprotect.commands.ContainerInfo;
import ro.ggez.containerprotect.commands.ContainerModify;
import ro.ggez.containerprotect.commands.ContainerRemove;
import ro.ggez.containerprotect.listeners.ProtectionAddListener;
import ro.ggez.containerprotect.listeners.ProtectionPreventDestroyListener;
import ro.ggez.containerprotect.listeners.ProtectionPreventInteractListener;

public class PluginMain extends JavaPlugin {

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(new ProtectionAddListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionPreventDestroyListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionPreventInteractListener(this), this);

        var containerInfo = new ContainerInfo(this);
        getServer().getPluginManager().registerEvents(containerInfo, this);
        getCommand("cinfo").setExecutor(containerInfo);

        var containerModify = new ContainerModify(this);
        getServer().getPluginManager().registerEvents(containerModify, this);
        getCommand("cmodify").setExecutor(containerModify);

        var containerRemove = new ContainerRemove(this);
        getServer().getPluginManager().registerEvents(containerRemove, this);
        getCommand("cremove").setExecutor(containerRemove);

    }

}
