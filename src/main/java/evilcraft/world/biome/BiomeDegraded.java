package evilcraft.world.biome;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.core.config.configurable.ConfigurableBiome;
import evilcraft.core.config.extendedconfig.BiomeConfig;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.helper.RenderHelpers;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;

/**
 * Enchantment for letting tools break tools faster.
 * @author rubensworks
 */
public class BiomeDegraded extends ConfigurableBiome {

    private static BiomeDegraded _instance = null;

    /**
     * Initialise the configurable.
     * @param eConfig The config.
     */
    public static void initInstance(ExtendedConfig<BiomeConfig> eConfig) {
        if(_instance == null)
            _instance = new BiomeDegraded(eConfig);
        else
            eConfig.showDoubleInitError();
    }

    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static BiomeDegraded getInstance() {
        return _instance;
    }

    private BiomeDegraded(ExtendedConfig<BiomeConfig> eConfig) {
        super(eConfig.downCast());
        this.setHeight(height_MidPlains);
        this.setTemperatureRainfall(0.8F, 0.9F);
        this.setColor(RenderHelpers.RGBToInt(0, 30, 20));
        this.func_76733_a(RenderHelpers.RGBToInt(20, 50, 30));
        this.waterColorMultiplier = RenderHelpers.RGBToInt(60, 50, 20);
    }

    @Override @SideOnly(Side.CLIENT)
    public int getBiomeGrassColor(int x, int y, int z) {
        double d0 = (double)MathHelper.clamp_float(this.getFloatTemperature(x, y, z), 0.0F, 1.0F);
        double d1 = (double)MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
        return ((ColorizerGrass.getGrassColor(d0, d1) & RenderHelpers.RGBToInt(10, 20, 5)) + 5115470) / 2;
    }

    @Override @SideOnly(Side.CLIENT)
    public int getBiomeFoliageColor(int x, int y, int z) {
        double d0 = (double)MathHelper.clamp_float(this.getFloatTemperature(x, y, z), 0.0F, 1.0F);
        double d1 = (double)MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
        return ((ColorizerFoliage.getFoliageColor(d0, d1) & RenderHelpers.RGBToInt(10, 20, 50)) + 5115470) / 2;
    }

    @Override
    public float getSpawningChance() {
        return 0.5F;
    }
}