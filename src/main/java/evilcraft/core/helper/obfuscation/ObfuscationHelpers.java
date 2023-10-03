/*package evilcraft.core.helper.obfuscation;

import cpw.mods.fml.relauncher.ReflectionHelper;
import evilcraft.core.helper.RenderHelpers;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Helper for getting private fields or methods.
 * @author rubensworks
 *
 *//*
public class ObfuscationHelpers {

	/**
	 * Get the private 'particleTextures' field from {@link net.minecraft.client.particle.EffectRenderer}.
	 * @return The private 'particleTextures' field.
	 */
	//public static ResourceLocation getParticleTexture() {
	//	return ReflectionHelper.getPrivateValue(EffectRenderer.class, null, ObfuscationData.PARTICLE_TEXTURES);
	//}
	
	/**
	 * Set the private 'recentlyHit' field from {@link net.minecraft.entity.EntityLivingBase}.
	 * @param entity The entity instance.
	 * @param recentlyHit The recently hit value to set.
	 */
	//public static void setRecentlyHit(EntityLivingBase entity, int recentlyHit) {
	//	ReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, recentlyHit, ObfuscationData.ENTITYLIVINGBASE_RECENTLYHIT);
	//}
	
	/**
	 * Call the protected getter 'getDeathSound' {@link net.minecraft.entity.EntityLivingBase}.
	 * @param entity The entity instance.
	 * @return The death sound.
	 *//*
	public static String getDeathSound(EntityLivingBase entity) {
		Method method = ReflectionHelper.findMethod(EntityLivingBase.class, entity, ObfuscationData.ENTITYLIVINGBASE_GETDEATHSOUND);
		try {
			return (String) method.invoke(entity);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
	/**
	 * Call the protected getter 'getLivingSound' {@link net.minecraft.entity.EntityLiving}.
	 * @param entity The entity instance.
	 * @return The living sound.
	 *//*
	public static String getLivingSound(EntityLiving entity) {
		Method method = ReflectionHelper.findMethod(EntityLiving.class, entity, ObfuscationData.ENTITYLIVING_GETLIVINGSOUND);
		try {
			return (String) method.invoke(entity);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
	/**
	 * Set the private 'itemInUseCount' field from {@link net.minecraft.entity.player.EntityPlayer}.
	 * @param player The player.
	 * @return itemInUseCount
	 */
	//public static int getItemInUseCount(EntityPlayer player) {
	//	return ReflectionHelper.getPrivateValue(EntityPlayer.class, player, ObfuscationData.ENTITYPLAYER_ITEMINUSECOUNT);
	//}
	
	/**
	 * Get the private 'itemInUse' field from {@link net.minecraft.entity.player.EntityPlayer}.
	 * @param player The player.
	 * @return itemInUse
	 */
	//public static ItemStack getItemInUse(EntityPlayer player) {
	//	return ReflectionHelper.getPrivateValue(EntityPlayer.class, player, ObfuscationData.ENTITYPLAYER_ITEMINUSE);
	//}
	
	/**
	 * Set the private static 'titlePanoramaPaths' field from @link{net.minecraft.client.gui.GuiMainMenu}
	 * @param titlePanoramaPaths The panorama path.
	 *//*
	public static void setTitlePanoramaPaths(ResourceLocation[] titlePanoramaPaths) {
		Field field = ReflectionHelper.findField(GuiMainMenu.class, ObfuscationData.GUIMAINMENU_TITLEPANORAMAPATHS);
		
		Field modifiersField;
		try {
			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
	        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

	        field.setAccessible(true);
	        field.set(null, titlePanoramaPaths);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}*/
	
	/**
	 * Get the renderFace method for the given side in {@link net.minecraft.client.renderer.RenderBlocks}.
	 * @param renderer The block renderer.
	 * @param direction The side.
	 * @return The method.
	 */
	/*public static Method getRenderFaceMethod(RenderBlocks renderer, ForgeDirection direction) {
		Method method = ReflectionHelper.findMethod(RenderBlocks.class, renderer,
				ObfuscationData.RENDERBLOCKS_RENDERFACE.get(direction), Block.class, double.class, double.class, double.class, IIcon.class);
		return method;
	}*/
	
	/**
	 * Set a private 'uvRotate' field from {@link net.minecraft.client.renderer.RenderBlocks}
	 * depending on which direction you specified.
	 * Note that this does not contain the MC side number bug fix, for this, please use {@link RenderHelpers}.
	 * @param renderer The block renderer.
	 * @param direction The side.
	 * @param rotation The rotation to set.
	 */
	//public static void setUVRotate(RenderBlocks renderer, ForgeDirection direction, int rotation) {
	//	ReflectionHelper.setPrivateValue(RenderBlocks.class, renderer, rotation,ObfuscationData.RENDERBLOCKS_UVROTATE.get(direction));
	//}
	
	/**
	 * Set the private 'duration' field from {@link net.minecraft.potion.PotionEffect}.
	 * @param potionEffect The potionEffect instance.
	 * @param duration The duration value to set.
	 */
	//public static void setPotionEffectDuration(PotionEffect potionEffect, int duration) {
	//	ReflectionHelper.setPrivateValue(PotionEffect.class, potionEffect, duration, ObfuscationData.POTIONEFFECT_DURATION);
	//}
	
	/**
	 * Call the protected method 'onChangedPotionEffect' {@link net.minecraft.entity.EntityLivingBase}.
	 * @param entity The entity instance.
	 * @param potionEffect The potion effect.
	 * @param reapplyAttributes If the datawatcher attributes need to be updated.
	 *//*
	public static void onChangedPotionEffect(EntityLivingBase entity, PotionEffect potionEffect, boolean reapplyAttributes) {
		Method method = ReflectionHelper.findMethod(EntityLivingBase.class, entity,
				ObfuscationData.ENTITYLIVINGBASE_ONCHANGEDPOTIONEFFECT, PotionEffect.class, boolean.class);
		try {
			method.invoke(entity, potionEffect, reapplyAttributes);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}*/
	
	/**
	 * Get the private 'classToIDMapping' field from {@link net.minecraft.entity.EntityList}.
	 * @return classToIDMapping
	 */
	//public static Map<Class<Entity>, Integer> getClassToID() {
	//	return ReflectionHelper.getPrivateValue(EntityList.class, null, ObfuscationData.ENTITYLIST_CLASSTOID);
	//}
	
	/**
	 * Get the private 'isBadEffect' field from {@link net.minecraft.potion.Potion}.
	 * @param potion The potion instance.
	 * @return isBadEffect
	 */
	//public static boolean isPotionBadEffect(Potion potion) {
	//	return ReflectionHelper.getPrivateValue(Potion.class, potion, ObfuscationData.POTION_ISBADEFFECT);
	//}

    /**
     * Set the private static final 'potionTypes' field from @link{net.minecraft.potion.Potion}
     * @param potionTypes The panorama path.
     *//*
    public static void setPotionTypesArray(Potion[] potionTypes) {
        Field field = ReflectionHelper.findField(Potion.class, ObfuscationData.POTION_POTIONTYPES);

        Field modifiersField;
        try {
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.setAccessible(true);
            field.set(null, potionTypes);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * Get the private 'enableRain' field from {@link net.minecraft.world.biome.BiomeGenBase}.
     * @param biome The biome instance.
     * @return enableRain
     */
    //public static boolean isRainingEnabled(BiomeGenBase biome) {
    //    return ReflectionHelper.getPrivateValue(BiomeGenBase.class, biome, ObfuscationData.BIOME_ENABLERAIN);
    //}

    /**
     * Get the private 'width' field from {@link net.minecraftforge.oredict.ShapedOreRecipe}.
     * @param recipe The recipe instance.
     * @return width
     */
    //public static int getShapedOreRecipeWidth(ShapedOreRecipe recipe) {
    //    return ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, recipe, ObfuscationData.SHAPEDORERECIPE_WIDTH);
    //}

    /**
     * Get the private 'height' field from {@link net.minecraftforge.oredict.ShapedOreRecipe}.
     * @param recipe The recipe instance.
     * @return width
     */
    //public static int getShapedOreRecipeHeight(ShapedOreRecipe recipe) {
    //    return ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, recipe, ObfuscationData.SHAPEDORERECIPE_HEIGHT);
    //}
	
//}
