package evilcraft.core.block.component;

import cpw.mods.fml.client.FMLClientHandler;
import evilcraft.client.particle.ExtendedEntityDropParticleFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Component that can show drops of a certain color underneath blocks.
 * This has by default the same behavior as lava and water drops underneath the blocks underneath them.
 * But this offset can be altered.
 * @author rubensworks
 */
public class EntityDropParticleFXBlockComponent implements IEntityDropParticleFXBlock {

    protected float particleRed;
    protected float particleGreen;
    protected float particleBlue;

    protected int offset = 1;
    protected int chance = 10;

    /**
     * Make a new instance.
     * @param particleRed Red color.
     * @param particleGreen Green color.
     * @param particleBlue Blue color.
     */
    public EntityDropParticleFXBlockComponent(float particleRed, float particleGreen, float particleBlue) {
        this.particleRed = particleRed;
        this.particleGreen = particleGreen;
        this.particleBlue = particleBlue;
    }

    /**
     * Set the offset to a lower Y where the drops should appear.
     * @param offset Amount of blocks to a lower Y.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Sets the chance for drop particles.
     * @param chance Every tick there will be a 1/chance chance.
     */
    public void setChance(int chance) {
        this.chance = chance;
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if(rand.nextInt(chance) == 0 && (offset == 0 || World.doesBlockHaveSolidTopSurface(world, x, y - offset, z)) && !world.getBlock(x, y - offset - 1, z).getMaterial().blocksMovement()) {
            double px = (double)((float)x + rand.nextFloat());
            double py = (double)y - 0.05D - offset;
            double pz = (double)((float)z + rand.nextFloat());

            EntityFX fx = new ExtendedEntityDropParticleFX(world, px, py, pz, particleRed, particleGreen, particleBlue);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
        }
    }
}