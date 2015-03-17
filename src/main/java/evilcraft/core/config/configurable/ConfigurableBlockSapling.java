package evilcraft.core.config.configurable;

import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.world.gen.WorldGeneratorUndeadTree;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Block extending from a sapling that can hold ExtendedConfigs
 * @author rubensworks
 *
 */
public class ConfigurableBlockSapling extends BlockSapling implements IConfigurable{

    @SuppressWarnings("rawtypes")
    protected ExtendedConfig eConfig = null;

    private WorldGeneratorUndeadTree treeGenerator;

    /**
     * Make a new blockState instance.
     * @param eConfig Config for this blockState.
     * @param material Material of this blockState.
     */
    @SuppressWarnings({ "rawtypes" })
    public ConfigurableBlockSapling(ExtendedConfig eConfig, Material material) {
        this.setConfig(eConfig);
        this.setUnlocalizedName(eConfig.getUnlocalizedName());
        treeGenerator = new WorldGeneratorUndeadTree(true, this);
        setStepSound(soundTypeGrass);
    }

    private void setConfig(@SuppressWarnings("rawtypes") ExtendedConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return eConfig;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, 0));
    }

    //growTree
    @Override
    public void func_176476_e(World world, BlockPos blockPos, IBlockState blockState, Random random) {
        if (world.isRemote) {
            return;
        }

        world.setBlockToAir(blockPos);

        if(!treeGenerator.growTree(world, random, blockPos)) {
            world.setBlockState(blockPos, blockState, 4);
        }
    }

}
