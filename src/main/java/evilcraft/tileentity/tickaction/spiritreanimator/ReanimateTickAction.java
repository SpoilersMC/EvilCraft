package evilcraft.tileentity.tickaction.spiritreanimator;

import evilcraft.block.SpiritReanimatorConfig;
import evilcraft.core.helper.InventoryHelpers;
import evilcraft.core.helper.MathHelpers;
import evilcraft.core.tileentity.tickaction.ITickAction;
import evilcraft.core.tileentity.upgrade.UpgradeSensitiveEvent;
import evilcraft.core.tileentity.upgrade.Upgrades;
import evilcraft.tileentity.TileSpiritFurnace;
import evilcraft.tileentity.TileSpiritReanimator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.mutable.MutableDouble;

/**
 * {@link ITickAction} that is able to reanimate boxes with spirits.
 * @author rubensworks
 */
public class ReanimateTickAction implements ITickAction<TileSpiritReanimator> {

    @Override
    public boolean canTick(TileSpiritReanimator tile, ItemStack itemStack, int slot, int tick) {
        return tile.getTank().getFluidAmount() >= getRequiredMb(tile, tick) && tile.canWork();
    }

    protected ItemStack getCookStack(TileSpiritFurnace tile) {
        return tile.getInventory().getStackInSlot(tile.getConsumeSlot());
    }

    @Override
    public void onTick(TileSpiritReanimator tile, ItemStack itemStack, int slot, int tick) {
        // Drain the tank a bit.
        tile.getTank().drain(getRequiredMb(tile, tick), true);
        if(tick >= getRequiredTicks(tile, slot, tick)) {
            int entityID = tile.getEntityID();
            if(SpiritReanimatorConfig.clearBoxContents) {
                itemStack.setTagCompound(new NBTTagCompound());
            }
            if(entityID > -1) {
                ItemStack eggStack = new ItemStack((Item)Item.itemRegistry.getObject("spawn_egg"), 1, entityID);
                if(addToProduceSlot(tile, eggStack)) {
                    tile.getInventory().decrStackSize(TileSpiritReanimator.SLOT_EGG, 1);
                }
            }
        }
    }

    protected int getRequiredMb(TileSpiritReanimator tile, int tick) {
        MutableDouble drain = new MutableDouble(SpiritReanimatorConfig.mBPerTick);
        Upgrades.sendEvent(tile, new UpgradeSensitiveEvent<MutableDouble>(drain, TileSpiritReanimator.UPGRADEEVENT_BLOODUSAGE));
        return MathHelpers.factorToBursts(drain.getValue(), tick);
    }

    @Override
    public float getRequiredTicks(TileSpiritReanimator tile, int slot, int tick) {
        MutableDouble drain = new MutableDouble(SpiritReanimatorConfig.requiredTicks);
        Upgrades.sendEvent(tile, new UpgradeSensitiveEvent<MutableDouble>(drain, TileSpiritReanimator.UPGRADEEVENT_SPEED));
        return (int)(double)drain.getValue();
    }

    /**
     * Try to add the given item to the production slot.
     * @param tile The tile where reanimation happened.
     * @param itemStack The item to try to put in the output slot.
     * @return If the item could be added or joined in the output slot.
     */
    public boolean addToProduceSlot(TileSpiritReanimator tile, ItemStack itemStack) {
        return InventoryHelpers.addToSlot(tile.getInventory(), TileSpiritReanimator.SLOTS_OUTPUT, itemStack);
    }
}