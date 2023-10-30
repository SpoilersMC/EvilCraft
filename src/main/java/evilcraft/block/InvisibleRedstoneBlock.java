package evilcraft.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.Configs;
import evilcraft.core.config.configurable.ConfigurableBlockContainer;
import evilcraft.core.config.extendedconfig.BlockConfig;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.helper.RenderHelpers;
import evilcraft.item.RedstoneGrenade;
import evilcraft.item.RedstoneGrenadeConfig;
import evilcraft.tileentity.TileInvisibleRedstoneBlock;

/**
 * An invisible block where players can walk through and disappears after a few ticks.
 * @author immortaleeb
 */
public class InvisibleRedstoneBlock extends ConfigurableBlockContainer {

    private static InvisibleRedstoneBlock _instance = null;

    /**
     * Initialise the configurable.
     * @param eConfig The config.
     */
    public static void initInstance(ExtendedConfig<BlockConfig> eConfig) {
        if(_instance == null)
            _instance = new InvisibleRedstoneBlock(eConfig);
        else
            eConfig.showDoubleInitError();
    }

    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static InvisibleRedstoneBlock getInstance() {
        return _instance;
    }

    private InvisibleRedstoneBlock(ExtendedConfig<BlockConfig> eConfig) {
        super(eConfig, Material.air, TileInvisibleRedstoneBlock.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setStepSound(soundTypeMetal);
    }

    @Override @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
    }

    @Override
    public Item getItemDropped(int meta, Random random, int zero) {
        if(Configs.isEnabled(RedstoneGrenadeConfig.class)) {
            return RedstoneGrenade.getInstance();
        } else {
            return null;
        }
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return 15;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int getMobilityFlag() {
        return 0;
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileInvisibleRedstoneBlock(world);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return RenderHelpers.EMPTYICON;
    }

    @Override @SuppressWarnings("rawtypes")
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list) {
        // Do not appear in creative tab
    }
}