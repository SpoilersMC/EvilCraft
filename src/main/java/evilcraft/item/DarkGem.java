package evilcraft.item;

import evilcraft.Configs;
import evilcraft.block.BloodStainedBlock;
import evilcraft.block.DarkOre;
import evilcraft.block.FluidBlockBlood;
import evilcraft.core.config.configurable.ConfigurableItem;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.config.extendedconfig.ItemConfig;
import evilcraft.core.helper.WorldHelpers;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Gem that drops from {@link DarkOre}.
 * @author rubensworks
 */
public class DarkGem extends ConfigurableItem {

    private static DarkGem _instance = null;
    private static final int REQUIRED_BLOOD_BLOCKS = 5;
    private static final int TICK_MODULUS = 5;

    /**
     * Initialize the configurable.
     * @param eConfig The config.
     */
    public static void initInstance(ExtendedConfig<ItemConfig> eConfig) {
        if(_instance == null)
            _instance = new DarkGem(eConfig);
        else
            eConfig.showDoubleInitError();
    }

    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static DarkGem getInstance() {
        return _instance;
    }

    private DarkGem(ExtendedConfig<ItemConfig> eConfig) {
        super(eConfig);
    }

    @Override
    public boolean onEntityItemUpdate(final EntityItem entityItem) {
        // This will transform a dark gem into a blood infusion core when it finds
        // REQUIRED_BLOOD_BLOCKS blood fluid blocks in the neighborhood.
        if(Configs.isEnabled(BloodInfusionCoreConfig.class) && !entityItem.worldObj.isRemote && WorldHelpers.efficientTick(entityItem.worldObj, TICK_MODULUS, (int)entityItem.posX, (int)entityItem.posY, (int)entityItem.posZ)) {
            final int x = MathHelper.floor_double(entityItem.posX);
            final int y = MathHelper.floor_double(entityItem.posY);
            final int z = MathHelper.floor_double(entityItem.posZ);
            World world = entityItem.worldObj;

            if(isValidBlock(world, x, y, z)) {
                // For storing REQUIRED_BLOOD_BLOCKS coordinates
                final int[] xs = new int[REQUIRED_BLOOD_BLOCKS];
                final int[] ys = new int[REQUIRED_BLOOD_BLOCKS];
                final int[] zs = new int[REQUIRED_BLOOD_BLOCKS];

                // Save first coordinate
                xs[0] = x;
                ys[0] = y;
                zs[0] = z;

                // Search in neighborhood
                WorldHelpers.foldArea(world, 3, x, y, z, new WorldHelpers.WorldFoldingFunction<Integer, Integer>() {
                    @Override @Nullable
                    public Integer apply(@Nullable Integer amount, World world, int xi, int yi, int zi) {
                        if(amount == null || amount == -1)
                            return amount;
                        if(!(xi == x && yi == y && zi == z) && isValidBlock(world, xi, yi, zi)) {
                            // Save next coordinate
                            xs[amount] = xi;
                            ys[amount] = yi;
                            zs[amount] = zi;

                            // Do the transform when REQUIRED_BLOOD_BLOCKS are found
                            if(++amount == REQUIRED_BLOOD_BLOCKS) {
                                // Spawn the new item
                                entityItem.getEntityItem().stackSize--;
                                entityItem.dropItem(DarkPowerGemConfig._instance.getItemInstance(), 1);

                                // Retrace coordinate steps and remove all those blocks + spawn particles
                                for(int restep = 0; restep < amount; restep++) {
                                    world.setBlockToAir(xs[restep], ys[restep], zs[restep]);
                                    if(world.isRemote)
                                        BloodStainedBlock.splash(world, xs[restep], ys[restep] - 1, zs[restep]);
                                    world.notifyBlocksOfNeighborChange(xs[restep], ys[restep], zs[restep], Blocks.air);
                                }
                                return -1;
                            }
                        }
                        return amount;
                    }
                }, 1);
            }
        }
        return false;
    }

    private boolean isValidBlock(IBlockAccess world, int x, int y, int z) {
        return world.getBlock(x, y, z) == FluidBlockBlood.getInstance() && FluidBlockBlood.getInstance().isSourceBlock(world, x, y, z);
    }
}