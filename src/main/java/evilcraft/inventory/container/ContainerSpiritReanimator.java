package evilcraft.inventory.container;

import evilcraft.block.SpiritReanimator;
import evilcraft.core.inventory.slot.SlotFluidContainer;
import evilcraft.core.inventory.slot.SlotRemoveOnly;
import evilcraft.core.inventory.slot.SlotSingleItem;
import evilcraft.core.inventory.slot.SlotWorking;
import evilcraft.tileentity.TileSpiritReanimator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;

/**
 * Container for the {@link SpiritReanimator}.
 * @author rubensworks
 */
public class ContainerSpiritReanimator extends ContainerTileWorking<TileSpiritReanimator> {

    private static final int INVENTORY_OFFSET_X = 8;
    private static final int INVENTORY_OFFSET_Y = 84;

    public static final int SLOT_CONTAINER_X = 8;
    public static final int SLOT_CONTAINER_Y = 36;

    public static final int SLOT_BOX_X = 97;
    public static final int SLOT_BOX_Y = 22;

    public static final int SLOT_EGG_X = 135;
    public static final int SLOT_EGG_Y = 22;

    public static final int SLOT_OUTPUT_X = 116;
    public static final int SLOT_OUTPUT_Y = 52;

    private static final int UPGRADE_INVENTORY_OFFSET_X = -22;
    private static final int UPGRADE_INVENTORY_OFFSET_Y = 6;

    /**
     * Make a new instance.
     * @param inventory The inventory of the player.
     * @param tile The tile entity that calls the GUI.
     */
    public ContainerSpiritReanimator(InventoryPlayer inventory, TileSpiritReanimator tile) {
        super(inventory, tile);

        // Adding inventory
        addSlotToContainer(new SlotFluidContainer(tile, TileSpiritReanimator.SLOT_CONTAINER, SLOT_CONTAINER_X, SLOT_CONTAINER_Y, tile.getTank())); // Container emptier
        addSlotToContainer(new SlotWorking<TileSpiritReanimator>(TileSpiritReanimator.SLOT_BOX, SLOT_BOX_X, SLOT_BOX_Y, tile)); // Box slot
        addSlotToContainer(new SlotSingleItem(tile, TileSpiritReanimator.SLOT_EGG, SLOT_EGG_X, SLOT_EGG_Y, Items.egg));
        addSlotToContainer(new SlotRemoveOnly(tile, TileSpiritReanimator.SLOTS_OUTPUT, SLOT_OUTPUT_X, SLOT_OUTPUT_Y));

        this.addUpgradeInventory(UPGRADE_INVENTORY_OFFSET_X, UPGRADE_INVENTORY_OFFSET_Y);
        this.addPlayerInventory(inventory, INVENTORY_OFFSET_X, INVENTORY_OFFSET_Y);
    }
}