package evilcraft.entity.item;

import net.minecraft.client.renderer.entity.Render;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.core.client.render.RenderThrowable;
import evilcraft.core.config.extendedconfig.EntityConfig;
import evilcraft.item.WeatherContainer;

/**
 * Config for the {@link EntityWeatherContainer}.
 * @author rubensworks
 */
public class EntityWeatherContainerConfig extends EntityConfig {

    /**
     * The unique instance.
     */
    public static EntityWeatherContainerConfig _instance;

    /**
     * Make a new instance.
     */
    public EntityWeatherContainerConfig() {
        super(true, "entityWeatherContainer", null, EntityWeatherContainer.class);
    }

    @Override @SideOnly(Side.CLIENT)
    public Render getRender() {
        return new RenderThrowable(WeatherContainer.getInstance());
    }

    @Override
    public boolean sendVelocityUpdates() {
        return true;
    }
}