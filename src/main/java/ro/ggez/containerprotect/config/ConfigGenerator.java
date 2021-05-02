package ro.ggez.containerprotect.config;

import org.bukkit.block.TileState;
import org.bukkit.configuration.file.FileConfiguration;
import ro.ggez.containerprotect.PluginMain;
import ro.ggez.containerprotect.reflection.TileReflection;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigGenerator {
    private final PluginMain plugin;
    private final FileConfiguration config;

    public ConfigGenerator(PluginMain plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void generate() {

        // Get all blocks extending TileState
        Set<Class<? extends TileState>> tileStates = TileReflection.getTileClasses();

        Map<String, Boolean> protectedBlocks = tileStates.stream().collect(Collectors.toMap(
                (entry) -> entry.getSimpleName().toLowerCase(Locale.ROOT),
                (entry) -> false
        ));

        config.addDefault("blocks", protectedBlocks);

        this.config.options().copyDefaults(true);
        this.plugin.saveConfig();

    }

    /**
     * Filter a list to only contain classes which meets the condition of isTopExtender
     *
     * @param classList
     * @return a list of Classes that meets the requirement
     */
    private Set<Class<? extends TileState>> filter(Set<Class<? extends TileState>> classList) {
        var classes = classList.stream().filter(c -> isTopExtender(c, classList));
        return classes.collect(Collectors.toSet());
    }


    /**
     * Checks if it no other classes from classList extends classToCheck
     *
     * @param classToCheck Class to check if it's the top extender
     * @param classList    List of Classes to check against
     * @return True if no class extends classToCheck, false otherwise
     */
    private boolean isTopExtender(Class<? extends TileState> classToCheck, Set<Class<? extends TileState>> classList) {
        return classList.stream().noneMatch(c -> !c.equals(classToCheck) && classToCheck.isAssignableFrom(c));
    }

}
