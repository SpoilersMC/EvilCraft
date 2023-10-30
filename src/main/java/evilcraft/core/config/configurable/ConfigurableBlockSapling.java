package evilcraft.core.config.configurable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.Reference;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.world.gen.WorldGeneratorUndeadTree;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Block extending from a sapling that can hold ExtendedConfigs
 * @author rubensworks
 */
public class ConfigurableBlockSapling extends BlockSapling implements IConfigurable {

    @SuppressWarnings("rawtypes")
    protected ExtendedConfig eConfig = null;

    private WorldGeneratorUndeadTree treeGenerator;

    /**
     * Make a new block instance.
     * @param eConfig Config for this block.
     * @param material Material of this block.
     */
    @SuppressWarnings("rawtypes")
    public ConfigurableBlockSapling(ExtendedConfig eConfig, Material material) {
        this.setConfig(eConfig);
        this.setBlockName(eConfig.getUnlocalizedName());
        treeGenerator = new WorldGeneratorUndeadTree(true, this);
        setStepSound(soundTypeGrass);
    }

    @SuppressWarnings("rawtypes")
    private void setConfig(ExtendedConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return eConfig;
    }

    @Override
    public String getTextureName() {
        return Reference.MOD_ID + ":" + eConfig.getNamedId();
    }

    @Override @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(getTextureName());
    }

    @Override
    public IIcon getIcon(int par1, int par2) {
        return this.blockIcon;
    }

    @Override @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, 0));
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    // isSameSapling
    @Override
    public boolean func_149880_a(World world, int x, int y, int z, int meta) {
        return world.getBlock(x, y, z) == this && (world.getBlockMetadata(x, y, z)) == meta;
    }

    // growTree
    @Override
    public void func_149878_d(World world, int x, int y, int z, Random random) {
        if(world.isRemote) {
            return;
        }

        world.setBlockToAir(x, y, z);

        if(!treeGenerator.growTree(world, random, x, y, z)) {
            world.setBlock(x, y, z, this, 0, 4);
        }
    }
}