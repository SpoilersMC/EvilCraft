package evilcraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.client.gui.container.GuiExaltedCrafter;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.config.extendedconfig.ItemConfig;
import evilcraft.core.helper.MinecraftHelpers;
import evilcraft.core.inventory.NBTSimpleInventoryItemHeld;
import evilcraft.core.item.ItemGui;
import evilcraft.entity.item.EntityItemEmpowerable;
import evilcraft.inventory.container.ContainerExaltedCrafter;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

/**
 * A portable crafting table with a built-in ender chest.
 * @author rubensworks
 */
public class ExaltedCrafter extends ItemGui implements IItemEmpowerable {

    private static final String NBT_RETURNTOINNER = "returnToInner";

    private static ExaltedCrafter _instance = null;

    @SideOnly(Side.CLIENT)
    private IIcon woodenIcon;

    /**
     * Initialise the configurable.
     * @param eConfig The config.
     */
    public static void initInstance(ExtendedConfig<ItemConfig> eConfig) {
        if(_instance == null)
            _instance = new ExaltedCrafter(eConfig);
        else
            eConfig.showDoubleInitError();
    }

    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static ExaltedCrafter getInstance() {
        return _instance;
    }

    private ExaltedCrafter(ExtendedConfig<ItemConfig> eConfig) {
        super(eConfig);
        this.setMaxStackSize(1);

        if(MinecraftHelpers.isClientSide())
            setGUI(GuiExaltedCrafter.class);

        setContainer(ContainerExaltedCrafter.class);
    }

    @Override @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        woodenIcon = iconRegister.registerIcon(getIconString() + "_wooden");
        super.registerIcons(iconRegister);
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        if((damage & 1) == 1) {
            return woodenIcon;
        }
        return super.getIconFromDamage(damage);
    }

    @Override
    public boolean hasEffect(ItemStack itemStack, int pass) {
        return isEmpowered(itemStack);
    }

    @Override @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack itemStack) {
        return isEmpowered(itemStack) ? EnumRarity.uncommon : super.getRarity(itemStack);
    }

    @Override
    public boolean isEmpowered(ItemStack itemStack) {
        return (itemStack.getItemDamage() >> 1 & 1) == 1;
    }

    @Override
    public ItemStack empower(ItemStack itemStack) {
        if(itemStack.getItem() == this) {
            itemStack.setItemDamage(itemStack.getItemDamage() | 2);
        }
        return itemStack;
    }

    protected boolean isWooden(ItemStack itemStack) {
        return (itemStack.getItemDamage() & 1) == 1;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName() + (isWooden(itemStack) ? ".wood" : "");
    }

    @Override @SideOnly(Side.CLIENT) @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubItems(Item item, CreativeTabs tab, List itemList) {
        itemList.add(new ItemStack(item, 1, 0));
        itemList.add(new ItemStack(item, 1, 1));
        itemList.add(new ItemStack(item, 1, 2));
        itemList.add(new ItemStack(item, 1, 3));
    }

    /**
     * Get the supplementary inventory of the given crafter.
     * @param player The player using the crafter.
     * @param itemStack The item stack.
     * @param itemIndex The item index.
     * @return The inventory.
     */
    public IInventory getSupplementaryInventory(EntityPlayer player, ItemStack itemStack, int itemIndex) {
        if(isWooden(itemStack)) {
            return new NBTSimpleInventoryItemHeld(player, itemIndex, 27, 64);
        }
        return player.getInventoryEnderChest();
    }

    @Override @SideOnly(Side.CLIENT) @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        if(isEmpowered(itemStack))
            list.add(EnumChatFormatting.RED + "Empowered");
    }

    @Override
    public boolean hasCustomEntity(ItemStack itemStack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemStack) {
        return new EntityItemEmpowerable(world, (EntityItem)location);
    }

    public void setReturnToInner(ItemStack itemStack, boolean returnToInner) {
        if(itemStack.hasTagCompound()) {
            itemStack.getTagCompound().setBoolean(NBT_RETURNTOINNER, returnToInner);
        }
    }

    public boolean isReturnToInner(ItemStack itemStack) {
        if(itemStack.hasTagCompound()) {
            return itemStack.getTagCompound().getBoolean(NBT_RETURNTOINNER);
        }
        return false;
    }
}