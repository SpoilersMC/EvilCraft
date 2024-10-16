package evilcraft.item;

import evilcraft.core.config.ConfigurableProperty;
import evilcraft.core.config.ConfigurableTypeCategory;
import evilcraft.core.config.extendedconfig.ItemConfig;
import net.minecraftforge.fluids.FluidContainerRegistry;

/**
 * Config for the {@link InvigoratingPendant}.
 * @author rubensworks
 */
public class InvigoratingPendantConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static InvigoratingPendantConfig _instance;

    /**
     * The capacity of the pendant.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.ITEM, comment = "The capacity of the pendant.", requiresMcRestart = true)
    public static int capacity = FluidContainerRegistry.BUCKET_VOLUME * 5;

    /**
     * The amount of Blood to drain after one reduction/clearing of one bad effect.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.ITEM, comment = "The amount of blood to drain after each clearing of one bad effect.", isCommandable = true)
    public static int usage = 100;

    /**
     * The amount of seconds that will be reduced from the first found bad effect.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.ITEM, comment = "The amount of seconds that will be reduced from the first found bad effect.", isCommandable = true)
    public static int reduceDuration = 30;

    /**
     * The amount of Blood to drain after one clearing of fire.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.ITEM, comment = "The amount of Blood to drain after one reduction/clearing of fire. -1 to disable fire extinguishing.", isCommandable = true)
    public static int fireUsage = 500;

    /**
     * Make a new instance.
     */
    public InvigoratingPendantConfig() {
        super(true, "invigoratingPendant", null, InvigoratingPendant.class);
    }

    @Override
    protected String getConfigPropertyPrefix() {
        return "invigPendant";
    }
}