package evilcraft.inventory.container;

import evilcraft.block.SpiritFurnace;
import evilcraft.core.inventory.slot.SlotFluidContainer;
import evilcraft.core.inventory.slot.SlotWorking;
import evilcraft.core.inventory.slot.SlotWorkingRemoveOnly;
import evilcraft.tileentity.TileSpiritFurnace;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * Container for the {@link SpiritFurnace}.
 * @author rubensworks
 */
public class ContainerSpiritFurnace extends ContainerTileWorking<TileSpiritFurnace> {

    private static final int INVENTORY_OFFSET_X = 8;
    private static final int INVENTORY_OFFSET_Y = 84;

    public static final int SLOT_CONTAINER_X = 8;
    public static final int SLOT_CONTAINER_Y = 36;

    public static final int SLOT_BOX_X = 79;
    public static final int SLOT_BOX_Y = 36;

    private static final int SLOTS_X = 2;
    private static final int SLOTS_Y = 2;

    public static final int SLOTS_DROP = SLOTS_X * SLOTS_Y;
    public static final int SLOT_DROP_X = 134;
    public static final int SLOT_DROP_Y = 28;

    private static final int UPGRADE_INVENTORY_OFFSET_X = -22;
    private static final int UPGRADE_INVENTORY_OFFSET_Y = 6;

    /**
     * Make a new instance.
     * @param inventory The inventory of the player.
     * @param tile The tile entity that calls the GUI.
     */
    public ContainerSpiritFurnace(InventoryPlayer inventory, TileSpiritFurnace tile) {
        super(inventory, tile);

        // Adding inventory
        addSlotToContainer(new SlotFluidContainer(tile, TileSpiritFurnace.SLOT_CONTAINER, SLOT_CONTAINER_X, SLOT_CONTAINER_Y, tile.getTank())); // Container emptier
        addSlotToContainer(new SlotWorking<TileSpiritFurnace>(TileSpiritFurnace.SLOT_BOX, SLOT_BOX_X, SLOT_BOX_Y, tile)); // Box slot

        int i = 0;
        for(int y = 0; y < SLOTS_X; y++) {
            for(int x = 0; x < SLOTS_Y; x++) { // Drop slot
                addSlotToContainer(new SlotWorkingRemoveOnly<TileSpiritFurnace>(TileSpiritFurnace.SLOTS_DROP[i], SLOT_DROP_X + x * ITEMBOX, SLOT_DROP_Y + y * ITEMBOX, tile, false));
                i++;
            }
        }

        this.addUpgradeInventory(UPGRADE_INVENTORY_OFFSET_X, UPGRADE_INVENTORY_OFFSET_Y);
        this.addPlayerInventory(inventory, INVENTORY_OFFSET_X, INVENTORY_OFFSET_Y);
    }
}