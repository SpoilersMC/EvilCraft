package evilcraft.block;

import evilcraft.core.config.extendedconfig.BlockContainerConfig;

/**
 * A config for {@link InvisibleRedstoneBlock}.
 * @author rubensworks
 */
public class InvisibleRedstoneBlockConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static InvisibleRedstoneBlockConfig _instance;

    /**
     * Make a new instance.
     */
    public InvisibleRedstoneBlockConfig() {
        super(true, "invisibleRedstoneBlock", null, InvisibleRedstoneBlock.class);
    }
}