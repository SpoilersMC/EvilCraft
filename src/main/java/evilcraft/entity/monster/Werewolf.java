package evilcraft.entity.monster;

import evilcraft.Configs;
import evilcraft.core.config.configurable.IConfigurable;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.helper.MinecraftHelpers;
import evilcraft.entity.villager.WerewolfVillagerConfig;
import evilcraft.item.WerewolfBoneConfig;
import evilcraft.item.WerewolfFurConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.Random;

/**
 * A large werewolf, only appears at night by transforming from a werewolf villager.
 * @author rubensworks
 */
public class Werewolf extends EntityMob implements IConfigurable {

    private NBTTagCompound villagerNBTTagCompound = new NBTTagCompound();
    private boolean fromVillager = false;

    private static int BARKCHANCE = 1000;
    private static int BARKLENGTH = 40;
    private static int barkprogress = -1;

    /**
     * Make a new instance.
     * @param world The world.
     */
    public Werewolf(World world) {
        super(world);

        this.getNavigator().setAvoidsWater(true);
        this.setSize(0.6F, 2.9F);
        this.stepHeight = 1.0F;
        this.isImmuneToFire = false;

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIWander(this, 1.0F));
        this.tasks.addTask(2, new EntityAILookIdle(this));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, true));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false));

        // This sets the default villager profession ID.
        if(Configs.isEnabled(WerewolfVillagerConfig.class)) {
            this.villagerNBTTagCompound.setInteger("Profession", WerewolfVillagerConfig._instance.getId());
        }
    }

    @Override
    protected float getSoundPitch() {
        return super.getSoundPitch() * 0.75F;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(7.0D);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound NBTTagCompound) {
        super.writeEntityToNBT(NBTTagCompound);
        NBTTagCompound.setTag("villager", villagerNBTTagCompound);
        NBTTagCompound.setBoolean("fromVillager", fromVillager);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound NBTTagCompound) {
        super.readEntityFromNBT(NBTTagCompound);
        this.villagerNBTTagCompound = NBTTagCompound.getCompoundTag("villager");
        this.fromVillager = NBTTagCompound.getBoolean("fromVillager");
    }

    /**
     * If at the current time in the given world werewolves can appear.
     * @param world The world.
     * @return If it is werewolf party time.
     */
    public static boolean isWerewolfTime(World world) {
        return world.getCurrentMoonPhaseFactor() == 1.0 && !MinecraftHelpers.isDay(world) && world.difficultySetting != EnumDifficulty.PEACEFUL;
    }

    private static void replaceEntity(EntityLiving old, EntityLiving neww, World world) {
        // TODO: A nice update effect?
        // Maybe something like this: https://github.com/iChun/Morph/blob/master/morph/client/model/ModelMorphAcquisition.java
        neww.copyLocationAndAnglesFrom(old);
        world.removeEntity(old);
        neww.onSpawnWithEgg((IEntityLivingData)null);

        world.spawnEntityInWorld(neww);
        world.playAuxSFXAtEntity((EntityPlayer)null, 1016, (int)old.posX, (int)old.posY, (int)old.posZ, 0);
    }

    /**
     * Replace this entity with the stored villager.
     */
    public void replaceWithVillager() {
        if(Configs.isEnabled(WerewolfVillagerConfig.class)) {
            EntityVillager villager = new EntityVillager(this.worldObj, WerewolfVillagerConfig._instance.getId());
            replaceEntity(this, villager, this.worldObj);
            villager.readEntityFromNBT(villagerNBTTagCompound);
        }
    }

    /**
     * Replace the given villager with a werewolf and store the data of that villager.
     * @param villager The villager to replace.
     */
    public static void replaceVillager(EntityVillager villager) {
        if(Configs.isEnabled(WerewolfConfig.class)) {
            Werewolf werewolf = new Werewolf(villager.worldObj);
            villager.writeEntityToNBT(werewolf.getVillagerNBTTagCompound());
            werewolf.setFromVillager(true);
            replaceEntity(villager, werewolf, villager.worldObj);
        }
    }

    @Override
    public void onLivingUpdate() {
        if(!worldObj.isRemote && (!isWerewolfTime(worldObj) || worldObj.difficultySetting == EnumDifficulty.PEACEFUL)) {
            replaceWithVillager();
        } else {
            super.onLivingUpdate();
        }

        // Random barking
        Random random = worldObj.rand;
        if(random.nextInt(BARKCHANCE) == 0 && barkprogress == -1) {
            barkprogress++;
        } else if(barkprogress > -1) {
            playSound("mob.wolf.growl", 0.15F, 1.0F);
            barkprogress++;
            if(barkprogress > BARKLENGTH) {
                barkprogress = -1;
            }
        }
    }

    /**
     * Get the bark progress scaled to the given parameter.
     * @param scale The scale.
     * @return The scaled progress.
     */
    public float getBarkProgressScaled(float scale) {
        if(barkprogress == -1)
            return 0;
        else
            return (float)barkprogress / (float)BARKLENGTH * scale;
    }

    @Override
    protected Item getDropItem() {
        if(Configs.isEnabled(WerewolfBoneConfig.class))
            return WerewolfBoneConfig._instance.getItemInstance();
        else
            return super.getDropItem();
    }

    @Override
    protected void dropRareDrop(int chance) {
        if(Configs.isEnabled(WerewolfFurConfig.class))
            this.dropItem(WerewolfFurConfig._instance.getItemInstance(), 1);
    }

    @Override
    protected String getLivingSound() {
        return "mob.wolf.growl";
    }

    @Override
    protected String getHurtSound() {
        return "mob.wolf.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.wolf.death";
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block block) { // playStepSound
        this.playSound("mob.zombie.step", 0.15F, 1.0F);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    protected boolean canDespawn() {
        return !isFromVillager();
    }

    /**
     * Get the villager data.
     * @return Villager data.
     */
    public NBTTagCompound getVillagerNBTTagCompound() {
        return villagerNBTTagCompound;
    }

    /**
     * If this werewolf was created from a transforming villager.
     * @return If it was a villager.
     */
    public boolean isFromVillager() {
        return fromVillager;
    }

    /**
     * Set is from villager.
     * @param fromVillager If this werewolf is a transformed villager.
     */
    public void setFromVillager(boolean fromVillager) {
        this.fromVillager = fromVillager;
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return null;
    }
}