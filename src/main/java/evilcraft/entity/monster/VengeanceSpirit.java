package evilcraft.entity.monster;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.Configs;
import evilcraft.EvilCraft;
import evilcraft.GeneralConfig;
import evilcraft.Reference;
import evilcraft.block.GemStoneTorchConfig;
import evilcraft.client.particle.EntityBlurFX;
import evilcraft.client.particle.EntityDarkSmokeFX;
import evilcraft.client.particle.EntityDegradeFX;
import evilcraft.core.config.IChangedCallback;
import evilcraft.core.config.configurable.IConfigurable;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.entity.monster.EntityNoMob;
import evilcraft.core.helper.L10NHelpers;
import evilcraft.core.helper.WorldHelpers;
import evilcraft.core.helper.obfuscation.MethodHandlesHelper;
import evilcraft.item.BurningGemStone;
import evilcraft.item.BurningGemStoneConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import thaumcraft.api.ThaumcraftApiHelper;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Vengeance Spirit
 * @author rubensworks
 */
public class VengeanceSpirit extends EntityNoMob implements IConfigurable {
    /**
     * The default I18N key for when no inner entity exists.
     */
    public static final String DEFAULT_L10N_KEY = "vengeanceSpirit";

    private static final int SWARM_TIERS = 5;

    private static final Set<Class<? extends EntityLivingBase>> BLACKLIST = Sets.newHashSet();
    private static final Set<Class<? extends EntityLivingBase>> IMC_BLACKLIST = Sets.newHashSet();
    /**
     * The minimum life duration in ticks the spirits should have.
     */
    public static final int REMAININGLIFE_MIN = 250;
    /**
     * The maximum life duration in ticks the spirits should have.
     */
    public static final int REMAININGLIFE_MAX = 1000;

    private static final int WATCHERID_INNER = 20;
    private static final int WATCHERID_REMAININGLIFE = 21;
    private static final int WATCHERID_FROZENDURATION = 22;
    private static final int WATCHERID_GLOBALVENGEANCE = 23;
    private static final int WATCHERID_VENGEANCEPLAYERS = 24;
    private static final int WATCHERID_ISSWARM = 25;
    private static final int WATCHERID_SWARMTIER = 26;
    private static final int WATCHERID_BUILDUP = 27;
    private static final int WATCHERID_PLAYERID = 28;
    private static final int WATCHERID_PLAYERNAME = 29;
    /**
     * The NBT key used to store the inner entity name.
     */
    public static final String NBTKEY_INNER_SPIRIT = "innerEntity";

    private EntityLivingBase innerEntity = null;

    /**
     * Make a new instance.
     * @param world The world.
     */
    public VengeanceSpirit(World world) {
        super(world);
        this.getNavigator().setAvoidsWater(false);
        this.stepHeight = 5.0F;
        this.isImmuneToFire = true;
        this.preventEntitySpawning = false;
        this.setSize(1, 1); // Dummy size, to avoid rare bounding box crashes before inner entity is init.

        double speed = 0.5D;
        double damage = 0.5D;
        int remainingLife = MathHelper.getRandomIntegerInRange(world.rand, REMAININGLIFE_MIN, REMAININGLIFE_MAX);
        if(isSwarm()) {
            speed += 0.25D * getSwarmTier();
            damage += 0.5D * getSwarmTier();
            remainingLife += (REMAININGLIFE_MAX - REMAININGLIFE_MIN) * getSwarmTier();
        }
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(speed);

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIWander(this, 1.0F));
        this.tasks.addTask(2, new EntityAILookIdle(this));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityPlayer.class, damage, false));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));

        setRemainingLife(remainingLife);
        setFrozenDuration(0);
    }

    @SuppressWarnings("unchecked")
    private String getRandomInnerEntity() {
        Collection<EntityList.EntityEggInfo> eggs = EntityList.entityEggs.values();
        ArrayList<EntityList.EntityEggInfo> eggList = Lists.newArrayList(eggs);
        if(eggList.size() > 0) {
            EntityList.EntityEggInfo egg = eggList.get(rand.nextInt(eggList.size()));
            if(egg != null) {
                Class<Entity> clazz = (Class<Entity>)EntityList.IDtoClassMapping.get(egg.spawnedID);
                if(clazz != null) {
                    return clazz.getName();
                }
            }
        }
        return VengeanceSpirit.class.getName();
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(WATCHERID_INNER, getRandomInnerEntity());
        this.dataWatcher.addObject(WATCHERID_REMAININGLIFE, 0);
        this.dataWatcher.addObject(WATCHERID_FROZENDURATION, 0);
        this.dataWatcher.addObject(WATCHERID_GLOBALVENGEANCE, 0);
        this.dataWatcher.addObject(WATCHERID_VENGEANCEPLAYERS, new String());
        this.dataWatcher.addObject(WATCHERID_ISSWARM, 0);
        this.dataWatcher.addObject(WATCHERID_SWARMTIER, rand.nextInt(SWARM_TIERS));
        this.dataWatcher.addObject(WATCHERID_BUILDUP, 0);
        this.dataWatcher.addObject(WATCHERID_PLAYERID, "");
        this.dataWatcher.addObject(WATCHERID_PLAYERNAME, "");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        if(getInnerEntity() != null)
            tag.setString("innerEntity", getInnerEntity().getClass().getName());
        tag.setInteger("remainingLife", getRemainingLife());
        tag.setInteger("frozenDuration", getFrozenDuration());
        tag.setBoolean("isSwarm", isSwarm());
        tag.setInteger("swarmTier", getSwarmTier());
        tag.setInteger("buildupDuration", getBuildupDuration());
        tag.setString("playerId", getPlayerId());
        tag.setString("playerName", getPlayerName());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        String name = tag.getString(NBTKEY_INNER_SPIRIT);
        if(name != null)
            this.dataWatcher.updateObject(WATCHERID_INNER, name);
        setRemainingLife(tag.getInteger("remainingLife"));
        setFrozenDuration(tag.getInteger("frozenDuration"));
        setIsSwarm(tag.getBoolean("isSwarm"));
        setSwarmTier(tag.getInteger("swarmTier"));
        setBuildupDuration(tag.getInteger("buildupDuration"));
        setPlayerId(tag.getString("playerId"));
        setPlayerName(tag.getString("playerName"));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
    }

    @Override
    protected float getSoundPitch() {
        return super.getSoundPitch() / 3.0F;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    public boolean isEntityInvulnerable() {
        return true;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if(getBuildupDuration() > 0) return false; // Don't attack anything when still building up.

        this.setDead();
        this.worldObj.removeEntity(this);
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;

            if(Loader.isModLoaded(Reference.MOD_THAUMCRAFT)) {
                addWarp((EntityPlayer)entity);
            }

            if(!Configs.isEnabled(BurningGemStoneConfig.class) || BurningGemStone.damageForPlayer(player, isSwarm() ? getSwarmTier() : 0, false)) {
                entity.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * 0.01F), 0.025D,
                        (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * 0.01F));
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), 0.1F);
                return false;
            }
        }
        return super.attackEntityAsMob(entity);
    }

    @Override
    public void setDead() {
        super.setDead();
        if(worldObj.isRemote && isVisible()) {
            spawnSmoke();
            playSound(getDeathSound(), 0.1F + worldObj.rand.nextFloat() * 0.9F, 0.1F + worldObj.rand.nextFloat() * 0.9F);
        }
    }

    @Override
    public boolean isMovementBlocked() {
        return isFrozen() || getBuildupDuration() > 0;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if(isVisible()) {
            if(innerEntity != null) {
                innerEntity.isDead = isDead;
                innerEntity.deathTime = deathTime;
                innerEntity.attackTime = attackTime;
                innerEntity.hurtTime = hurtTime;
                innerEntity.rotationPitch = rotationPitch;
                innerEntity.rotationYaw = rotationYaw;
                innerEntity.rotationYawHead = rotationYawHead;
                innerEntity.renderYawOffset = renderYawOffset;
                innerEntity.cameraPitch = cameraPitch;
                innerEntity.prevCameraPitch = prevCameraPitch;
                innerEntity.prevRenderYawOffset = prevRenderYawOffset;
                innerEntity.prevRotationPitch = prevRotationPitch;
                innerEntity.prevRotationYaw = prevRotationYaw;
                innerEntity.prevRotationYawHead = prevRotationYawHead;
            }
            if(worldObj.isRemote) {
                spawnSmoke();
                if(isSwarm()) {
                    spawnSwarmParticles();
                }
            }
        }
        int buildupDuration = getBuildupDuration();
        if(buildupDuration > 0) setBuildupDuration(buildupDuration - 1);

        if(isFrozen()) {
            this.motionX = 0;
            this.motionY = 0;
            this.motionZ = 0;
            addFrozenDuration(-1);
            // TODO: render entangled particles
        } else {
            setRemainingLife(getRemainingLife() - 1);
            if(getRemainingLife() <= 0) {
                this.setDead();
                worldObj.removeEntity(this);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnSmoke() {
        int numParticles = rand.nextInt(5);
        if(this.isDead)
            numParticles *= 10;
        float clearRange = width; // Particles can't spawn within this X and Z distance
        for(int i = 0; i < numParticles; i++) {
            double particleX = posX - width / 2 + width * rand.nextFloat();
            if(particleX < 0.7F && particleX >= 0) particleX += width / 2;
            if(particleX > -0.7F && particleX <= 0) particleX -= width / 2;
            double particleY = posY + height * rand.nextFloat();
            double particleZ = posZ - width / 2 + width * rand.nextFloat();
            if(particleZ < clearRange && particleZ >= 0) particleZ += width / 2;
            if(particleZ > -clearRange && particleZ <= 0) particleZ -= width / 2;

            float particleMotionX = (-0.5F + rand.nextFloat()) * 0.05F;
            float particleMotionY = (-0.5F + rand.nextFloat()) * 0.05F;
            float particleMotionZ = (-0.5F + rand.nextFloat()) * 0.05F;

            EntityDarkSmokeFX particle = new EntityDarkSmokeFX(worldObj, particleX, particleY, particleZ, particleMotionX, particleMotionY, particleMotionZ);
            if(this.isDead)
                particle.setDeathParticles();
            particle.setLiving((float)getRemainingLife() / (float)REMAININGLIFE_MAX);
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnSwarmParticles() {
        int numParticles = 5 * (rand.nextInt((getSwarmTier() << 1) + 1) + 1);
        for(int i = 0; i < numParticles; i++) {
            double particleX = posX - width / 2 + width * rand.nextFloat();
            if(particleX < 0.7F && particleX >= 0) particleX += width / 2;
            if(particleX > -0.7F && particleX <= 0) particleX -= width / 2;
            double particleY = posY + height * rand.nextFloat();
            double particleZ = posZ - width / 2 + width * rand.nextFloat();

            float particleMotionX = (-0.5F + rand.nextFloat()) * 0.05F;
            float particleMotionY = (-0.5F + rand.nextFloat()) * 0.05F;
            float particleMotionZ = (-0.5F + rand.nextFloat()) * 0.05F;

            EntityDegradeFX particle = new EntityDegradeFX(worldObj, particleX, particleY, particleZ, particleMotionX, particleMotionY, particleMotionZ);
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
    }

    /**
     * If this entity is visible to the current player
     * @return If it is visible
     */
    public boolean isVisible() {
        return worldObj.isRemote && (isAlternativelyVisible() || isClientVisible());
    }

    @SideOnly(Side.CLIENT)
    private boolean isClientVisible() {
        return isEnabledVengeance(Minecraft.getMinecraft().thePlayer);
    }

    private boolean isAlternativelyVisible() {
        // TODO: add other possibilities like glasses
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        return player != null && player.capabilities.isCreativeMode;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightning) {
        setGlobalVengeance(true);
    }

    @Override
    public boolean canEntityBeSeen(Entity entity) {
        if(entity instanceof EntityPlayer)
            return isEnabledVengeance((EntityPlayer)entity);
        else
            return super.canEntityBeSeen(entity);
    }

    /**
     * If the given player is vengeanced by this spirit
     * @param player the player.
     * @return If it should be visible.
     */
    public boolean isEnabledVengeance(EntityPlayer player) {
        return isGlobalVengeance() || ArrayUtils.contains(getVengeancePlayers(), player.getDisplayName());
    }

    /**
     * Enable vengeance of this spirit for the given player.
     * @param player This player will be added to the target list.
     * @param enabled If vengeance should be enabled
     */
    public void setEnabledVengeance(EntityPlayer player, boolean enabled) {
        String[] players = getVengeancePlayers();
        int index = ArrayUtils.indexOf(players, player.getDisplayName());
        if(enabled && index == ArrayUtils.INDEX_NOT_FOUND)
            players = ArrayUtils.add(players, player.getDisplayName());
        else if(!enabled && index != ArrayUtils.INDEX_NOT_FOUND)
            players = ArrayUtils.remove(players, index);
        setVengeancePlayers(players);
    }

    /**
     * Get the inner entity.
     * @return inner entity
     */
    @SuppressWarnings("unchecked")
    public EntityLivingBase getInnerEntity() {
        if(isSwarm()) {
            this.height = getSwarmTier() / 2 + 1;
            this.width = getSwarmTier() / 3 + 1;
            return null;
        }
        if(innerEntity != null)
            return innerEntity;
        Class<EntityLivingBase> clazzLog = null;
        try {
            Class<EntityLivingBase> clazz = (Class<EntityLivingBase>)Class.forName(dataWatcher.getWatchableObjectString(WATCHERID_INNER));
            if(!clazz.equals(VengeanceSpirit.class)) {
                clazzLog = clazz;
                String name = (String)EntityList.classToStringMapping.get(clazz);
                Entity entity = EntityList.createEntityByName(name, worldObj);
                if(canSustain((EntityLivingBase)entity)) {
                    innerEntity = (EntityLivingBase)entity;
                    this.setSize(innerEntity.width, innerEntity.height);
                    return innerEntity;
                }
            }
        } catch(ClassNotFoundException e) {
            // In this case it is a vengeance swarm.
        } catch(NullPointerException e) {
            EvilCraft.log("Tried to spirit invalid[NPE] entity of class " + clazzLog + ", removing it now.", Level.ERROR);
        } catch(ClassCastException e) {
            EvilCraft.log("Tried to spirit invalid[CCE] entity of class " + clazzLog + ", removing it now.", Level.ERROR);
        }
        if(!this.worldObj.isRemote) {
            this.worldObj.removeEntity(this);
        }
        return null;
    }

    /**
     * Set the inner entity;
     * @param innerEntity inner entity
     */
    public void setInnerEntity(EntityLivingBase innerEntity) {
        if(innerEntity instanceof EntityPlayer) {
            setPlayerId(((EntityPlayer)innerEntity).getGameProfile().getId().toString());
            setPlayerName(((EntityPlayer)innerEntity).getGameProfile().getName());
            innerEntity = new EntityZombie(worldObj);
        }
        this.dataWatcher.updateObject(WATCHERID_INNER, innerEntity.getClass().getName());
    }

    /**
     * Get the remaining life.
     * @return The remaining life.
     */
    public int getRemainingLife() {
        return dataWatcher.getWatchableObjectInt(WATCHERID_REMAININGLIFE);
    }

    /**
     * Set the remaining life.
     * @param remainingLife The remaining life.
     */
    public void setRemainingLife(int remainingLife) {
        this.dataWatcher.updateObject(WATCHERID_REMAININGLIFE, remainingLife);
    }

    /**
     * Get the remaining life.
     * @return The remaining life.
     */
    public int getBuildupDuration() {
        return dataWatcher.getWatchableObjectInt(WATCHERID_BUILDUP);
    }

    /**
     * Set the remaining buildup time.
     * @param buildupDuration The remaining life.
     */
    public void setBuildupDuration(int buildupDuration) {
        this.dataWatcher.updateObject(WATCHERID_BUILDUP, buildupDuration);
    }

    /**
     * Get the playerId.
     * @return The playerId.
     */
    public String getPlayerId() {
        return dataWatcher.getWatchableObjectString(WATCHERID_PLAYERID);
    }

    /**
     * Get the player UUID or null.
     * @return The player UUID
     */
    public UUID getPlayerUUID() {
        try {
            return UUID.fromString(getPlayerId());
        } catch(IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * @return If this spirit is a player spirit.
     */
    public boolean isPlayer() {
        return getPlayerUUID() != null;
    }

    /**
     * Set the playerId.
     * @param playerId The playerId.
     */
    public void setPlayerId(String playerId) {
        this.dataWatcher.updateObject(WATCHERID_PLAYERID, playerId);
    }

    /**
     * Get the playerName.
     * @return The playerName.
     */
    public String getPlayerName() {
        return dataWatcher.getWatchableObjectString(WATCHERID_PLAYERNAME);
    }

    /**
     * Set the playerName.
     * @param playerName The playerName.
     */
    public void setPlayerName(String playerName) {
        this.dataWatcher.updateObject(WATCHERID_PLAYERNAME, playerName);
    }

    /**
     * Get the frozen duration.
     * @return The frozen duration.
     */
    public int getFrozenDuration() {
        return dataWatcher.getWatchableObjectInt(WATCHERID_FROZENDURATION);
    }

    /**
     * Set the frozen duration.
     * @param frozenDuration The frozen duration.
     */
    public void setFrozenDuration(int frozenDuration) {
        this.dataWatcher.updateObject(WATCHERID_FROZENDURATION, frozenDuration);
    }

    /**
     * Is this spirit globally vengeancable.
     * @return Is globally vengeancable.
     */
    public boolean isGlobalVengeance() {
        return dataWatcher.getWatchableObjectInt(WATCHERID_GLOBALVENGEANCE) == 1;
    }

    /**
     * Set if this spirit globally vengeancable.
     * @param globalVengeance Is globally vengeancable.
     */
    public void setGlobalVengeance(boolean globalVengeance) {
        this.dataWatcher.updateObject(WATCHERID_GLOBALVENGEANCE, globalVengeance ? 1 : 0);
    }

    /**
     * Is this spirit is a swarm.
     * @return Is a swarm.
     */
    public boolean isSwarm() {
        return dataWatcher.getWatchableObjectInt(WATCHERID_ISSWARM) == 1;
    }

    /**
     * Set if this spirit is a swarm.
     * @param isSwarm Is a swarm.
     */
    public void setIsSwarm(boolean isSwarm) {
        this.dataWatcher.updateObject(WATCHERID_ISSWARM, isSwarm ? 1 : 0);
    }

    /**
     * Get the tier of swarm for this spirit.
     * @return The swarm tier.
     */
    public int getSwarmTier() {
        return dataWatcher.getWatchableObjectInt(WATCHERID_SWARMTIER);
    }

    /**
     * Set the tier of swarm this spirit should be.
     * @param swarmTier The tier to set.
     */
    public void setSwarmTier(int swarmTier) {
        this.dataWatcher.updateObject(WATCHERID_SWARMTIER, swarmTier);
    }

    /**
     * Get the vengeanced players.
     * @return The vengeanced players by display name.
     */
    public String[] getVengeancePlayers() {
        String encodedPlayers = dataWatcher.getWatchableObjectString(WATCHERID_VENGEANCEPLAYERS);
        if(encodedPlayers.length() == 0) {
            return new String[0];
        }
        return encodedPlayers.split("&");
    }

    /**
     * Set the vengeanced players.
     * @param vengeancePlayers The vengeanced players by display name.
     */
    public void setVengeancePlayers(String[] vengeancePlayers) {
        this.dataWatcher.updateObject(WATCHERID_VENGEANCEPLAYERS, StringUtils.join(vengeancePlayers, "&"));
    }

    /**
     * If the given entity can be 'spirited'
     * @param entityLiving The entity to check.
     * @return If it can become a spirit.
     */
    public static boolean canSustain(EntityLivingBase entityLiving) {
        for(Class<? extends EntityLivingBase> clazz : BLACKLIST) {
            if(clazz.isInstance(entityLiving)) {
                return false;
            }
        }
        return true;
    }

    /**
     * If the given entity class can be 'spirited'
     * @param entityLivingClazz The entity class to check.
     * @return If it can become a spirit.
     */
    public static boolean canSustainClass(Class<?> entityLivingClazz) {
        for(Class<? extends EntityLivingBase> clazz : BLACKLIST) {
            if(clazz.equals(entityLivingClazz)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if we can spawn a new vengeance spirit in the given location.
     * It will check if the amount of spirits in an area is below a certain threshold and if there aren't any gemstone torches in the area
     * @param world The world.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z Coordinate.
     * @return If we are allowed to spawn a spirit.
     */
    @SuppressWarnings("unchecked")
    public static boolean canSpawnNew(World world, double x, double y, double z) {
        if(Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z))
            return false;

        int area = VengeanceSpiritConfig.spawnLimitArea;
        int threshold = VengeanceSpiritConfig.spawnLimit;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(area, area, area);
        List<VengeanceSpirit> spirits = world.getEntitiesWithinAABB(VengeanceSpirit.class, box);
        if(spirits.size() >= threshold) {
            return false;
        }

        if(!Configs.isEnabled(GemStoneTorchConfig.class))
            return true;

        return WorldHelpers.foldArea(world, GemStoneTorchConfig.area, (int)x, (int)y, (int)z, new WorldHelpers.WorldFoldingFunction<Boolean, Boolean>() {
            @Override @Nullable
            public Boolean apply(@Nullable Boolean input, World world, int x, int y, int z) {
                return (input == null || input) && world.getBlock(x, y, z) != GemStoneTorchConfig._instance.getBlockInstance();
            }
        }, true);
    }

    /**
     * Add a frozen duration.
     * @param addFrozen Ticks to add frozen.
     */
    public void addFrozenDuration(int addFrozen) {
        this.setFrozenDuration(this.getFrozenDuration() + addFrozen);
    }

    /**
     * If this spirit is frozen.
     * @return If frozen duration larger than zero.
     */
    public boolean isFrozen() {
        return this.getFrozenDuration() > 0;
    }

    /**
     * When this spirit is hit by a neutron.
     * @param hitX Hit X.
     * @param hitY Hit Y.
     * @param hitZ Hit Z.
     * @param impactMotionX The motion speed for X.
     * @param impactMotionY The motion speed for Y.
     * @param impactMotionZ The motion speed for Z.
     */
    public void onHit(double hitX, double hitY, double hitZ, double impactMotionX, double impactMotionY, double impactMotionZ) {
        addFrozenDuration(worldObj.rand.nextInt(4) + 3);
        if(worldObj.isRemote) {
            showBurstParticles(hitX, hitY, hitZ, impactMotionX, impactMotionY, impactMotionZ);
        }
    }

    @SideOnly(Side.CLIENT)
    private void showBurstParticles(double hitX, double hitY, double hitZ, double impactMotionX, double impactMotionY, double impactMotionZ) {
        for(int i = 0; i < worldObj.rand.nextInt(5); i++) {
            float scale = 0.04F - rand.nextFloat() * 0.02F;
            float red = rand.nextFloat() * 0.2F + 0.3F;
            float green = rand.nextFloat() * 0.2F + 0.3F;
            float blue = rand.nextFloat() * 0.01F;
            float ageMultiplier = (float)(rand.nextDouble() * 0.5D + 3D);

            double dx = 0.1D - rand.nextDouble() * 0.2D - impactMotionX * 0.1D;
            double dy = 0.1D - rand.nextDouble() * 0.2D - impactMotionY * 0.1D;
            double dz = 0.1D - rand.nextDouble() * 0.2D - impactMotionZ * 0.1D;

            EntityBlurFX blur = new EntityBlurFX(worldObj, hitX, hitY, hitZ, scale, dx, dy, dz, red, green, blue, ageMultiplier);
            Minecraft.getMinecraft().effectRenderer.addEffect(blur);
        }
    }

    /**
     * Spawn a random vengeance spirit in the given area.
     * @param world The world.
     * @param x The center X coordinate.
     * @param y The center Y coordinate.
     * @param z The center Z coordinate.
     * @param area The radius in which the spawn can occur.
     * @return The spawned spirit, could be null.
     */
    public static VengeanceSpirit spawnRandom(World world, int x, int y, int z, int area) {
        VengeanceSpirit spirit = new VengeanceSpirit(world);
        int attempts = 50;
        int baseDistance = 5;
        while(canSpawnNew(world, x, y, z) && attempts > 0) {
            int posX = x + MathHelper.getRandomIntegerInRange(world.rand, baseDistance, baseDistance + area) * MathHelper.getRandomIntegerInRange(world.rand, -1, 1);
            int posY = y + MathHelper.getRandomIntegerInRange(world.rand, 0, 3) * MathHelper.getRandomIntegerInRange(world.rand, -1, 1);
            int posZ = z + MathHelper.getRandomIntegerInRange(world.rand, baseDistance, baseDistance + area) * MathHelper.getRandomIntegerInRange(world.rand, -1, 1);

            if(World.doesBlockHaveSolidTopSurface(world, posX, posY - 1, posZ)) {
                spirit.setPosition((double)posX + 0.5, (double)posY + 0.5, (double)posZ + 0.5);
                if(world.checkNoEntityCollision(spirit.boundingBox) && world.getCollidingBoundingBoxes(spirit, spirit.boundingBox).isEmpty() && !world.isAnyLiquid(spirit.boundingBox)) {
                    world.spawnEntityInWorld(spirit);
                    spirit.onSpawnWithEgg((IEntityLivingData)null);
                    attempts = -1;
                    return spirit;
                }
            }
            attempts--;
        }
        return null;
    }

    /**
     * Get the localized name of the inner entity.
     * @return The L10N name.
     */
    public String getLocalizedInnerEntityName() {
        if(isPlayer()) {
            return getPlayerName();
        }
        String key = DEFAULT_L10N_KEY;
        if(getInnerEntity() != null) {
            key = (String)EntityList.classToStringMapping.get(getInnerEntity().getClass());
        }
        return L10NHelpers.getLocalizedEntityName(key);
    }

    @Override
    protected String getDeathSound() {
        if(getInnerEntity() != null) {
            return MethodHandlesHelper.getDeathSound(getInnerEntity());
        }
        return "vengeanceSpiritDeath";
    }

    @Override
    protected String getLivingSound() {
        EntityLivingBase entity = getInnerEntity();
        if(entity != null && entity instanceof EntityLiving) {
            return MethodHandlesHelper.getLivingSound((EntityLiving)entity);
        }
        return "vengeanceSpirit";
    }

    @Override
    public void playSound(String sound, float volume, float frequency) {
        if(isVisible() && sound != null) {
            if(isSwarm()) {
                EvilCraft.proxy.playSound(posX, posY, posZ, sound, volume, frequency);
            } else {
                EvilCraft.proxy.playSoundMinecraft(posX, posY, posZ, sound, volume, frequency);
            }
        }
    }

    @Optional.Method(modid = Reference.MOD_THAUMCRAFT)
    private void addWarp(EntityPlayer player) {
        if(GeneralConfig.thaumcraftVengeanceSpiritWarp) {
            ThaumcraftApiHelper.addWarpToPlayer(player, 1, true);
        }
    }

    /**
     * Add an entity class to the blacklist, every subinstance of this class will not be spirited anymore.
     * @param clazz The root class that will be blocked from spiritation.
     */
    public static void addToBlacklist(Class<? extends EntityLivingBase> clazz) {
        if(BLACKLIST.add(clazz))
            EvilCraft.log("Added entity class " + clazz.getCanonicalName() + " to the spirit blacklist.");
    }

    /**
     * Add an entity class to the blacklist, every subinstance of this class will not be spirited anymore.
     * This should only be called by IMC message handlers.
     * @param clazz The root class that will be blocked from spiritation.
     */
    public static void addToBlacklistIMC(Class<? extends EntityLivingBase> clazz) {
        IMC_BLACKLIST.add(clazz);
        addToBlacklist(clazz);
    }

    @SuppressWarnings("unchecked")
    protected static void setBlacklist(String[] blacklist) {
        BLACKLIST.clear();
        for(String entity : blacklist) {
            Class<EntityLivingBase> clazz = (Class<EntityLivingBase>)EntityList.stringToClassMapping.get(entity);
            if(clazz == null) {
                EvilCraft.log("Could not find entity by name '" + entity + "' for spirit blacklist.", Level.ERROR);
            } else {
                addToBlacklist(clazz);
            }
        }
        for(Class<? extends EntityLivingBase> clazz : IMC_BLACKLIST) {
            addToBlacklist(clazz);
        }
        // Hard-code some entities
        addToBlacklist(VengeanceSpirit.class);
        addToBlacklist(ControlledZombie.class);
        addToBlacklist(EntityDragon.class);
    }

    /**
     * The changed callback for the spirit blacklist.
     * @author rubensworks
     */
    public static class SpiritBlacklistChanged implements IChangedCallback {

        private static boolean calledOnce = false;

        @Override
        public void onChanged(Object value) {
            if(calledOnce) {
                setBlacklist((String[])value);
            }
            calledOnce = true;
        }

        @Override
        public void onRegisteredPostInit(Object value) {
            onChanged(value);
        }
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return null;
    }

    @Override
    public boolean handleWaterMovement() {
        // Ignore water movement and particles
        return this.inWater;
    }
}