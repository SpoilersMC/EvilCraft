package evilcraft.item;

import evilcraft.client.render.item.RenderItemBroom;
import evilcraft.core.config.ConfigurableProperty;
import evilcraft.core.config.ConfigurableTypeCategory;
import evilcraft.core.config.extendedconfig.ItemConfig;
import evilcraft.core.helper.MinecraftHelpers;
import evilcraft.proxy.ClientProxy;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

/**
 * Config for the {@link Broom}.
 * @author rubensworks
 */
public class BroomConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static BroomConfig _instance;
    /**
     * If the broom should spawn in loot chests.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.ITEM, comment = "If the broom should spawn in loot chests.")
    public static boolean lootChests = true;

    /**
     * Make a new instance.
     */
    public BroomConfig() {
        super(true, "broom", null, Broom.class);
    }

    @Override
    public void onRegistered() {
        super.onRegistered();
        if(BroomConfig.lootChests) {
            for(String chestCategory : MinecraftHelpers.CHESTGENCATEGORIES) {
                ChestGenHooks.getInfo(chestCategory).addItem(new WeightedRandomChestContent(Broom.getInstance(), 0, 1, 1, 2));
            }
        }
        if(MinecraftHelpers.isClientSide()) {
            ClientProxy.ITEM_RENDERERS.put(this.getItemInstance(), new RenderItemBroom(this));
        }
    }
}