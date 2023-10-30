package evilcraft.item;

import evilcraft.core.config.extendedconfig.ItemConfig;

/**
 * Config for the {@link ContainedFlux}.
 * @author rubensworks
 */
public class ContainedFluxConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ContainedFluxConfig _instance;

    /**
     * Make a new instance.
     */
    public ContainedFluxConfig() {
        super(true, "containedFlux", null, ContainedFlux.class);
    }

    @Override
    public boolean isHardDisabled() {
        return true;
    }
}