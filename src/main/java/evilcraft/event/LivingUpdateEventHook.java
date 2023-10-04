package evilcraft.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import evilcraft.Configs;
import evilcraft.ExtendedDamageSource;
import evilcraft.GeneralConfig;
import evilcraft.block.ExcrementPile;
import evilcraft.block.ExcrementPileConfig;
import evilcraft.core.helper.WorldHelpers;
import evilcraft.entity.monster.Werewolf;
import evilcraft.entity.villager.WerewolfVillagerConfig;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

/**
 * Event hook for {@link LivingUpdateEvent}.
 * @author rubensworks
 *
 */
public class LivingUpdateEventHook {

    private static final int CHANCE_DROP_EXCREMENT = 500; // Real chance is 1/CHANCE_DROP_EXCREMENT
    private static final int CHANCE_DIE_WITHOUT_ANY_REASON = 1000000; // Real chance is 1/CHANCE_DIE_WITHOUT_ANY_REASON

    /**
     * When a sound event is received.
     * @param event The received event.
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onLivingUpdate(LivingUpdateEvent event) {
    	if(WorldHelpers.efficientTick(event.entity.worldObj, 80)) {
	        dropExcrement(event);
	        dieWithoutAnyReason(event);
	        transformWerewolfVillager(event);
    	}
    }

    private void dropExcrement(LivingUpdateEvent event) {
        if(event.entity instanceof EntityAnimal && Configs.isEnabled(ExcrementPileConfig.class)
                && !event.entity.worldObj.isRemote && event.entity.worldObj.rand.nextInt(CHANCE_DROP_EXCREMENT) == 0) {
            EntityAnimal entity = (EntityAnimal)event.entity;
            World world = entity.worldObj;
            int x = MathHelper.floor_double(entity.posX);
            int y = MathHelper.floor_double(entity.posY);
            int z = MathHelper.floor_double(entity.posZ);
            if(world.getBlock(x, y, z) == Blocks.air && world.getBlock(x, y - 1, z).isNormalCube()) {
                world.setBlock(x, y, z, ExcrementPile.getInstance());
            } else if(world.getBlock(x, y, z) == ExcrementPile.getInstance()) {
                ExcrementPile.heightenPileAt(world, x, y, z);
            }
        }
    }

    private void dieWithoutAnyReason(LivingUpdateEvent event) {
        if(event.entity instanceof EntityPlayer && GeneralConfig.dieWithoutAnyReason
                && !event.entity.worldObj.isRemote && event.entity.worldObj.rand.nextInt(CHANCE_DIE_WITHOUT_ANY_REASON) == 0) {
            EntityPlayer entity = (EntityPlayer)event.entity;
            entity.attackEntityFrom(ExtendedDamageSource.dieWithoutAnyReason, Float.MAX_VALUE);
        }
    }
 
    private void transformWerewolfVillager(LivingUpdateEvent event) {
        if(event.entity instanceof EntityVillager && !event.entity.worldObj.isRemote) {
            EntityVillager villager = (EntityVillager)event.entity;
            if(Werewolf.isWerewolfTime(event.entity.worldObj) && Configs.isEnabled(WerewolfVillagerConfig.class)
                    && villager.getProfession() == WerewolfVillagerConfig._instance.getId()) {
                Werewolf.replaceVillager(villager);
            }
        }
    }
}