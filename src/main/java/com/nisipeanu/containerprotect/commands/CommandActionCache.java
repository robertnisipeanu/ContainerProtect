package com.nisipeanu.containerprotect.commands;

import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.javatuples.Pair;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandActionCache {
    private final Map<Player, Pair<ContainerCacheCommand, Object>> activeCommands = ExpiringMap.builder().expiration(30, TimeUnit.SECONDS).build();

    /**
     * Check if activeCommands map contains player with any command
     * @param player
     * @return
     */
    public boolean containsAny(Player player) {
        return this.activeCommands.containsKey(player);
    }

    /**
     * Check if activeCommands map contains matching player and command
     * @param player
     * @param command
     * @return
     */
    public boolean contains(Player player, ContainerCacheCommand command) {
        var result = this.activeCommands.get(player);

        return result != null && command == result.getValue(0);
    }

    /**
     * Add a (player, command) combination + data to activeCommands map
     * @param player
     * @param command
     * @param data
     */
    public void add(Player player, ContainerCacheCommand command, Object data) {
        this.activeCommands.put(player, new Pair<>(command, data));
    }

    /**
     * Add a (player, command) combination (with no data) to activeCommands map
     * @param player
     * @param command
     */
    public void add(Player player, ContainerCacheCommand command) {
        this.add(player, command, null);
    }

    /**
     * Remove a player from the activeCommands map
     * @param player
     */
    public void remove(Player player) {
        this.activeCommands.remove(player);
    }

    /**
     * Return data from (player, command) combination
     * @param player
     * @param command
     */
    public Object get(Player player, ContainerCacheCommand command) {
        var result = this.activeCommands.get(player);

        if (result == null || result.getValue(0) != command) return null;

        return result.getValue(1);
    }
}
