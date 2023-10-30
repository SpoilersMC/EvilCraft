package evilcraft.block;

import evilcraft.core.config.ConfigurableProperty;
import evilcraft.core.config.ConfigurableTypeCategory;
import evilcraft.core.config.extendedconfig.BlockConfig;
import evilcraft.item.ExcrementPileItemBlock;
import net.minecraft.item.ItemBlock;

/**
 * Config for the {@link ExcrementPile}.
 * @author rubensworks
 */
public class ExcrementPileConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static ExcrementPileConfig _instance;

    /**
     * If Excrement can also poison any mob next to players.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.BLOCK, comment = "If Excrement can also poison any mob next to players.", isCommandable = true)
    public static boolean poisonEntities = false;

    /**
     * The relative effectiveness when compared to bonemeal if shift right click using.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.BLOCK, comment = "The relative effectiveness when compared to bonemeal if shift right click using.", isCommandable = true)
    public static int effectiveness = 3;

    /**
     * Make a new instance.
     */
    public ExcrementPileConfig() {
        super(false, "excrementPile", null, ExcrementPile.class);
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ExcrementPileItemBlock.class;
    }
}