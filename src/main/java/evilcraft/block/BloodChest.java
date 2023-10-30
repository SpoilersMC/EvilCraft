package evilcraft.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.client.gui.container.GuiBloodChest;
import evilcraft.core.config.configurable.ConfigurableBlockContainerGuiTankInfo;
import evilcraft.core.config.extendedconfig.BlockConfig;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.helper.MinecraftHelpers;
import evilcraft.inventory.container.ContainerBloodChest;
import evilcraft.tileentity.TileBloodChest;

/**
 * A chest that runs on blood and repairs tools.
 * @author rubensworks
 */
public class BloodChest extends ConfigurableBlockContainerGuiTankInfo {

    private static BloodChest _instance = null;

    /**
     * Initialise the configurable.
     * @param eConfig The config.
     */
    public static void initInstance(ExtendedConfig<BlockConfig> eConfig) {
        if(_instance == null)
            _instance = new BloodChest(eConfig);
        else
            eConfig.showDoubleInitError();
    }

    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static BloodChest getInstance() {
        return _instance;
    }

    private BloodChest(ExtendedConfig<BlockConfig> eConfig) {
        super(eConfig, Material.wood, TileBloodChest.class);

        this.setHardness(2.5F);
        this.setStepSound(soundTypeWood);
        this.setRotatable(true);
        setBlockBounds(0.0625F, 0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);

        if(MinecraftHelpers.isClientSide())
            setGUI(GuiBloodChest.class);

        setContainer(ContainerBloodChest.class);
    }

    @Override
    public int getRenderType() {
        return 22;
    }

    @Override @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {

    }

    @Override
    public IIcon getIcon(int side, int meta) {
        // This is ONLY used for the block breaking/broken particles
        // Since the blood infuser looks very similar, we use that icon.
        return BloodInfuser.getInstance().getIcon(side, meta);
    }

    @Override
    public Item getItemDropped(int par1, Random random, int zero) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public String getTankNBTName() {
        return TileBloodChest.TANKNAME;
    }

    @Override
    public int getMaxCapacity() {
        return TileBloodChest.LIQUID_PER_SLOT;
    }
}