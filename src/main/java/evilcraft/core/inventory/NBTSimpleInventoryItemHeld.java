package evilcraft.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import evilcraft.core.helper.InventoryHelpers;

/**
 * A simple inventory for a currently held item by a player that can be stored in NBT.
 * @author rubensworks
 */
public class NBTSimpleInventoryItemHeld extends SimpleInventory {

    public static final String NBT_TAG_ROOT = "NBTSimpleInventory";

    protected EntityPlayer player;
    protected int itemIndex;

    /**
     * Make a new instance.
     * @param player The player holding the item.
     * @param itemIndex The index of the item in use inside the player inventory.
     * @param size The amount of slots in the inventory.
     * @param stackLimit The stack limit for each slot.
     */
    public NBTSimpleInventoryItemHeld(EntityPlayer player, int itemIndex, int size, int stackLimit) {
        super(size, NBT_TAG_ROOT, stackLimit);
        this.player = player;
        this.itemIndex = itemIndex;
        InventoryHelpers.validateNBTStorage(this, InventoryHelpers.getItemFromIndex(player, itemIndex), NBT_TAG_ROOT);
    }

    @Override
    public void markDirty() {
        ItemStack itemStack = InventoryHelpers.getItemFromIndex(player, itemIndex);
        NBTTagCompound tag = itemStack.getTagCompound();
        if(tag == null) {
            tag = new NBTTagCompound();
            itemStack.setTagCompound(tag);
        }
        writeToNBT(tag, NBT_TAG_ROOT);
        InventoryHelpers.getItemFromIndex(player, itemIndex).setTagCompound(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound data, String tagName) {
        InventoryHelpers.readFromNBT(this, data, tagName);
    }

    @Override
    public void writeToNBT(NBTTagCompound data, String tagName) {
        InventoryHelpers.writeToNBT(this, data, tagName);
    }
}