package evilcraft.client.render.tileentity;

import evilcraft.Reference;
import evilcraft.core.helper.RenderHelpers;
import evilcraft.tileentity.TileSpiritPortal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Random;

/**
 * EvilCraft's version of a beacon renderer, this allows us to have custom colors and customize other stuff without being dependend on vanilla code
 * @author immortaleeb
 */
public class RenderTileEntitySpiritPortal extends TileEntitySpecialRenderer {

    private static final ResourceLocation PORTALBASE = new ResourceLocation(Reference.MOD_ID, Reference.TEXTURE_PATH_MODELS + "portalBases.png");

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialTickTime) {
        renderTileEntityAt((TileSpiritPortal)tileentity, x, y, z, partialTickTime);
    }

    protected void renderTileEntityAt(TileSpiritPortal tileentity, double x, double y, double z, float partialTickTime) {
        float progress = tileentity.getProgress();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5F, 0.5f, 0.5F);
        renderPortalBase((float)x, (float)y, (float)z, progress);
        GL11.glTranslatef((float)x, (float)y, (float)z);
        Random random = new Random();
        int seed = tileentity.xCoord + tileentity.yCoord + tileentity.zCoord;
        random.setSeed((long)seed);
        renderStar(seed, progress, Tessellator.instance, partialTickTime, random);
        GL11.glPopMatrix();
    }

    private void renderStar(float rotation, float progress, Tessellator tessellator, float partialTicks, Random random) {
        // Rotate opposite direction at 20% speed
        GL11.glRotatef(rotation * -0.2f % 360, 0.5f, 1, 0.5f);

        // Configuration tweaks
        float BEAM_START_DISTANCE = 2F;
        float BEAM_END_DISTANCE = 7f;
        float MAX_OPACITY = 40f;

        RenderHelper.disableStandardItemLighting();
        float f2 = 0.0F;

        if(progress > 0.8F) {
            f2 = (progress - 0.8F) / 0.2F;
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        int color1 = RenderHelpers.RGBToInt(171, 97, 210);
        int color2 = RenderHelpers.RGBToInt(175, 100, 215);

        for(int i = 0; i < (progress + progress * progress) / 2.0F * 60.0F; ++i) {
            GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(random.nextFloat() * 360.0F + progress * 90.0F, 0.0F, 0.0F, 1.0F);
            tessellator.startDrawing(6);
            float f3 = random.nextFloat() * BEAM_END_DISTANCE + 5.0F + f2 * 10.0F;
            float f4 = random.nextFloat() * BEAM_START_DISTANCE + 1.0F + f2 * 2.0F;
            tessellator.setBrightness(255);
            tessellator.setColorRGBA_I(color1, (int)(MAX_OPACITY * (1.0F - f2)));
            tessellator.addVertex(0.0D, 0.0D, 0.0D);
            tessellator.setColorRGBA_I(color2, 0);
            tessellator.addVertex(-0.866D * f4, f3, -0.5F * f4);
            tessellator.addVertex(0.866D * f4, f3, -0.5F * f4);
            tessellator.addVertex(0.0D, f3, 1.0F * f4);
            tessellator.addVertex(-0.866D * f4, f3, -0.5F * f4);
            tessellator.draw();
        }

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        RenderHelper.enableStandardItemLighting();
    }

    private void renderPortalBase(float x, float y, float z, float progress) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        GL11.glColor3f(0.72F, 0.5f, 0.83F);

        bindTexture(PORTALBASE);
        Tessellator tessellator = Tessellator.instance;
        RenderManager renderManager = RenderManager.instance;
        float r = 180.0F - renderManager.playerViewY;
        GL11.glRotatef(r, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewX, 1F, 0F, 0F);
        renderIconForProgress(tessellator, ((int)(progress * 100)) % 4, progress);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    private void renderIconForProgress(Tessellator tessellator, int index, float progress) {
        if(progress > 0.8F) {
            progress -= (progress - 0.8F) * 4;
        }

        float u1 = .0625f * index;
        float u2 = .0625f * (index + 1);
        float v1 = 0;
        float v2 = .0625f;

        GL11.glScalef(0.5f * progress, 0.5f * progress, 0.5f * progress);
        GL11.glTranslatef(-0.5F, -0.5f, 0);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.setBrightness(100);
        tessellator.addVertexWithUV(0, 1, 0.0D, u1, v2);
        tessellator.addVertexWithUV(0, 0, 0.0D, u1, v1);
        tessellator.addVertexWithUV(1, 0, 0.0D, u2, v1);
        tessellator.addVertexWithUV(1, 1, 0.0D, u2, v2);
        tessellator.draw();
    }
}