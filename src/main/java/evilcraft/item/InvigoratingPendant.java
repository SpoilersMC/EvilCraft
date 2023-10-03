package evilcraft.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.Optional;
import evilcraft.Reference;
import evilcraft.core.config.configurable.ConfigurableDamageIndicatedItemFluidContainer;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.config.extendedconfig.ItemConfig;
import evilcraft.core.helper.MinecraftHelpers;
import evilcraft.core.helper.WorldHelpers;
//import evilcraft.core.helper.obfuscation.ObfuscationHelpers;
import evilcraft.fluid.Blood;
import evilcraft.modcompat.baubles.BaublesModCompat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.Iterator;

/**
 * Ring that can enable sight into the vengeance spirit realm.
 * @author rubensworks
 *
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = Reference.MOD_BAUBLES, striprefs = true)
public class InvigoratingPendant extends ConfigurableDamageIndicatedItemFluidContainer implements IBauble {

	private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 2;
	
    private static InvigoratingPendant _instance = null;
    
    /**
     * Initialise the configurable.
     * @param eConfig The config.
     */
    public static void initInstance(ExtendedConfig<ItemConfig> eConfig) {
        if(_instance == null)
            _instance = new InvigoratingPendant(eConfig);
        else
            eConfig.showDoubleInitError();
    }
    
    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static InvigoratingPendant getInstance() {
        return _instance;
    }

    private InvigoratingPendant(ExtendedConfig<ItemConfig> eConfig) {
        super(eConfig, InvigoratingPendantConfig.capacity, Blood.getInstance());
        this.setMaxStackSize(1);
    }
    
    /**
     * Clear the bad effects of given player.
     * Each 'tick', a certain amount of bad effect duration reduction is reserved.
     * Each found effect it's duration is reduced by as much as possible (not larger than the reserved amount)
     * and the inner tank is drained according to how much was reduced.
     * If the reserved duration is not zero at the end, the next bad effect will be taken.
     * @param itemStack The pendant to drain.
     * @param player The player to receive the powers.
     */
    public void clearBadEffects(ItemStack itemStack, EntityPlayer player) {
    	int amount = InvigoratingPendantConfig.usage;
    	if(canConsume(amount, itemStack, player)) {
    		
    		int originalReducableDuration = InvigoratingPendantConfig.reduceDuration * MinecraftHelpers.SECOND_IN_TICKS;
    		int reducableDuration = originalReducableDuration;
    		
	    	@SuppressWarnings("unchecked")
			Iterator<PotionEffect> potionList = Lists.newLinkedList(player.getActivePotionEffects()).iterator();
	    	while(reducableDuration > 0 && potionList.hasNext() && canConsume(amount, itemStack, player)) {
	    		PotionEffect effect = potionList.next();
	    		int potionID = effect.getPotionID();
	    		
	    		boolean shouldClear = true;
	    		if(potionID >= 0 && potionID < Potion.potionTypes.length) {
	    			//shouldClear = ObfuscationHelpers.isPotionBadEffect(Potion.potionTypes[potionID]);
	    		    shouldClear = Potion.potionTypes[potionID].isBadEffect;
	    		}
                shouldClear = shouldClear & !effect.getIsAmbient();
	    		
	    		if(shouldClear) {	    			
	    			int reductionMultiplier = effect.getAmplifier() + 1;
	    			int reducableDurationForThisEffect = reducableDuration / reductionMultiplier;
	    			int remaining = effect.getDuration();
	    			int toReduce = Math.min(reducableDurationForThisEffect, remaining);
	    			int toDrain = amount;
	    			
	    			reducableDuration -= toReduce;
	    			if(remaining == toReduce) {
	    				player.removePotionEffect(potionID);
	    			} else {
	    			    effect.duration = remaining - toReduce;
	    				//ObfuscationHelpers.setPotionEffectDuration(effect, remaining - toReduce);
	    				player.onChangedPotionEffect(effect, true);
                        //ObfuscationHelpers.onChangedPotionEffect(player, effect, true);
	    				toDrain = (int)Math.ceil((double)(reductionMultiplier * amount) * ((double)toReduce / (double)originalReducableDuration));
	    			}
	    			consume(toDrain, itemStack, player);
	    		}
	    	}
    	}

        if(InvigoratingPendantConfig.fireUsage >= 0 && player.isBurning() && canConsume(InvigoratingPendantConfig.fireUsage, itemStack, player)) {
            player.extinguish();
            consume(InvigoratingPendantConfig.fireUsage, itemStack, player);
        }
	}
    
	@Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
        if(entity instanceof EntityPlayer && WorldHelpers.efficientTick(world, TICK_MODULUS, entity.getEntityId())) {
        	clearBadEffects(itemStack, (EntityPlayer) entity);
        }
        super.onUpdate(itemStack, world, entity, par4, par5);
    }

	@Optional.Method(modid = Reference.MOD_BAUBLES)
	@Override
	public boolean canEquip(ItemStack itemStack, EntityLivingBase entity) {
		return BaublesModCompat.canUse();
	}

	@Optional.Method(modid = Reference.MOD_BAUBLES)
	@Override
	public boolean canUnequip(ItemStack itemStack, EntityLivingBase entity) {
		return true;
	}

	@Optional.Method(modid = Reference.MOD_BAUBLES)
	@Override
	public BaubleType getBaubleType(ItemStack itemStack) {
		return BaubleType.AMULET;
	}

	@Optional.Method(modid = Reference.MOD_BAUBLES)
	@Override
	public void onEquipped(ItemStack itemStack, EntityLivingBase entity) {
		
	}

	@Optional.Method(modid = Reference.MOD_BAUBLES)
	@Override
	public void onUnequipped(ItemStack itemStack, EntityLivingBase entity) {
		
	}

	@Optional.Method(modid = Reference.MOD_BAUBLES)
	@Override
	public void onWornTick(ItemStack itemStack, EntityLivingBase entity) {
		if(BaublesModCompat.canUse()) {
			this.onUpdate(itemStack, entity.worldObj, entity, 0, false);
		}
	}
}