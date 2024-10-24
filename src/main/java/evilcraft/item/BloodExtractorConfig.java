package evilcraft.item;

import evilcraft.core.config.ConfigurableProperty;
import evilcraft.core.config.ConfigurableTypeCategory;
import evilcraft.core.config.extendedconfig.ItemConfig;

/**
 * Config for the {@link BloodExtractor}.
 * @author rubensworks
 */
public class BloodExtractorConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static BloodExtractorConfig _instance;
    /**
     * The minimum multiplier for amount of mB to receive per mob HP.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.ITEM, comment = "The minimum multiplier for amount of mB to receive per mob HP.", isCommandable = true)
    public static double minimumMobMultiplier = 5;
    /**
     * The maximum multiplier for amount of mB to receive per mob HP.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.ITEM, comment = "The maximum multiplier for amount of mB to receive per mob HP. IMPORTANT: must be larger than maximumMobMultiplier!", isCommandable = true)
    public static double maximumMobMultiplier = 40;
    /**
     * The amount of blood (mB) this container can hold.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.ITEM, comment = "The amount of blood (mB) this container can hold.", requiresMcRestart = true)
    public static int containerSize = 5000;

    /**
     * Make a new instance.
     */
    public BloodExtractorConfig() {
        super(true, "bloodExtractor", null, BloodExtractor.class);
    }
}