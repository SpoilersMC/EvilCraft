package evilcraft.world.gen.structure;

import evilcraft.api.ILocation;
import evilcraft.core.algorithm.Location;
import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A quarter of a structure that can be rotated three times.
 * @author immortaleeb
 */
public abstract class QuarterSymmetricalStructure {
    private List<Integer> layerHeights;
    private List<BlockWrapper[]> layers;

    // Width and height of a quarter of the structure
    protected int quarterWidth;
    protected int quarterHeight;

    /**
     * Make a new instance with the given dimensions (only one height layer).
     * @param quarterWidth Width of the quarter.
     * @param quarterHeight Height of the quarter.
     */
    public QuarterSymmetricalStructure(int quarterWidth, int quarterHeight) {
        layerHeights = new ArrayList<Integer>();
        layers = new ArrayList<BlockWrapper[]>();
        this.quarterWidth = quarterWidth;
        this.quarterHeight = quarterHeight;
        generateLayers();
    }

    protected abstract void generateLayers();

    protected void addLayer(int height, BlockWrapper[] layer) {
        layerHeights.add(height);
        layers.add(layer);
    }

    protected void buildCorner(World world, int x, int y, int z, int incX, int incZ) {
        Random r = new Random();
        for(int i = 0; i < layerHeights.size(); ++i) {
            int layerHeight = layerHeights.get(i);
            BlockWrapper[] layer = layers.get(i);

            // Don't overwrite the borders everytime we place blocks
            int start = (incX == incZ) ? 0 : 1;

            for(int zr = start; zr < quarterHeight; ++zr) {
                for(int xr = start; xr < quarterWidth; ++xr) {
                    BlockWrapper wrapper = layer[(quarterWidth - xr - 1) * quarterHeight + zr];
                    if(wrapper != null && wrapper.chance >= r.nextFloat()) {
                        world.setBlock(x + xr * incX, y + layerHeight, z + zr * incZ, wrapper.block, wrapper.metadata, 2);
                        if(wrapper.action != null) {
                            wrapper.action.run(world, new Location(x + xr * incX, y + layerHeight, z + zr * incZ));
                        }
                    }
                }
            }
        }
        postBuildCorner(world, x, y, z, incX, incZ);
    }

    protected void postBuildCorner(World world, int x, int y, int z, int incX, int incZ) {
        for(int i = 0; i < layerHeights.size(); ++i) {
            int layerHeight = layerHeights.get(i);
            BlockWrapper[] layer = layers.get(i);

            // Don't overwrite the borders everytime we place blocks
            int start = (incX == incZ) ? 0 : 1;
            for(int zr = start; zr < quarterHeight; ++zr) {
                for(int xr = start; xr < quarterWidth; ++xr) {
                    BlockWrapper wrapper = layer[(quarterWidth - xr - 1) * quarterHeight + zr];
                    if(wrapper != null) {
                        if(wrapper.action != null && world.getBlock(x + xr * incX, y + layerHeight, z + zr * incZ) == wrapper.block) {
                            wrapper.action.run(world, new Location(x + xr * incX, y + layerHeight, z + zr * incZ));
                        }
                    }
                }
            }
        }
    }

    /**
     * Generate this structure.
     * @param world The world.
     * @param random Random object.
     * @param x X center coordinate.
     * @param y Y center coordinate.
     * @param z Z center coordinate.
     * @return If the structure was generated.
     */
    public boolean generate(World world, Random random, int x, int y, int z) {
        buildCorner(world, x, y, z, 1, 1);
        buildCorner(world, x, y, z, -1, 1);
        buildCorner(world, x, y, z, 1, -1);
        buildCorner(world, x, y, z, -1, -1);
        return true;
    }

    /**
     * This is a wrapper class, which wraps around a {@link Block} and pairs with it the metadata for that specific block instance.
     * @author immortaleeb
     */
    public class BlockWrapper {
        /**
         * {@link Block} for which this instance is a wrapper.
         */
        public Block block;
        /**
         * Metadata which should be used for the specific {@link Block}.
         */
        public int metadata;
        /**
         * Chance of spawning
         */
        public float chance = 1;
        /**
         * An optional action after a block has been placed.
         */
        public IBlockAction action = null;

        /**
         * Creates a new wrapper around the specified {@link Block} with metadata 0.
         * @param block The block to wrap.
         */
        public BlockWrapper(Block block) {
            this(block, 0);
        }

        /**
         * Creates a new wrapper around the specified {@link Block} with the specified metadata.
         * @param block The block to wrap.
         * @param metadata The metadata of the block.
         */
        public BlockWrapper(Block block, int metadata) {
            this.block = block;
            this.metadata = metadata;
        }

        /**
         * Creates a new wrapper around the specified {@link Block} with the specified metadata and a certain chance.
         * @param block The block to wrap.
         * @param metadata The metadata of the block.
         * @param chance The chance on spawning.
         */
        public BlockWrapper(Block block, int metadata, float chance) {
            this.block = block;
            this.metadata = metadata;
            this.chance = chance;
        }
    }

    public static interface IBlockAction {

        public void run(World world, ILocation location);
    }
}