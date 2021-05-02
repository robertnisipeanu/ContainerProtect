package ro.ggez.containerprotect.checkers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Chest;

import java.util.EnumSet;
import java.util.Set;

public class DoubleChestChecker {

    private static final Set<Material> CHEST_TYPES = EnumSet.of(Material.CHEST, Material.TRAPPED_CHEST);
    private static final BlockFace[] FACES = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

    public static Block getNeighboringChest(Block block) {

        // Check if block can be casted to Chest
        Chest chestData;
        try {
            chestData = (Chest) block.getBlockData();
        } catch (ClassCastException ex) {
            return null;
        }

        // Get the block face for neighboring chest
        var blockFace = getNeighboringChestBlockFace(chestData);
        if (blockFace == null) return null;

        Block neighboringBlock = block.getRelative(blockFace);

        if (block.getType() != neighboringBlock.getType()) return null;

        return neighboringBlock;

    }

    public static BlockFace getNeighboringChestBlockFace(Chest chest) {
        if (chest.getType() == Chest.Type.SINGLE)
            return null;

        BlockFace face = chest.getFacing();
        switch (face) {
            case NORTH:
                return chest.getType() == Chest.Type.LEFT ? BlockFace.EAST : BlockFace.WEST;
            case SOUTH:
                return chest.getType() == Chest.Type.LEFT ? BlockFace.WEST : BlockFace.EAST;
            case EAST:
                return chest.getType() == Chest.Type.LEFT ? BlockFace.SOUTH : BlockFace.NORTH;
            case WEST:
                return chest.getType() == Chest.Type.LEFT ? BlockFace.NORTH : BlockFace.SOUTH;
            default:
                return null;
        }
    }

}
