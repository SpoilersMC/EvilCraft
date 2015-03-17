package evilcraft.entity.item;

import evilcraft.core.config.extendedconfig.EntityConfig;
import evilcraft.item.BloodPearlOfTeleportation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Config for the {@link EntityBloodPearl}.
 * @author rubensworks
 *
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
        super(
        	true,
            "entityBloodPearl",
            null,
            EntityBloodPearl.class
        );
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Render getRender(RenderManager renderManager, RenderItem renderItem) {
        return new RenderSnowball(renderManager, BloodPearlOfTeleportation.getInstance(), renderItem);
    }
    
    @Override
    public boolean sendVelocityUpdates() {
        return true;
    }
    
}
