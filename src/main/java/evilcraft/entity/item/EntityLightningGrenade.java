package evilcraft.entity.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.core.config.configurable.IConfigurable;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.helper.EntityHelpers;
import evilcraft.item.LightningGrenade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Entity for the {@link LightningGrenade}.
 * @author rubensworks
 */
public class EntityLightningGrenade extends EntityThrowable implements IConfigurable {

    /**
     * Make a new instance in the given world.
     * @param world The world to make it in.
     */
    public EntityLightningGrenade(World world) {
        super(world);
    }

    /**
     * Make a new instance in a world by a placer {@link EntityLivingBase}.
     * @param world The world.
     * @param entity The {@link EntityLivingBase} that placed this {@link Entity}.
     */
    public EntityLightningGrenade(World world, EntityLivingBase entity) {
        super(world, entity);
    }

    /**
     * Make a new instance at the given location in a world.
     * @param world The world.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     */
    @SideOnly(Side.CLIENT)
    public EntityLightningGrenade(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
        if(par1MovingObjectPosition.entityHit != null) {
            par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
        }
        for(int i = 0; i < 32; ++i) {
            this.worldObj.spawnParticle("magicCrit", this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
        }
        if(!this.worldObj.isRemote) {
            if(this.getThrower() != null && this.getThrower() instanceof EntityPlayerMP) {
                EntityHelpers.onEntityCollided(this.worldObj, par1MovingObjectPosition.blockX, par1MovingObjectPosition.blockY, par1MovingObjectPosition.blockZ, this);
                this.worldObj.addWeatherEffect(new EntityLightningBolt(this.worldObj, this.posX, this.posY, this.posZ));
            }
            this.setDead();
        }
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return null;
    }
}