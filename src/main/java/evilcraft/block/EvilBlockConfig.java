package evilcraft.block;

import evilcraft.core.config.extendedconfig.BlockConfig;

/**
 * Config for the {@link EvilBlock}.
 * @author rubensworks
 */
public class EvilBlockConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static EvilBlockConfig _instance;

    /**
     * Make a new instance.
     */
    public EvilBlockConfig() {
        super(true, "evilBlock", null, EvilBlock.class);
    }

    @Override
    public boolean isHardDisabled() {
        return true;
    }
}