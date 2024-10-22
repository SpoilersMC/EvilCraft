package evilcraft.client.gui.container;

import evilcraft.core.client.gui.container.GuiContainerExtended;
import evilcraft.core.helper.InventoryHelpers;
import evilcraft.inventory.container.ContainerPrimedPendant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * GUI for the {@link evilcraft.item.PrimedPendant}.
 * @author rubensworks
 */
public class GuiPrimedPendant extends GuiContainerExtended {

    private static final int TEXTUREHEIGHT = 165;

    private EntityPlayer player;
    private int itemIndex;

    public GuiPrimedPendant(EntityPlayer player, int itemIndex) {
        super(new ContainerPrimedPendant(player, itemIndex));
        this.player = player;
        this.itemIndex = itemIndex;
    }

    @Override
    protected int getBaseYSize() {
        return TEXTUREHEIGHT;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);
        ItemStack itemStack = InventoryHelpers.getItemFromIndex(player, itemIndex);
        if(itemStack != null)
            this.fontRendererObj.drawString(itemStack.getDisplayName(), 28, 6, 4210752);
    }
}