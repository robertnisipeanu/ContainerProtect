package com.nisipeanu.containerprotect.sdk;

import org.bukkit.OfflinePlayer;

public interface ContainerAllowed {

    /**
     * ContainerProtect will instantiate an object of this class and call the setValue method when `/cmodify prefix:value` is used, with `value` as param
     *
     * @param value
     */
    void setValue(String value);

    /**
     * Is this player allowed to interact with the container?
     * @param player
     * @return
     */
    boolean isAllowed(OfflinePlayer player);

    /**
     * Get a nice looking display name. It will be used when displaying who's allowed (e.g. on /cinfo)
     * @return
     */
    String getDisplayName();

    byte[] serialize();
    void deserialize(byte[] value);

}
