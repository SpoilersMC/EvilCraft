package evilcraft.entity.effect;

import net.minecraft.client.renderer.entity.Render;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.core.client.render.RenderNull;
import evilcraft.core.config.extendedconfig.EntityConfig;

/**
 * Config for the {@link EntityAntiVengeanceBeam}.
 * @author rubensworks
 */
public class EntityAntiVengeanceBeamConfig extends EntityConfig {

    /**
     * The unique instance.
     */
    public static EntityAntiVengeanceBeamConfig _instance;

    /**
     * Make a new instance.
     */
    public EntityAntiVengeanceBeamConfig() {
        super(true, "entityNeutronBeam", null, EntityAntiVengeanceBeam.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

    @Override @SideOnly(Side.CLIENT)
    public Render getRender() {
        return new RenderNull();
    }

    @Override
    public boolean sendVelocityUpdates() {
        return true;
    }
}