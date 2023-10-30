package evilcraft.client.render.tileentity;

import evilcraft.Reference;
import evilcraft.block.Purifier;
import evilcraft.tileentity.TilePurifier;
import evilcraft.tileentity.tickaction.purifier.DisenchantPurifyAction;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * Renderer for the item inside the {@link Purifier}.
 * @author rubensworks
 */
public class RenderTileEntityPurifier extends TileEntitySpecialRenderer {

    private static final ResourceLocation TEXTURE_BLOOK = new ResourceLocation(Reference.MOD_ID, Reference.TEXTURE_PATH_ENTITIES + "blook.png");
    private static final ResourceLocation TEXTURE_ENCHANTEDBOOK = new ResourceLocation(Reference.MOD_ID, Reference.TEXTURE_PATH_ENTITIES + "enchantedBook.png");
    private ModelBook enchantmentBook = new ModelBook();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
        TilePurifier tile = (TilePurifier)tileEntity;

        if(tile != null) {
            ItemStack additionalItem = tile.getAdditionalItem();
            if(additionalItem != null) {
                if(additionalItem.getItem() == DisenchantPurifyAction.ALLOWED_BOOK || additionalItem.getItem() == Items.enchanted_book) {
                    renderBook(tile, tile.getWorldObj(), additionalItem, x, y + 0.4, z, partialTickTime);
                } else {
                    renderAdditionalItem(tile, tile.getWorldObj(), additionalItem, x, y + 0.4, z, partialTickTime);
                }
            }
        }

        GL11.glPushMatrix();
        float var10 = (float)(x - 0.5F);
        float var11 = (float)(y - 0.5F);
        float var12 = (float)(z - 0.5F);
        GL11.glTranslatef(var10, var11, var12);

        if(tile != null) {
            if(tile.getPurifyItem() != null) {
                renderItem(tile.getWorldObj(), tile.getPurifyItem(), tile.getRandomRotation());
            }
        }
        GL11.glPopMatrix();
    }

    private void renderItem(World world, ItemStack itemStack, float rotation) {
        GL11.glPushMatrix();
        if(itemStack.getItem() instanceof ItemBlock) {
            GL11.glTranslatef(1F, 0.675F, 1F);
            GL11.glScalef(1.8F, 1.8F, 1.8F);
        } else {
            GL11.glTranslatef(1F, 0.875F, 0.8F);
            GL11.glRotatef(25F, 1, 0, 0);
            GL11.glRotatef(25F, 0, 1, 0);
            GL11.glRotatef(rotation, 0, 1, 0);
            GL11.glScalef(2F, 2F, 2F);
        }

        RenderItem.renderInFrame = true;
        EntityItem entity = new EntityItem(world, 0, 0, 0, itemStack);
        entity.hoverStart = 0.0F;
        RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;
        GL11.glPopMatrix();
    }

    private void renderAdditionalItem(TilePurifier tile, World world, ItemStack itemStack, double x, double y, double z, float partialTickTime) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
        float tick = (float)tile.tickCount + partialTickTime;
        GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(tick * 0.1F) * 0.01F, 0.0F);
        if(itemStack.getItem() instanceof ItemBlock) {
            GL11.glTranslatef(1F, 0.675F, 1F);
            GL11.glScalef(1.8F, 1.8F, 1.8F);
        } else {
            GL11.glRotatef(25F, 0, 1, 0);
            GL11.glScalef(2F, 2F, 2F);
        }

        float speedUp;

        for(speedUp = tile.additionalRotation2 - tile.additionalRotationPrev; speedUp >= (float)Math.PI; speedUp -= ((float)Math.PI * 2F)) {
        }

        while(speedUp < -(float)Math.PI) {
            speedUp += ((float)Math.PI * 2F);
        }

        float rotation = tile.additionalRotationPrev + speedUp * partialTickTime;
        GL11.glRotatef(-rotation * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);

        RenderItem.renderInFrame = true;
        EntityItem entity = new EntityItem(world, 0, 0, 0, itemStack);
        entity.hoverStart = 0.0F;
        RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;
        GL11.glPopMatrix();
    }

    private void renderBook(TilePurifier tile, World world, ItemStack itemStack, double x, double y, double z, float partialTickTime) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
        float tick = (float)tile.tickCount + partialTickTime;
        GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(tick * 0.1F) * 0.01F, 0.0F);
        float speedUp;

        for(speedUp = tile.additionalRotation2 - tile.additionalRotationPrev; speedUp >= (float)Math.PI; speedUp -= ((float)Math.PI * 2F)) {
        }

        while(speedUp < -(float)Math.PI) {
            speedUp += ((float)Math.PI * 2F);
        }

        float rotation = tile.additionalRotationPrev + speedUp * partialTickTime;
        GL11.glRotatef(-rotation * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);

        if(itemStack.getItem() == DisenchantPurifyAction.ALLOWED_BOOK)
            this.bindTexture(TEXTURE_BLOOK);
        else
            this.bindTexture(TEXTURE_ENCHANTEDBOOK);

        GL11.glEnable(GL11.GL_CULL_FACE);
        this.enchantmentBook.render((Entity)null, tick, 0, 0, 0, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }
}