package evilcraft.client.render.model;

import evilcraft.Reference;
import evilcraft.core.helper.RenderHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.Random;

/**
 * Model for the Box of Eternal Closure
 * @author rubensworks
 */
public class ModelBoxOfEternalClosure extends ModelBase {

    private static WavefrontObject mainModel = new WavefrontObject(new ResourceLocation(Reference.MOD_ID, Reference.MODEL_PATH + "box.obj"));
    private static WavefrontObject coverModel = new WavefrontObject(new ResourceLocation(Reference.MOD_ID, Reference.MODEL_PATH + "boxCover.obj"));

    private static ResourceLocation mainModelTexture = new ResourceLocation(Reference.MOD_ID, Reference.TEXTURE_PATH_MODELS + "box.png");
    private static ResourceLocation coverModelTexture = new ResourceLocation(Reference.MOD_ID, Reference.TEXTURE_PATH_MODELS + "boxCover.png");

    private static final ResourceLocation TEXTURE_NOISE = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation TEXTURE_DOTS = new ResourceLocation("textures/entity/end_portal.png");

    private float rotationX = -0.28F;
    private float rotationY = 0.19F;
    private float rotationZ = 0F;

    private float coverAngle = 0F;

    private static final Random rand = new Random(31100L);
    private FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);

    /**
     * Make a new instance.
     */
    public ModelBoxOfEternalClosure() {

    }

    /**
     * Set the angle of the lid.
     * @param coverAngle The lid angle.
     */
    public void setCoverAngle(float coverAngle) {
        this.coverAngle = coverAngle;
    }

    private FloatBuffer appendToBuffer(float a, float b, float c, float d) {
        this.buffer.clear();
        this.buffer.put(a).put(b).put(c).put(d);
        this.buffer.flip();
        return this.buffer;
    }

    private void renderContent() {
        float f1 = 0;
        float f2 = 0;
        float f3 = 0;
        double x = 0;
        double y = 0;
        double z = 0;

        GL11.glDisable(GL11.GL_LIGHTING);
        rand.setSeed(31100L);
        float depth = 0.75F;

        for(int i = 0; i < 16; ++i) {
            GL11.glPushMatrix();
            float iInverse = (float)(16 - i);
            float scale = 0.0625F;
            float brightness = 1.0F / (iInverse + 1.0F);

            if(i == 0) {
                RenderHelpers.bindTexture(TEXTURE_NOISE);
                brightness = 0.1F;
                iInverse = 65.0F;
                scale = 0.125F;
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            } else if(i == 1) {
                RenderHelpers.bindTexture(TEXTURE_DOTS);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
                scale = 0.5F;
            }

            float f8 = (float)(-(y + (double)depth));
            float f9 = f8 + ActiveRenderInfo.objectY;
            float f10 = f8 + iInverse + ActiveRenderInfo.objectY;
            float f2_2 = f9 / f10;
            f2_2 += (float)(y + (double)depth);
            GL11.glTranslatef(f1, f2_2, f3);
            GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
            GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.appendToBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.appendToBuffer(0.0F, 0.0F, 1.0F, 0.0F));
            GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, this.appendToBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, this.appendToBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
            GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
            GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
            GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, (float)(Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(0.5F, 0.5F, 0.0F);
            GL11.glRotatef((float)(i * i * 4321 + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
            GL11.glTranslatef(-f1, -f3, -f2);
            f9 = f8 + ActiveRenderInfo.objectY;
            GL11.glTranslatef(ActiveRenderInfo.objectX * iInverse / f9, ActiveRenderInfo.objectZ * iInverse / f9, -f2);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            float r = rand.nextFloat() * 0.5F + 0.5F;
            float b = rand.nextFloat() * 0.5F + 0.5F;

            if(i == 0) {
                r = 1.0F;
                b = 1.0F;
            }

            tessellator.setColorRGBA_F(r * brightness, 0F, b * brightness, 1.0F);
            tessellator.addVertex(x, y + (double)depth, z);
            tessellator.addVertex(x, y + (double)depth, z + 1.0D);
            tessellator.addVertex(x + 1.0D, y + (double)depth, z + 1.0D);
            tessellator.addVertex(x + 1.0D, y + (double)depth, z);
            tessellator.draw();
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * This method renders out all parts of the box model.
     */
    public void renderAll() {
        GL11.glRotatef(90F, 0F, 1F, 0F);
        GL11.glRotatef(180F, 1F, 0F, 0F);
        GL11.glTranslatef(-0.5F, -0.76F, -0.5F);
        GL11.glScalef(1F, 1F, 0.95F);
        RenderHelpers.bindTexture(mainModelTexture);
        mainModel.renderAll();

        GL11.glPushMatrix();
        GL11.glTranslatef(rotationX, rotationY, rotationZ);
        GL11.glRotatef(coverAngle, 0F, 0F, 1F);
        GL11.glTranslatef(-rotationX, -rotationY, -rotationZ);
        RenderHelpers.bindTexture(coverModelTexture);
        coverModel.renderAll();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 1.0F, 1.0F);
        GL11.glTranslatef(-0.5F, -0.6F, -0.5F);
        renderContent();
        GL11.glPopMatrix();
    }
}