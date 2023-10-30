package evilcraft.core.config.configurabletypeaction;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.registry.EntityRegistry;
import evilcraft.EvilCraft;
import evilcraft.core.config.extendedconfig.MobConfig;
import evilcraft.core.helper.Helpers;
import evilcraft.core.helper.MinecraftHelpers;
import evilcraft.core.helper.Helpers.IDType;
import evilcraft.proxy.ClientProxy;

/**
 * The action used for {@link MobConfig}.
 * @author rubensworks
 * @see ConfigurableTypeAction
 */
public class MobAction extends ConfigurableTypeAction<MobConfig> {

    @Override
    public void preRun(MobConfig eConfig, Configuration config, boolean startup) {
    }

    @SuppressWarnings("unchecked") @Override
    public void postRun(MobConfig eConfig, Configuration config) {
        // Save the config inside the correct element
        eConfig.save();

        // Register mob
        Class<? extends EntityLiving> clazz = (Class<? extends EntityLiving>)eConfig.getElement();
        if(MinecraftHelpers.isClientSide())
            ClientProxy.ENTITY_RENDERERS.put(clazz, eConfig.getRender());
        if(eConfig.hasSpawnEgg()) {
            EntityRegistry.registerGlobalEntityID(clazz, eConfig.getNamedId(), EntityRegistry.findGlobalUniqueEntityId(), eConfig.getBackgroundEggColor(), eConfig.getForegroundEggColor());
        } else {
            EntityRegistry.registerGlobalEntityID(clazz, eConfig.getNamedId(), EntityRegistry.findGlobalUniqueEntityId());
        }
        EntityRegistry.registerModEntity(clazz, eConfig.getNamedId(), Helpers.getNewId(IDType.ENTITY), EvilCraft._instance, 80, 3, true);
    }
}