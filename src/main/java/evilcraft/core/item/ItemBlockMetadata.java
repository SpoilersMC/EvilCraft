package evilcraft.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import evilcraft.core.IInformationProvider;
import evilcraft.core.block.IBlockRarityProvider;
import evilcraft.core.helper.L10NHelpers;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * An extended {@link ItemBlockWithMetadata} that will automatically add information to the block item if that block implements {@link IInformationProvider}.
 * @author rubensworks
 */
public class ItemBlockMetadata extends ItemBlockWithMetadata {

    protected InformationProviderComponent informationProvider;
    protected IBlockRarityProvider rarityProvider = null;

    /**
     * Make a new instance.
     * @param block The block instance.
     */
    public ItemBlockMetadata(Block block) {
        super(block, block);
        informationProvider = new InformationProviderComponent(block);
        if(block instanceof IBlockRarityProvider) {
            rarityProvider = (IBlockRarityProvider)block;
        }
    }

    @Override @SideOnly(Side.CLIENT) @SuppressWarnings("rawtypes")
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        L10NHelpers.addOptionalInfo(list, getUnlocalizedName());
        informationProvider.addInformation(itemStack, entityPlayer, list, par4);
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        if(rarityProvider != null) {
            return rarityProvider.getRarity(itemStack);
        }
        return super.getRarity(itemStack);
    }
}