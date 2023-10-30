package evilcraft.block;

import evilcraft.core.config.extendedconfig.BlockConfig;

/**
 * Config for {@link FluidBlockPoison}.
 * @author rubensworks
 */
public class FluidBlockPoisonConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static FluidBlockPoisonConfig _instance;

    /**
     * Make a new instance.
     */
    public FluidBlockPoisonConfig() {
        super(true, "blockPoison", null, FluidBlockPoison.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }
}