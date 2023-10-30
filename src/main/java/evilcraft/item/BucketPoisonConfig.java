package evilcraft.item;

import evilcraft.block.FluidBlockPoison;
import evilcraft.core.config.configurable.ConfigurableBlockFluidClassic;
import evilcraft.core.config.configurable.ConfigurableFluid;
import evilcraft.core.config.configurable.ConfigurableItemBucket;
import evilcraft.core.config.configurable.IConfigurable;
import evilcraft.core.config.extendedconfig.ItemBucketConfig;
import evilcraft.fluid.Poison;

/**
 * Config for the Poison Bucket.
 * @author rubensworks
 */
public class BucketPoisonConfig extends ItemBucketConfig {

    /**
     * The unique instance.
     */
    public static BucketPoisonConfig _instance;

    /**
     * Make a new instance.
     */
    public BucketPoisonConfig() {
        super(true, "bucketPoison", null, null);
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new ConfigurableItemBucket(this, FluidBlockPoison.getInstance());
    }

    @Override
    public ConfigurableFluid getFluidInstance() {
        return Poison.getInstance();
    }

    @Override
    public ConfigurableBlockFluidClassic getFluidBlockInstance() {
        return FluidBlockPoison.getInstance();
    }

    @Override
    public boolean isDisableable() {
        return false;
    }
}