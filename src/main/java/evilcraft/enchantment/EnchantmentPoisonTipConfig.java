package evilcraft.enchantment;

import evilcraft.Reference;
import evilcraft.core.config.extendedconfig.EnchantmentConfig;

/**
 * Config for {@link EnchantmentPoisonTip}.
 * @author rubensworks
 */
public class EnchantmentPoisonTipConfig extends EnchantmentConfig {

    /**
     * The unique instance.
     */
    public static EnchantmentPoisonTipConfig _instance;

    /**
     * Make a new instance.
     */
    public EnchantmentPoisonTipConfig() {
        super(Reference.ENCHANTMENT_POISON_TIP, "poisonTip", null, EnchantmentPoisonTip.class);
    }
}