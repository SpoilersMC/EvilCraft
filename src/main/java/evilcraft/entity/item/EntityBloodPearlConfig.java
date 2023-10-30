package evilcraft.entity.item;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderSnowball;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.core.config.extendedconfig.EntityConfig;
import evilcraft.item.BloodPearlOfTeleportation;

/**
 * Config for the {@link EntityBloodPearl}.
 * @author rubensworks
 */
public class EntityBloodPearlConfig extends EntityConfig {

    /**
     * The unique instance.
     */
    public static EntityBloodPearlConfig _instance;

    /**
     * Make a new instance.
     */
    public EntityBloodPearlConfig() {
        super(true, "entityBloodPearl", null, EntityBloodPearl.class);
    }

    @Override @SideOnly(Side.CLIENT)
    public Render getRender() {
        return new RenderSnowball(BloodPearlOfTeleportation.getInstance());
    }

    @Override
    public boolean sendVelocityUpdates() {
        return true;
    }
}