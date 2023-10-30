package evilcraft.entity.item;

import evilcraft.Configs;
import evilcraft.core.config.configurable.IConfigurable;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.entity.item.EntityThrowable;
import evilcraft.item.WeatherContainer;
import evilcraft.item.WeatherContainer.WeatherContainerTypes;
import evilcraft.item.WeatherContainerConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Entity for the {@link WeatherContainer}.
 * @author rubensworks
 */
public class EntityWeatherContainer extends EntityThrowable implements IConfigurable {

    private static final int ITEMSTACK_INDEX = 15;

    /**
     * Make a new instance in the given world.
     * @param world The world to make it in.
     */
    public EntityWeatherContainer(World world) {
        super(world);
    }

    /**
     * Make a new instance in a world by a placer {@link EntityLivingBase}.
     * @param world The world.
     * @param entity The {@link EntityLivingBase} that placed this {@link Entity}.
     * @param damage The damage value for the {@link WeatherContainer} to be rendered.
     */
    public EntityWeatherContainer(World world, EntityLivingBase entity, int damage) {
        this(world, entity, new ItemStack(Configs.isEnabled(WeatherContainerConfig.class) ? WeatherContainer.getInstance() : Items.coal, 1, damage));
    }

    /**
     * Make a new instance at the given location in a world.
     * @param world The world.
     * @param entity The entity
     * @param stack The {@link ItemStack} inside this entity.
     */
    public EntityWeatherContainer(World world, EntityLivingBase entity, ItemStack stack) {
        super(world, entity);
        setItemStack(stack);
    }

    public static void playImpactSounds(World world) {
        if(!world.isRemote) {
            // Play evil sounds at the players in that world
            for(Object o : world.playerEntities) {
                EntityPlayer entityPlayer = (EntityPlayer)o;
                world.playSoundAtEntity(entityPlayer, "mob.endermen.portal", 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
                world.playSoundAtEntity(entityPlayer, "mob.ghast.moan", 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
                world.playSoundAtEntity(entityPlayer, "mob.wither.death", 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
            }
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition movingobjectposition) {
        ItemStack stack = getItemStack();
        WeatherContainerTypes containerType = WeatherContainer.getWeatherContainerType(stack);
        containerType.onUse(worldObj, stack);

        playImpactSounds(worldObj);
        // Play sound and show particles of splash potion of harming
        // TODO: make custom particles for this
        this.worldObj.playAuxSFX(2002, (int)Math.round(this.posX), (int)Math.round(this.posY), (int)Math.round(this.posZ), 16428);
        setDead();
    }

    @Override
    protected float getGravityVelocity() {
        // The bigger, the faster the entity falls to the ground
        return 0.1F;
    }

    @Override
    protected float func_70182_d() {
        // Determines the distance of the throw
        return 1.0F;
    }

    @Override
    protected float func_70183_g() {
        // Offset for the start height at which the entity is thrown
        return 0.0F;
    }

    @Override
    public ItemStack getItemStack() {
        return dataWatcher.getWatchableObjectItemStack(ITEMSTACK_INDEX);
    }

    private void setItemStack(ItemStack stack) {
        dataWatcher.updateObject(ITEMSTACK_INDEX, stack);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(ITEMSTACK_INDEX, WeatherContainer.createItemStack(WeatherContainerTypes.EMPTY, 1));
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return null;
    }
}