package evilcraft.core;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import evilcraft.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.Iterator;

/**
 * Iterate over a player's inventory and any other attached inventory like baubles.
 * @author rubensworks
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = Reference.MOD_BAUBLES, striprefs = true)
public class PlayerExtendedInventoryIterator implements Iterator<ItemStack> {

    private PlayerInventoryIterator innerIt;
    private boolean hasIteratedInner = false;
    private int maxBaublesSize = 4;
    private int baublesIterator = maxBaublesSize;
    private EntityPlayer player;

    /**
     * Create a new HotbarIterator.
     * @param player The player to iterate the hotbar from.
     */
    public PlayerExtendedInventoryIterator(EntityPlayer player) {
        this.player = player;
        innerIt = new PlayerInventoryIterator(player);
        if(Loader.isModLoaded(Reference.MOD_BAUBLES)) {
            setBaublesData();
        }
    }

    @Override
    public boolean hasNext() {
        return !hasIteratedInner || baublesIterator < maxBaublesSize;
    }

    @Override
    public ItemStack next() {
        if(hasIteratedInner && hasNext()) {
            ItemStack itemStack = getBaublesStack(baublesIterator);
            baublesIterator++;
            return itemStack;
        } else {
            ItemStack next = innerIt.next();
            if(!innerIt.hasNext()) {
                hasIteratedInner = true;
            }
            return next;
        }
    }

    @Optional.Method(modid = Reference.MOD_BAUBLES)
    protected ItemStack getBaublesStack(int index) {
        return BaublesApi.getBaubles(player).getStackInSlot(index);
    }

    @Optional.Method(modid = Reference.MOD_BAUBLES)
    protected void setBaublesStack(int index, ItemStack itemStack) {
        BaublesApi.getBaubles(player).setInventorySlotContents(index, itemStack);
    }

    @Optional.Method(modid = Reference.MOD_BAUBLES)
    protected void setBaublesData() {
        maxBaublesSize = BaublesApi.getBaubles(player).getSizeInventory();
        baublesIterator = 0;
    }

    @Override
    public void remove() {
        throw new RuntimeException("Not implemented.");
    }

    /**
     * Replaces the itemstack on the position of the last returned itemstack.
     * @param itemStack The itemstack to place.
     */
    public void replace(ItemStack itemStack) {
        if(hasIteratedInner && baublesIterator > 0) {
            setBaublesStack(baublesIterator - 1, itemStack);
        } else {
            innerIt.replace(itemStack);
        }
    }
}