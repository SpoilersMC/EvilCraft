package evilcraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import evilcraft.core.PlayerInventoryIterator;
import evilcraft.core.config.configurable.ConfigurableItem;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.config.extendedconfig.ItemConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A dark gem that somehow caught fire.
 * @author rubensworks
 */
public class BurningGemStone extends ConfigurableItem {

    private static BurningGemStone _instance = null;

    /**
     * Initialise the configurable.
     * @param eConfig The config.
     */
    public static void initInstance(ExtendedConfig<ItemConfig> eConfig) {
        if(_instance == null)
            _instance = new BurningGemStone(eConfig);
        else
            eConfig.showDoubleInitError();
    }

    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static BurningGemStone getInstance() {
        return _instance;
    }

    private BurningGemStone(ExtendedConfig<ItemConfig> eConfig) {
        super(eConfig);
        this.maxStackSize = 1;
        this.setMaxDamage(BurningGemStoneConfig.maxDamage);
        this.setNoRepair();
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.uncommon;
    }

    /**
     * Try damaging a burning gem stone inside the given player's inventory.
     * @param player The player.
     * @param swarmTier The tier of swarm.
     * @param simulate If damaging should be simulated.
     * @return If a burning gem stone was found and damaged.
     */
    public static boolean damageForPlayer(EntityPlayer player, int swarmTier, boolean simulate) {
        PlayerInventoryIterator it = new PlayerInventoryIterator(player);
        while(it.hasNext()) {
            Pair<Integer, ItemStack> current = it.nextIndexed();
            ItemStack itemStack = current.getRight();
            if(itemStack != null && itemStack.getItem() == BurningGemStone.getInstance()) {
                if(!simulate) {
                    itemStack.damageItem(1 + swarmTier, player);
                    player.addExhaustion(10);
                    if(itemStack.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(current.getLeft(), null);
                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, itemStack));
                    }
                }
                return true;
            }
        }
        return false;
    }
}