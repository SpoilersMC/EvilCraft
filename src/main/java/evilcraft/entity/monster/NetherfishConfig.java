package evilcraft.entity.monster;

import net.minecraft.client.renderer.entity.Render;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.client.render.entity.RenderNetherfish;
import evilcraft.core.config.ConfigurableProperty;
import evilcraft.core.config.ConfigurableTypeCategory;
import evilcraft.core.config.extendedconfig.MobConfig;
import evilcraft.core.helper.RenderHelpers;

/**
 * Config for the {@link Netherfish}.
 * @author rubensworks
 */
public class NetherfishConfig extends MobConfig {

    /**
     * The unique instance.
     */
    public static NetherfishConfig _instance;

    /**
     * Should the Netherfish be enabled?
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.MOB, comment = "Should the Netherfish be enabled?", requiresMcRestart = true)
    public static boolean isEnabled = true;

    /**
     * Make a new instance.
     */
    public NetherfishConfig() {
        super(true, "netherfish", null, Netherfish.class);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public int getBackgroundEggColor() {
        return RenderHelpers.RGBToInt(73, 27, 20);
    }

    @Override
    public int getForegroundEggColor() {
        return RenderHelpers.RGBToInt(160, 45, 27);
    }

    @Override @SideOnly(Side.CLIENT)
    public Render getRender() {
        return new RenderNetherfish(this);
    }
}