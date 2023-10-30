package evilcraft.tileentity;

import com.google.common.collect.Lists;
import evilcraft.Configs;
import evilcraft.api.ILocation;
import evilcraft.block.BoxOfEternalClosure;
import evilcraft.block.BoxOfEternalClosureConfig;
import evilcraft.block.DarkBloodBrick;
import evilcraft.block.SpiritFurnace;
import evilcraft.core.algorithm.Size;
import evilcraft.core.algorithm.Sizes;
import evilcraft.core.block.AllowedBlock;
import evilcraft.core.block.CubeDetector;
import evilcraft.core.block.HollowCubeDetector;
import evilcraft.core.fluid.BloodFluidConverter;
import evilcraft.core.fluid.ImplicitFluidConversionTank;
import evilcraft.core.fluid.SingleUseTank;
import evilcraft.core.helper.LocationHelpers;
import evilcraft.core.helper.MinecraftHelpers;
import evilcraft.core.inventory.slot.SlotFluidContainer;
import evilcraft.core.tileentity.NBTPersist;
import evilcraft.core.tileentity.tickaction.ITickAction;
import evilcraft.core.tileentity.tickaction.TickComponent;
import evilcraft.core.tileentity.upgrade.IUpgradeSensitiveEvent;
import evilcraft.core.tileentity.upgrade.UpgradeBehaviour;
import evilcraft.core.tileentity.upgrade.Upgrades;
import evilcraft.core.world.FakeWorldItemDelegator;
import evilcraft.core.world.FakeWorldItemDelegator.IItemDropListener;
import evilcraft.fluid.Blood;
import evilcraft.network.PacketHandler;
import evilcraft.network.packet.DetectionListenerPacket;
import evilcraft.tileentity.tickaction.EmptyFluidContainerInTankTickAction;
import evilcraft.tileentity.tickaction.EmptyItemBucketInTankTickAction;
import evilcraft.tileentity.tickaction.spiritfurnace.BoxCookTickAction;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidContainerItem;
import org.apache.commons.lang3.mutable.MutableDouble;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A furnace that is able to cook spirits for their inner entity drops.
 * @author rubensworks
 */
public class TileSpiritFurnace extends TileWorking<TileSpiritFurnace, MutableDouble> implements IItemDropListener {

    /**
     * The id of the fluid container drainer slot.
     */
    public static final int SLOT_CONTAINER = 0;
    /**
     * The id of the box slot.
     */
    public static final int SLOT_BOX = 1;
    /**
     * The id of the drops slot.
     */
    public static final int[] SLOTS_DROP = new int[] { 2, 3, 4, 5 };

    /**
     * The total amount of slots in this machine.
     */
    public static final int SLOTS = 2 + SLOTS_DROP.length;

    /**
     * The name of the tank, used for NBT storage.
     */
    public static String TANKNAME = "spiritFurnaceTank";
    /**
     * The capacity of the tank.
     */
    public static final int LIQUID_PER_SLOT = FluidContainerRegistry.BUCKET_VOLUME * 10;
    /**
     * The amount of ticks per mB the tank can accept per tick.
     */
    public static final int TICKS_PER_LIQUID = 2;
    /**
     * The fluid that is accepted in the tank.
     */
    public static final Fluid ACCEPTED_FLUID = Blood.getInstance();

    /**
     * The multiblock structure detector for this furnace.
     */
    public static CubeDetector detector = new HollowCubeDetector(new AllowedBlock[] {
            new AllowedBlock(DarkBloodBrick.getInstance()), new AllowedBlock(SpiritFurnace.getInstance()).setMaxOccurences(1) },
            Lists.newArrayList(SpiritFurnace.getInstance(), DarkBloodBrick.getInstance())).setMinimumSize(new Size(2, 2, 2));

    private static final Map<Class<?>, ITickAction<TileSpiritFurnace>> BOX_COOK_TICK_ACTIONS = new LinkedHashMap<Class<?>, ITickAction<TileSpiritFurnace>>();
    static {
        BOX_COOK_TICK_ACTIONS.put(BoxOfEternalClosure.class, new BoxCookTickAction());
    }

    private static final Map<Class<?>, ITickAction<TileSpiritFurnace>> EMPTY_IN_TANK_TICK_ACTIONS = new LinkedHashMap<Class<?>, ITickAction<TileSpiritFurnace>>();
    static {
        EMPTY_IN_TANK_TICK_ACTIONS.put(IFluidContainerItem.class, new EmptyFluidContainerInTankTickAction<TileSpiritFurnace>());
        EMPTY_IN_TANK_TICK_ACTIONS.put(Item.class, new EmptyItemBucketInTankTickAction<TileSpiritFurnace>());
    }

    public static final Upgrades.UpgradeEventType UPGRADEEVENT_SPEED = Upgrades.newUpgradeEventType();
    public static final Upgrades.UpgradeEventType UPGRADEEVENT_BLOODUSAGE = Upgrades.newUpgradeEventType();

    @NBTPersist
    private Size size = Size.NULL_SIZE.copy();
    @NBTPersist
    private Boolean forceHalt = false;
    @NBTPersist
    private Boolean caughtError = false;
    private int cookTicker;
    private EntityLiving boxEntityCache = null;

    /**
     * Make a new instance.
     */
    public TileSpiritFurnace() {
        super(SLOTS, SpiritFurnace.getInstance().getLocalizedName(), LIQUID_PER_SLOT, TileSpiritFurnace.TANKNAME, ACCEPTED_FLUID);
        cookTicker = addTicker(new TickComponent<TileSpiritFurnace, ITickAction<TileSpiritFurnace>>(this, BOX_COOK_TICK_ACTIONS, SLOT_BOX));
        addTicker(new TickComponent<TileSpiritFurnace, ITickAction<TileSpiritFurnace>>(this, EMPTY_IN_TANK_TICK_ACTIONS, SLOT_CONTAINER, false));

        // The slots side mapping
        List<Integer> inSlots = new LinkedList<Integer>();
        inSlots.add(SLOT_BOX);
        List<Integer> inSlotsTank = new LinkedList<Integer>();
        inSlotsTank.add(SLOT_CONTAINER);
        List<Integer> outSlots = new LinkedList<Integer>();
        for(int slot : SLOTS_DROP) {
            outSlots.add(slot);
        }
        addSlotsToSide(ForgeDirection.EAST, inSlotsTank);
        addSlotsToSide(ForgeDirection.UP, inSlots);
        addSlotsToSide(ForgeDirection.DOWN, outSlots);
        addSlotsToSide(ForgeDirection.SOUTH, outSlots);
        addSlotsToSide(ForgeDirection.WEST, outSlots);

        // Upgrade behaviour
        upgradeBehaviour.put(UPGRADE_SPEED, new UpgradeBehaviour<TileSpiritFurnace, MutableDouble>(1) {
            @Override
            public void applyUpgrade(TileSpiritFurnace upgradable, Upgrades.Upgrade upgrade, int upgradeLevel, IUpgradeSensitiveEvent<MutableDouble> event) {
                if(event.getType() == UPGRADEEVENT_SPEED) {
                    double val = event.getObject().getValue();
                    val /= (1 + upgradeLevel / valueFactor);
                    event.getObject().setValue(val);
                }
                if(event.getType() == UPGRADEEVENT_BLOODUSAGE) {
                    double val = event.getObject().getValue();
                    val *= (1 + upgradeLevel / valueFactor);
                    event.getObject().setValue(val);
                }
            }
        });
        upgradeBehaviour.put(UPGRADE_EFFICIENCY, new UpgradeBehaviour<TileSpiritFurnace, MutableDouble>(2) {
            @Override
            public void applyUpgrade(TileSpiritFurnace upgradable, Upgrades.Upgrade upgrade, int upgradeLevel, IUpgradeSensitiveEvent<MutableDouble> event) {
                if(event.getType() == UPGRADEEVENT_BLOODUSAGE) {
                    double val = event.getObject().getValue();
                    val /= (1 + upgradeLevel / valueFactor);
                    event.getObject().setValue(val);
                }
            }
        });
    }

    @Override
    protected SingleUseTank newTank(String tankName, int tankSize) {
        return new ImplicitFluidConversionTank(tankName, tankSize, this, BloodFluidConverter.getInstance());
    }

    @Override
    protected int getWorkTicker() {
        return cookTicker;
    }

    /**
     * Get the entity that is contained in a box.
     * @return The entity or null if no box or invalid box.
     */
    @SuppressWarnings("unchecked")
    public EntityLiving getEntity() {
        ItemStack boxStack = getInventory().getStackInSlot(getConsumeSlot());
        if(boxStack != null && boxStack.getItem() == getAllowedCookItem()) {
            String id = BoxOfEternalClosure.getInstance().getSpiritName(boxStack);
            if(id != null) {
                // We cache the entity inside 'boxEntityCache' for obvious efficiency reasons.
                if(boxEntityCache != null && id.equals(EntityList.getEntityString(boxEntityCache))) {
                    return boxEntityCache;
                } else {
                    Class<? extends EntityLivingBase> entityClass = (Class<? extends EntityLivingBase>)EntityList.stringToClassMapping.get(id);
                    if(entityClass != null) {
                        FakeWorldItemDelegator world = FakeWorldItemDelegator.getInstance();
                        EntityLiving entity = (EntityLiving)EntityList.createEntityByName(id, world);
                        boxEntityCache = entity;
                        return entity;
                    }
                }
            }
        }
        return null;
    }

    public String getPlayerId() {
        ItemStack boxStack = getInventory().getStackInSlot(getConsumeSlot());
        if(boxStack != null && boxStack.getItem() == getAllowedCookItem()) {
            return BoxOfEternalClosure.getInstance().getPlayerId(boxStack);
        }
        return "";
    }

    public String getPlayerName() {
        ItemStack boxStack = getInventory().getStackInSlot(getConsumeSlot());
        if(boxStack != null && boxStack.getItem() == getAllowedCookItem()) {
            return BoxOfEternalClosure.getInstance().getPlayerName(boxStack);
        }
        return "";
    }

    public boolean isPlayer() {
        return !getPlayerId().isEmpty();
    }

    /**
     * Get the size of the box entity.
     * @return The box entity size.
     */
    public Size getEntitySize() {
        EntityLiving entity = getEntity();
        if(entity == null) {
            return Size.NULL_SIZE;
        }
        return Sizes.getEntitySize(entity);
    }

    /**
     * If the size is valid for the contained entity to cook. It will check the inner size of the furnace and the size of
     * the entity.
     * @return If it is valid.
     */
    public boolean isSizeValidForEntity() {
        EntityLiving entity = getEntity();
        if(entity == null) {
            return false;
        }
        Size requiredSize = getEntitySize();
        return getInnerSize().compareTo(requiredSize) >= 0;
    }

    @Override
    public boolean canWork() {
        Size size = getSize();
        return size.compareTo(TileSpiritFurnace.detector.getMinimumSize()) >= 0;
    }

    /**
     * Check if the spirit furnace on the given location is valid and can start working.
     * @param world The world.
     * @param location The location.
     * @return If it is valid.
     */
    public static boolean canWork(World world, ILocation location) {
        TileEntity tile = LocationHelpers.getTile(world, location);
        if(tile != null) {
            return ((TileSpiritFurnace)tile).canWork();
        }
        return false;
    }

    /**
     * Get the allowed cooking item for this furnace.
     * @return The allowed item.
     */
    public static Item getAllowedCookItem() {
        Item allowedItem = Items.apple;
        if(Configs.isEnabled(BoxOfEternalClosureConfig.class)) {
            allowedItem = Item.getItemFromBlock(BoxOfEternalClosure.getInstance());
        }
        return allowedItem;
    }

    /**
     * Callback for when a structure has been detected for a spirit furnace block.
     * @param world The world.
     * @param location The location of one block of the structure.
     * @param size The size of the structure.
     * @param valid If the structure is being validated(/created), otherwise invalidated.
     */
    public static void detectStructure(World world, ILocation location, Size size, boolean valid) {
        int newMeta = valid ? 1 : 0;
        boolean change = LocationHelpers.getBlockMeta(world, location) != newMeta;
        LocationHelpers.setBlockMetadata(world, location, newMeta, MinecraftHelpers.BLOCK_NOTIFY_CLIENT);
        if(change) {
            int[] c = location.getCoordinates();
            PacketHandler.sendToAllAround(new DetectionListenerPacket(location, valid), LocationHelpers.createTargetPointFromLocation(world, location, 50));
        }
    }

    @Override
    public boolean canConsume(ItemStack itemStack) {
        return itemStack != null && getAllowedCookItem() == itemStack.getItem();
    }

    /**
     * Get the id of the infusion slot.
     * @return id of the infusion slot.
     */
    public int getConsumeSlot() {
        return SLOT_BOX;
    }

    /**
     * Get the ids of the result slots.
     * @return ids of the result slots.
     */
    public int[] getProduceSlots() {
        return SLOTS_DROP;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        if(slot == SLOT_BOX)
            return canConsume(itemStack);
        if(slot == SLOT_CONTAINER)
            return SlotFluidContainer.checkIsItemValid(itemStack, getTank());
        return false;
    }

    /**
     * @return the size
     */
    public Size getSize() {
        return size;
    }

    /**
     * @return the actual inner size.
     */
    public Size getInnerSize() {
        return (Size)getSize().subtract(new Size(1, 1, 1));
    }

    /**
     * @param size the size to set
     */
    public void setSize(Size size) {
        this.size = size;
        sendUpdate();
    }

    @Override
    public void onItemDrop(ItemStack itemStack) {
        boolean placed = false;
        int[] slots = getProduceSlots();
        int i = 0;

        // Try placing the item inside the inventory slots.
        while(!placed && i < slots.length) {
            ItemStack produceStack = getInventory().getStackInSlot(slots[i]);
            if(produceStack == null) {
                getInventory().setInventorySlotContents(slots[i], itemStack);
                placed = true;
            } else {
                if(produceStack.getItem() == itemStack.getItem() && produceStack.getMaxStackSize() >= produceStack.stackSize + itemStack.stackSize) {
                    produceStack.stackSize += itemStack.stackSize;
                    placed = true;
                }
            }
            i++;
        }
        // Halt the cooking if the item couldn't be placed
        forceHalt = !placed;
    }

    @Override
    public void resetWork(boolean hardReset) {
        forceHalt = false;
        caughtError = false;
        super.resetWork(hardReset);
    }

    /**
     * If the cooking is being halted because the inventory is full.
     * @return the forceHalt
     */
    public boolean isForceHalt() {
        return forceHalt;
    }

    /**
     * @return the caughtError
     */
    public boolean isCaughtError() {
        return caughtError;
    }

    /**
     * If an error was caught while killing a spirit.
     */
    public void caughtError() {
        this.caughtError = true;
    }
}