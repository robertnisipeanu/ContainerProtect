package com.nisipeanu.containerprotect.protection;

import org.apache.commons.lang.WordUtils;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import com.nisipeanu.containerprotect.PluginMain;
import com.nisipeanu.containerprotect.checkers.DoubleChestChecker;
import com.nisipeanu.containerprotect.reflection.TileReflection;

import java.util.Locale;

public class TileProtection extends Protection {

    private TileState tileState;

    public TileProtection(PluginMain plugin, BlockState blockState) {
        super(plugin);

        if (!(blockState instanceof TileState)) return;

        this.tileState = (TileState) blockState;

        this.dataContainer = this.tileState.getPersistentDataContainer();
    }

    @Override
    public boolean canBeProtected() {
        if (this.tileState == null) return false;

        var tileClass = TileReflection.getClassFromTileState(this.tileState);
        if (tileClass == null) return false;

        return plugin.getConfig().getBoolean("blocks." + tileClass.getSimpleName().toLowerCase(Locale.ROOT));
    }

    @Override
    public String getDisplayName() {
        return WordUtils.capitalizeFully(this.tileState.getType().toString().replace("_", " "));
    }

    @Override
    public void save() {
        this.save(true);
    }

    protected void save(boolean withAdjacent) {
        super.save();
        this.tileState.update();

        if (!withAdjacent) return;

        var adjacentProtection = this.getAdjacentProtection();

        if (adjacentProtection != null) {
            adjacentProtection.setOwner(this.getOwner());
            adjacentProtection.setAllowedList(this.getAllowedList());
            adjacentProtection.setProtectionLevel(this.getProtectionLevel());
            adjacentProtection.save(false);
        }
    }

    @Override
    public void remove() {
        this.remove(true);
    }

    /**
     * Remove protection.
     * Allows to remove protection for this block/entity without deleting it for the adjacent one
     *
     * @param deleteAdjacentToo
     */
    public void remove(boolean deleteAdjacentToo) {
        super.remove();
        this.tileState.update();

        if (!deleteAdjacentToo) return;

        var adjacentProtection = this.getAdjacentProtection();

        if (adjacentProtection != null) {
            adjacentProtection.remove(false);
        }

    }

    /**
     * Get adjacent protection (neighbouring chest if it's a doublechest)
     *
     * @return a ProtectionManager instance for the adjacent block, null if there is no adjacent block or if ignoreAdjacent is set to true
     */
    private TileProtection getAdjacentProtection() {
        // Get adjacent protection if exists
        var adjacentBlock = DoubleChestChecker.getNeighboringChest(this.tileState.getBlock());

        if (adjacentBlock == null) return null;

        return new TileProtection(this.plugin, adjacentBlock.getState());
    }
}
