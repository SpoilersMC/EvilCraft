package evilcraft.core.inventory.container;

import evilcraft.core.inventory.slot.SlotArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * A container with inventory.
 * @author rubensworks
 */
public abstract class InventoryContainer extends Container {

    protected static final int ITEMBOX = 18;

    private IInventory playerIInventory;
    protected int offsetX = 0;
    protected int offsetY = 0;

    /**
     * Make a new TileInventoryContainer.
     * @param inventory The player inventory.
     */
    public InventoryContainer(InventoryPlayer inventory) {
        this.playerIInventory = inventory;
    }

    protected Slot createNewSlot(IInventory inventory, int index, int x, int y) {
        return new Slot(inventory, index, x, y);
    }

    protected Slot addSlotToContainer(Slot slot) {
        slot.xDisplayPosition += offsetX;
        slot.yDisplayPosition += offsetY;
        return super.addSlotToContainer(slot);
    }

    protected void addInventory(IInventory inventory, int indexOffset, int offsetX, int offsetY, int rows, int cols) {
        for(int y = 0; y < rows; y++) {
            for(int x = 0; x < cols; x++) {
                // Slot params: id, x-coord, y-coord (coords are relative to gui box)
                addSlotToContainer(createNewSlot(inventory, x + y * cols + indexOffset, offsetX + x * ITEMBOX, offsetY + y * ITEMBOX));
            }
        }
    }

    /**
     * Add player inventory and hotbar to the GUI.
     * @param inventory Inventory of the player
     * @param offsetX Offset to X
     * @param offsetY Offset to Y
     */
    protected void addPlayerInventory(InventoryPlayer inventory, int offsetX, int offsetY) {
        // Player inventory
        int rows = 3;
        int cols = 9;
        addInventory(inventory, cols, offsetX, offsetY, rows, cols);

        // Player hotbar
        offsetY += 58;
        addInventory(inventory, 0, offsetX, offsetY, 1, cols);
    }

    /**
     * Add player armor inventory to the GUI.
     * @param inventory Inventory of the player
     * @param offsetX Offset to X
     * @param offsetY Offset to Y
     */
    protected void addPlayerArmorInventory(InventoryPlayer inventory, int offsetX, int offsetY) {
        for(int y = 0; y < 4; y++) {
            addSlotToContainer(new SlotArmor(inventory, 4 * 9 + (3 - y), offsetX, offsetY + y * ITEMBOX, inventory.player, y));
        }
    }

    protected abstract int getSizeInventory();

    protected int getSlotStart(int originSlot, int slotStart, boolean reverse) {
        return slotStart;
    }

    protected int getSlotRange(int originSlot, int slotRange, boolean reverse) {
        return slotRange;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        ItemStack stack = null;
        Slot slot = (Slot)inventorySlots.get(slotID);
        int slots = getSizeInventory();

        if(slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            stack = stackInSlot.copy();

            if(slotID < slots) { // Click in tile -> player inventory
                if(!mergeItemStack(stackInSlot, getSlotStart(slotID, slots, true), getSlotRange(slotID, inventorySlots.size(), true), true)) {
                    return null;
                }
            } else if(!mergeItemStack(stackInSlot, getSlotStart(slotID, 0, false), getSlotRange(slotID, slots, false), false)) { // Click in player inventory -> tile
                return null;
            }

            if(stackInSlot.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if(stackInSlot.stackSize == stack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, stackInSlot);
        }

        return stack;
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int slotStart, int slotRange, boolean reverse) {
        boolean successful = false;
        int slotIndex = slotStart;
        int maxStack = stack.getMaxStackSize();

        if(reverse) {
            slotIndex = slotRange - 1;
        }

        Slot slot;
        ItemStack existingStack;

        if(stack.isStackable()) {
            while(stack.stackSize > 0 && (!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart)) {
                slot = (Slot)this.inventorySlots.get(slotIndex);
                int maxSlotSize = Math.min(slot.getSlotStackLimit(), maxStack);
                existingStack = slot.getStack();

                if(slot.isItemValid(stack) && existingStack != null && existingStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == existingStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, existingStack)) {
                    int existingSize = existingStack.stackSize + stack.stackSize;

                    if(existingSize <= maxSlotSize) {
                        stack.stackSize = 0;
                        existingStack.stackSize = existingSize;
                        slot.onSlotChanged();
                        successful = true;
                    } else if(existingStack.stackSize < maxSlotSize) {
                        stack.stackSize -= maxSlotSize - existingStack.stackSize;
                        existingStack.stackSize = maxSlotSize;
                        slot.onSlotChanged();
                        successful = true;
                    }
                }

                if(reverse) {
                    --slotIndex;
                } else {
                    ++slotIndex;
                }
            }
        }

        if(stack.stackSize > 0) {
            if(reverse) {
                slotIndex = slotRange - 1;
            } else {
                slotIndex = slotStart;
            }

            while(!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart) {
                slot = (Slot)this.inventorySlots.get(slotIndex);
                existingStack = slot.getStack();

                if(slot.isItemValid(stack) && existingStack == null) {
                    int placedAmount = Math.min(stack.stackSize, slot.getSlotStackLimit());
                    ItemStack toPut = stack.copy();
                    toPut.stackSize = placedAmount;
                    slot.putStack(toPut);
                    slot.onSlotChanged();
                    stack.stackSize -= placedAmount;
                    successful = true;
                    break;
                }
                if(reverse) {
                    --slotIndex;
                } else {
                    ++slotIndex;
                }
            }
        }
        return successful;
    }

    /**
     * Get the inventory of the player for which this container is instantiated.
     * @return The player inventory.
     */
    public IInventory getPlayerIInventory() {
        return playerIInventory;
    }

    @Override
    public ItemStack slotClick(int slotId, int keyOrdinal, int clickType, EntityPlayer player) {
        ItemStack itemStack = super.slotClick(slotId, keyOrdinal, clickType, player);
        if(keyOrdinal == 0 && clickType == 0 && slotId >= 0) {
            getSlot(slotId).putStack(getSlot(slotId).getStack());
        }
        return itemStack;
    }
}