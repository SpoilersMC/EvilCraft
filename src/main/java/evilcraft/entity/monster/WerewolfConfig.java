package evilcraft.entity.monster;

import net.minecraft.client.renderer.entity.Render;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.client.render.entity.RenderWerewolf;
import evilcraft.client.render.model.ModelWerewolf;
import evilcraft.core.config.ConfigurableProperty;
import evilcraft.core.config.ConfigurableTypeCategory;
import evilcraft.core.config.extendedconfig.MobConfig;
import evilcraft.core.helper.RenderHelpers;

/**
 * Config for the {@link Werewolf}.
 * @author rubensworks
 */
public class WerewolfConfig extends MobConfig {

    /**
     * The unique instance.
     */
    public static WerewolfConfig _instance;

    /**
     * Should the Werewolf be enabled?
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.MOB, comment = "Should the Werewolf be enabled?", requiresMcRestart = true)
    public static boolean isEnabled = true;

    /**
     * Make a new instance.
     */
    public WerewolfConfig() {
        super(true, "werewolf", null, Werewolf.class);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public int getBackgroundEggColor() {
        return RenderHelpers.RGBToInt(105, 67, 18);
    }

    @Override
    public int getForegroundEggColor() {
        return RenderHelpers.RGBToInt(57, 25, 10);
    }

    @Override @SideOnly(Side.CLIENT)
    public Render getRender() {
        return new RenderWerewolf(this, new ModelWerewolf(), 0.5F);
    }
}