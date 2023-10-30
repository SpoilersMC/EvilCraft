package evilcraft.core.helper;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.registry.GameData;
import evilcraft.GeneralConfig;
import evilcraft.core.PlayerExtendedInventoryIterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

/**
 * Helpers for items.
 * @author rubensworks
 */
public class ItemHelpers {

    private static final int MB_FILL_PERTICK = GeneralConfig.mbFlowRate;

    /**
     * Check if the given item is activated.
     * @param itemStack The item to check
     * @return If it is an active container.
     */
    public static boolean isActivated(ItemStack itemStack) {
        return itemStack != null && itemStack.getTagCompound() != null && itemStack.getTagCompound().getBoolean("enabled");
    }

    /**
     * Toggle activation for the given item.
     * @param itemStack The item to toggle.
     */
    public static void toggleActivation(ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if(tag == null) {
            tag = new NBTTagCompound();
            itemStack.setTagCompound(tag);
        }
        tag.setBoolean("enabled", !isActivated(itemStack));
    }

    /**
     * Get the integer value of the given ItemStack.
     * @param itemStack The item to check.
     * @param tag The tag in NBT for storing this value.
     * @return The integer value for the given tag.
     */
    public static int getNBTInt(ItemStack itemStack, String tag) {
        if(itemStack == null || itemStack.getTagCompound() == null) {
            return 0;
        }
        return itemStack.getTagCompound().getInteger(tag);
    }

    /**
     * Set the integer value of the given ItemStack for the given tag.
     * @param itemStack The item to change.
     * @param integer The new integer value.
     * @param tag The tag in NBT for storing this value.
     */
    public static void setNBTInt(ItemStack itemStack, int integer, String tag) {
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if(tagCompound == null) {
            tagCompound = new NBTTagCompound();
            itemStack.setTagCompound(tagCompound);
        }
        tagCompound.setInteger(tag, integer);
    }

    /**
     * Run an auto-fill tick for filling currently held container items from this item.
     * @param item The item type to fill from.
     * @param itemStack The item stack to fill from.
     * @param world The world.
     * @param entity The entity that holds this item.
     */
    public static void updateAutoFill(IFluidContainerItem item, ItemStack itemStack, World world, Entity entity) {
        if(entity instanceof EntityPlayer && !world.isRemote) {
            FluidStack tickFluid = item.getFluid(itemStack);
            if(tickFluid != null && tickFluid.amount > 0) {
                EntityPlayer player = (EntityPlayer)entity;
                ItemStack held = player.getCurrentEquippedItem();
                tryFillContainerForPlayer(item, itemStack, held, tickFluid, player);
            }
        }
    }

    /**
     * Tries to fill a container item in a player inventory.
     * @param item The item container to drain from.
     * @param itemStack The stack to drain from.
     * @param toFill The container to try to fill.
     * @param tickFluid The fluid to fill with.
     * @param player The player that is the owner of toFill.
     */
    public static void tryFillContainerForPlayer(IFluidContainerItem item, ItemStack itemStack, ItemStack toFill, FluidStack tickFluid, EntityPlayer player) {
        if(toFill != null && toFill != itemStack && toFill.getItem() instanceof IFluidContainerItem && !player.isUsingItem()) {
            IFluidContainerItem fluidContainer = (IFluidContainerItem)toFill.getItem();
            FluidStack heldFluid = fluidContainer.getFluid(toFill);
            if(heldFluid == null || (heldFluid.isFluidEqual(tickFluid) && heldFluid.amount < fluidContainer.getCapacity(toFill))) {
                int filled = fluidContainer.fill(toFill, new FluidStack(tickFluid.getFluid(), Math.min(tickFluid.amount, MB_FILL_PERTICK)), true);
                item.drain(itemStack, filled, true);
            }
        }
    }

    /**
     * Check if the given player has at least one of the given item.
     * @param player The player.
     * @param item The item to search in the inventory.
     * @return If the player has the item.
     */
    public static boolean hasPlayerItem(EntityPlayer player, Item item) {
        for(PlayerExtendedInventoryIterator it = new PlayerExtendedInventoryIterator(player); it.hasNext();) {
            ItemStack itemStack = it.next();
            if(itemStack != null && itemStack.getItem() == item) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a list of variants from the given stack if its damage value is the wildcard value, otherwise the list will only contain the given itemstack.
     * @param itemStack The itemstack
     * @return The list of variants.
     */
    public static List<ItemStack> getVariants(ItemStack itemStack) {
        List<ItemStack> output = Lists.newLinkedList();
        if(itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
            itemStack.getItem().getSubItems(itemStack.getItem(), null, output);
        } else {
            output.add(itemStack);
        }
        return output;
    }

    /**
     * Parse a string to an itemstack. Expects the format "domain:itemname:amount:meta"
     * The domain and itemname are mandatory, the rest is optional.
     * @param itemStackString The string to parse.
     * @return The itemstack.
     * @throws IllegalArgumentException If the string was incorrectly formatted.
     */
    public static ItemStack parseItemStack(String itemStackString) {
        String[] split = itemStackString.split(":");
        String itemName = split[0] + ":" + split[1];
        Item item = GameData.getItemRegistry().getObject(itemName);
        if(item == null) {
            throw new IllegalArgumentException("Invalid ItemStack item: " + itemName);
        }
        int amount = 1;
        int meta = 0;
        if(split.length > 2) {
            try {
                amount = Integer.parseInt(split[2]);
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("Invalid ItemStack amount: " + split[2]);
            }
            if(split.length > 3) {
                try {
                    meta = Integer.parseInt(split[3]);
                } catch(NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid ItemStack meta: " + split[3]);
                }
            }
        }
        return new ItemStack(item, amount, meta);
    }
}