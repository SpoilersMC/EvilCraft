package evilcraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.core.config.configurable.ConfigurableItem;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.config.extendedconfig.ItemConfig;
import evilcraft.entity.item.EntityItemEmpowerable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

/**
 * An inverted Potentia Sphere.
 * @author rubensworks
 */
public class InvertedPotentia extends ConfigurableItem implements IItemEmpowerable {

    private static InvertedPotentia _instance = null;

    /**
     * Meta data for the empowered state.
     */
    public static final int EMPOWERED_META = 1;

    /**
     * Initialise the configurable.
     * @param eConfig The config.
     */
    public static void initInstance(ExtendedConfig<ItemConfig> eConfig) {
        if(_instance == null)
            _instance = new InvertedPotentia(eConfig);
        else
            eConfig.showDoubleInitError();
    }

    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static InvertedPotentia getInstance() {
        return _instance;
    }

    private InvertedPotentia(ExtendedConfig<ItemConfig> eConfig) {
        super(eConfig);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean hasEffect(ItemStack itemStack, int pass) {
        return isEmpowered(itemStack);
    }

    @Override
    public ItemStack empower(ItemStack itemStack) {
        if(itemStack.getItem() == InvertedPotentia.getInstance()) {
            itemStack.setItemDamage(EMPOWERED_META);
        }
        return itemStack;
    }

    @Override
    public boolean isEmpowered(ItemStack itemStack) {
        return itemStack.getItem() == this && itemStack.getItemDamage() == EMPOWERED_META;
    }

    @Override @SideOnly(Side.CLIENT) @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        if(isEmpowered(itemStack))
            list.add(EnumChatFormatting.RED + "Empowered");
    }

    @Override @SideOnly(Side.CLIENT) @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        for(int i = 0; i < 2; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack itemStack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemStack) {
        return new EntityItemEmpowerable(world, (EntityItem)location);
    }
}