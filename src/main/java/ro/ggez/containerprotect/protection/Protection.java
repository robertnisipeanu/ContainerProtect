package ro.ggez.containerprotect.protection;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.ggez.containerprotect.PluginMain;
import ro.ggez.containerprotect.data.ContainerProtectData;
import ro.ggez.containerprotect.data.ContainerProtectDataType;
import ro.ggez.containerprotect.data.ProtectionType;

import java.util.ArrayList;

public abstract class Protection {

    protected final PluginMain plugin;

    protected PersistentDataContainer dataContainer;
    protected NamespacedKey key;

    protected ContainerProtectData protectionData;

    protected Protection(PluginMain plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "containerprotect");
    }

    /**
     * Get the data associated with this protection
     *
     * @return ContainerProtectData for this protection or null if no data found
     */
    protected ContainerProtectData getData() {
        if (this.protectionData != null) return this.protectionData;

        if (!(this.dataContainer.has(key, new ContainerProtectDataType()))) return this.protectionData = new ContainerProtectData();

        return this.protectionData = this.dataContainer.get(key, new ContainerProtectDataType());
    }

    /**
     * Check if block can be protected
     *
     * @return true if can be protected, false otherwise
     */
    public abstract boolean canBeProtected();

    /**
     * Get the protection level (ProtectionType) associated with this protection
     * @return ProtectionType for this protection
     */
    public @NotNull ProtectionType getProtectionLevel() {
        return this.getData().protectionType;
    }

    /**
     * Set protection level for this block/entity
     * @param type ProtectionType to set
     */
    public void setProtectionLevel(@NotNull ProtectionType type) {
        this.getData().protectionType = type;
    }

    /**
     * Get Owner for this protection
     * @return OfflinePlayer that owns this protection or null if there is no data for this protection
     */
    public @Nullable OfflinePlayer getOwner() {
        return this.getData().owner;
    }

    /**
     * Set Owner for this protection
     * @param player OfflinePlayer to be set as Owner
     */
    public void setOwner(@NotNull OfflinePlayer player) {
        this.getData().owner = player;
    }

    /**
     * Get a list of players that are allowed to use this protection
     * @return ArrayList of OfflinePlayer that are allowed
     */
    public @NotNull ArrayList<OfflinePlayer> getAllowedList() {
        return new ArrayList<>(this.getData().allowedList);
    }

    /**
     * Set allowed list of players that are allowed to use this protection
     * @param allowed ArrayList of OfflinePlayer to be allowed
     */
    public void setAllowedList(@NotNull ArrayList<OfflinePlayer> allowed) {
        this.getData().allowedList = new ArrayList<>(allowed);
    }

    /**
     * Add a player to the allowed list
     * @param player OfflinePlayer to be allowed
     */
    public void addAllowedList(@NotNull OfflinePlayer player) {
        this.getData().allowedList.add(player);
    }

    /**
     * Save everything to the block/entity
     * Note: Doesn't needs to be called after remove()
     */
    public void save() {
        this.dataContainer.set(this.key, new ContainerProtectDataType(), this.protectionData);
    }

    /**
     * Remove protection.
     * Note: For hoppers/minecart hoppers or any other item that can transfer items to another inventory
     * it is required to use setProtectionLevel instead, with a ProtectionType of None.
     * This is because we check if the owners match on ItemInventoryMoveEvent.
     */
    public void remove() {
        this.dataContainer.remove(key);
    }

    /**
     * Get display name for block/entity (capitalized)
     * @return Display name
     */
    public abstract String getDisplayName();

}
