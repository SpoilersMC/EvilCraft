package evilcraft.item;

import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.config.extendedconfig.ItemConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;

/**
 * A mace that produces explosions around the player, without damaging that player.
 * @author rubensworks
 */
public class MaceOfDestruction extends Mace {

    private static MaceOfDestruction _instance = null;

    /**
     * The amount of ticks that should go between each update of the area of effect particles.
     */
    public static final int AOE_TICK_UPDATE = 20;

    private static final int MAXIMUM_CHARGE = 100;
    private static final float MELEE_DAMAGE = 10.0F;
    private static final int CONTAINER_SIZE = FluidContainerRegistry.BUCKET_VOLUME * 4;
    private static final int HIT_USAGE = 6;
    private static final int POWER_LEVELS = 5;

    /**
     * Initialize the configurable.
     * @param eConfig The config.
     */
    public static void initInstance(ExtendedConfig<ItemConfig> eConfig) {
        if(_instance == null)
            _instance = new MaceOfDestruction(eConfig);
        else
            eConfig.showDoubleInitError();
    }

    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static MaceOfDestruction getInstance() {
        return _instance;
    }

    private MaceOfDestruction(ExtendedConfig<ItemConfig> eConfig) {
        super(eConfig, CONTAINER_SIZE, HIT_USAGE, MAXIMUM_CHARGE, POWER_LEVELS, MELEE_DAMAGE);
    }

    @Override
    protected void use(World world, EntityPlayer player, int itemUsedCount, int power) {
        if(!world.isRemote) {
            Vec3 v = player.getLookVec();
            world.createExplosion(player, player.posX + v.xCoord * 2, player.posY + player.eyeHeight + v.yCoord * 2, player.posZ + v.zCoord * 2, ((float)itemUsedCount) / 20 + power, true);
        }
    }
}