package evilcraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.core.config.configurable.ConfigurableItem;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.config.extendedconfig.ItemConfig;
import evilcraft.core.helper.L10NHelpers;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;

/**
 * Egg to hold entities. This is an alternative for mob eggs if an entity does not have one.
 * DISABLED FOR NOW.
 * @author rubensworks
 */
public class ResurgenceEgg extends ConfigurableItem {

    private static final String NBTKEY_ENTITY = "innerEntity";

    private static ResurgenceEgg _instance = null;

    /**
     * Initialize the configurable.
     * @param eConfig The config.
     */
    public static void initInstance(ExtendedConfig<ItemConfig> eConfig) {
        if(_instance == null)
            _instance = new ResurgenceEgg(eConfig);
        else
            eConfig.showDoubleInitError();
    }

    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static ResurgenceEgg getInstance() {
        return _instance;
    }

    private ResurgenceEgg(ExtendedConfig<ItemConfig> eConfig) {
        super(eConfig);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean hasEffect(ItemStack itemStack, int pass) {
        return !isEmpty(itemStack);
    }

    /**
     * Get the ID of an inner entity, can be null.
     * @param itemStack The item stack.
     * @return The ID.
     */
    public String getEntityString(ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if(tag != null) {
            return tag.getString(NBTKEY_ENTITY);
        }
        return null;
    }

    /**
     * Check if the given egg is empty.
     * @param itemStack itemStack The item stack.
     * @return If it is empty.
     */
    public boolean isEmpty(ItemStack itemStack) {
        return getEntityString(itemStack) == null;
    }

    /**
     * Put an entity in this egg.
     * @param itemStack The box.
     * @param entityString The unique string of the entity to set.
     */
    public void setEntity(ItemStack itemStack, String entityString) {
        NBTTagCompound tag = itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
        tag.setString(NBTKEY_ENTITY, entityString);
        itemStack.setTagCompound(tag);
    }

    @Override @SideOnly(Side.CLIENT) @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        String content = EnumChatFormatting.ITALIC + L10NHelpers.localize("general.info.empty");
        String id = getEntityString(itemStack);
        if(id != null) {
            content = L10NHelpers.getLocalizedEntityName(id);
        }
        list.add(EnumChatFormatting.BOLD + L10NHelpers.localize(getUnlocalizedName() + ".info.content", EnumChatFormatting.RESET + content));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if(world.isRemote) {
            return itemStack;
        } else {
            MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, true);
            if(movingobjectposition == null) {
                return itemStack;
            } else {
                if(movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    int x = movingobjectposition.blockX;
                    int y = movingobjectposition.blockY;
                    int z = movingobjectposition.blockZ;

                    if(!world.canMineBlock(player, x, y, z) || !player.canPlayerEdit(x, y, z, movingobjectposition.sideHit, itemStack)) {
                        return itemStack;
                    }

                    if(world.getBlock(x, y, z) instanceof BlockLiquid) {
                        Entity entity = spawnCreature(world, getEntityString(itemStack), (double)x, (double)y, (double)z);
                        if(entity != null) {
                            if(entity instanceof EntityLivingBase && itemStack.hasDisplayName()) {
                                ((EntityLiving)entity).setCustomNameTag(itemStack.getDisplayName());
                            }
                            if(!player.capabilities.isCreativeMode) {
                                --itemStack.stackSize;
                            }
                        }
                    }
                }
                return itemStack;
            }
        }
    }

    /**
     * Spawn a creature.
     * @param world The world.
     * @param entityString The unique entity string.
     * @param x X
     * @param y Y
     * @param z Z
     * @return The spawned entity, could be null if not spawnable.
     */
    public static Entity spawnCreature(World world, String entityString, double x, double y, double z) {
        Entity entity = EntityList.createEntityByName(entityString, world);
        if(entity != null && entity instanceof EntityLivingBase) {
            EntityLiving entityliving = (EntityLiving)entity;
            entity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
            entityliving.rotationYawHead = entityliving.rotationYaw;
            entityliving.renderYawOffset = entityliving.rotationYaw;
            entityliving.onSpawnWithEgg((IEntityLivingData)null);
            world.spawnEntityInWorld(entity);
            entityliving.playLivingSound();
        }
        return entity;
    }
}