package evilcraft.entity.block;

import evilcraft.block.LightningBomb;
import evilcraft.core.config.configurable.IConfigurable;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Entity for primed {@link LightningBomb}.
 * @author rubensworks
 */
public class EntityLightningBombPrimed extends EntityTNTPrimed implements IConfigurable {

    private static final float EXPLOSION_STRENGTH = 1.0f;

    /**
     * Make a new instance in the given world.
     * @param world The world to make it in.
     */
    public EntityLightningBombPrimed(World world) {
        super(world);
        setFuse();
    }

    /**
     * Make a new instance at the given location in a world by a placer {@link EntityLivingBase}.
     * @param world The world.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @param placer The {@link EntityLivingBase} that placed this {@link Entity}.
     */
    public EntityLightningBombPrimed(World world, double x, double y, double z, EntityLivingBase placer) {
        super(world, x, y, z, placer);
        setFuse();
    }

    protected void setFuse() {
        this.fuse = EntityLightningBombPrimedConfig.fuse;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03999999910593033D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if(this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
            this.motionY *= -0.5D;
        }

        if(this.fuse-- <= 0) {
            this.setDead();

            if(!this.worldObj.isRemote) {
                this.explode(this.worldObj, this.posX, this.posY, this.posZ);
            }
        } else {
            this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    private void explode(World world, double x, double y, double z) {
        Random rand = new Random();
        for(int i = 0; i < 32; ++i) {
            world.spawnParticle("magicCrit", x, y + rand.nextDouble() * 2.0D, z, rand.nextGaussian(), 0.0D, rand.nextGaussian());
        }
        if(!world.isRemote) {
            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, EXPLOSION_STRENGTH, true);
            world.addWeatherEffect(new EntityLightningBolt(world, x, y, z));
        }
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return null;
    }
}