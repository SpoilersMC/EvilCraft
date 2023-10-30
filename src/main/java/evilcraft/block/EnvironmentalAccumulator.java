package evilcraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.api.ILocation;
import evilcraft.api.RegistryManager;
import evilcraft.api.recipes.custom.IMachine;
import evilcraft.api.recipes.custom.IRecipeRegistry;
import evilcraft.api.recipes.custom.ISuperRecipeRegistry;
import evilcraft.client.render.block.RenderEnvironmentalAccumulator;
import evilcraft.core.config.configurable.ConfigurableBlockContainer;
import evilcraft.core.config.extendedconfig.BlockConfig;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.helper.WorldHelpers;
import evilcraft.core.recipe.custom.EnvironmentalAccumulatorRecipeComponent;
import evilcraft.core.recipe.custom.EnvironmentalAccumulatorRecipeProperties;
import evilcraft.item.EnvironmentalAccumulationCoreConfig;
import evilcraft.tileentity.TileEnvironmentalAccumulator;
import evilcraft.world.gen.DarkTempleGenerator;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Block that can collect the weather and stuff.
 * @author immortaleeb
 */
public class EnvironmentalAccumulator extends ConfigurableBlockContainer 
    implements IMachine<EnvironmentalAccumulator, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties> {

    private static EnvironmentalAccumulator _instance = null;

    /**
     * Initialise the configurable.
     * @param eConfig The config.
     */
    public static void initInstance(ExtendedConfig<BlockConfig> eConfig) {
        if(_instance == null)
            _instance = new EnvironmentalAccumulator(eConfig);
        else
            eConfig.showDoubleInitError();
    }

    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static EnvironmentalAccumulator getInstance() {
        return _instance;
    }

    /**
     * State indicating the environmental accumulator is idle.
     */
    public static final int STATE_IDLE = 0;
    /**
     * State indicating the environmental accumulator is currently processing an item.
     */
    public static final int STATE_PROCESSING_ITEM = 1;
    /**
     * State indicating the environmental accumulator is cooling down.
     */
    public static final int STATE_COOLING_DOWN = 2;
    /**
     * State indicating the environmental accumulator has just finished processing an item.
     * This state is necessary because using this state we can put some delay between processing an item
     * and cooling down so that the client gets a moment to show an effect when an item has finished processing.
     */
    public static final int STATE_FINISHED_PROCESSING_ITEM = 3;

    private IIcon sideIcon;
    private IIcon bottomIcon;
    private IIcon topIcon;

    private EnvironmentalAccumulator(ExtendedConfig<BlockConfig> eConfig) {
        super(eConfig, Material.iron, TileEnvironmentalAccumulator.class);
        this.setRotatable(true);
        this.setStepSound(soundTypeMetal);
        this.setHardness(50.0F);
        this.setResistance(6000000.0F); // Can not be destroyed by explosions
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return RenderEnvironmentalAccumulator.ID;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if(side == ForgeDirection.UP.ordinal())
            return topIcon;

        if(side == ForgeDirection.DOWN.ordinal())
            return bottomIcon;

        return sideIcon;
    }

    @Override @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        sideIcon = iconRegister.registerIcon(getTextureName() + "_side");
        bottomIcon = iconRegister.registerIcon(getTextureName() + "_bottom");
        topIcon = iconRegister.registerIcon(getTextureName() + "_top");
    }

    @Override
    public Item getItemDropped(int meta, Random random, int zero) {
        return EnvironmentalAccumulationCoreConfig._instance.getItemInstance();
    }

    @Override
    public IRecipeRegistry<EnvironmentalAccumulator, EnvironmentalAccumulatorRecipeComponent,
            EnvironmentalAccumulatorRecipeComponent, EnvironmentalAccumulatorRecipeProperties> getRecipeRegistry() {
        return RegistryManager.getRegistry(ISuperRecipeRegistry.class).getRecipeRegistry(this);
    }

    @Override
    protected void onPreBlockDestroyed(World world, int x, int y, int z) {
        if(!world.isRemote) {
            ILocation closest = DarkTempleGenerator.getClosestForCoords(world, x, z);
            if(closest != null) {
                DarkTempleGenerator.getCachedData(world).addFailedLocation(closest.getCoordinates()[0] / WorldHelpers.CHUNK_SIZE, closest.getCoordinates()[2] / WorldHelpers.CHUNK_SIZE);
            }
        }
        super.onPreBlockDestroyed(world, x, y, z);
    }
}