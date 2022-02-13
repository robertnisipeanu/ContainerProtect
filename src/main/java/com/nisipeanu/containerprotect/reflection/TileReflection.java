package com.nisipeanu.containerprotect.reflection;

import org.bukkit.block.TileState;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles reflection on TileState.
 * Caches the result of the Reflection.
 */
public class TileReflection {

    private static Set<Class<? extends TileState>> tileClasses;

    public static Set<Class<? extends TileState>> getTileClasses() {
        if (tileClasses != null) return tileClasses;

//        var blockClasses = ClassReflection.filterSuperClasses(
//                ClassReflection.getClassesInPackage("org.bukkit.block")
//        ).stream().filter(c -> TileState.class.isAssignableFrom(c));
        var blockClasses = ClassReflection.getClassesInPackage("org.bukkit.block")
                .stream().filter(TileState.class::isAssignableFrom);

        tileClasses = blockClasses.map(c -> (Class<? extends TileState>) c).collect(Collectors.toSet());
        return tileClasses;
    }

    /**
     * Get a Tile Class by name
     *
     * @param name name of class
     * @return A class that extends TileState if found, null otherwise
     */
    public static Class<? extends TileState> getClassByName(String name) {
        return getTileClasses().stream().filter(c -> c.getSimpleName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static Class<? extends TileState> getClassFromTileState(TileState tileState) {
        return getTileClasses().stream().filter(c -> c.isAssignableFrom(tileState.getClass())).findFirst().orElse(null);
    }
}
