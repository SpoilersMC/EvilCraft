package evilcraft.core.config.extendedconfig;

import evilcraft.core.config.ConfigurableType;
import net.minecraft.enchantment.Enchantment;

/**
 * Config for enchantments.
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class EnchantmentConfig extends ExtendedConfig<EnchantmentConfig> {

    /**
     * The ID for the configurable.
     */
    public int ID;

    /**
     * Make a new instance.
     * @param defaultId The default ID for the configurable.
     * @param namedId The unique name ID for the configurable.
     * @param comment The comment to add in the config file for this configurable.
     * @param element The class of this configurable.
     */
    public EnchantmentConfig(int defaultId, String namedId, String comment, Class<? extends Enchantment> element) {
        super(defaultId != 0, namedId, comment, element);
        this.ID = defaultId;
    }

    @Override
    public String getUnlocalizedName() {
        return "enchantments." + getNamedId();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && this.ID != 0;
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.ENCHANTMENT;
    }
}