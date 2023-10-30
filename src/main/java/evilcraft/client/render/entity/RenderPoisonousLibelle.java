package evilcraft.client.render.entity;

import evilcraft.core.client.render.entity.RenderModelLiving;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import evilcraft.core.config.extendedconfig.MobConfig;
import evilcraft.entity.monster.PoisonousLibelle;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

/**
 * Renderer for a libelle
 * @author rubensworks
 */
public class RenderPoisonousLibelle extends RenderModelLiving<ModelBase> {

    /**
     * Make a new instance
     * @param config The config.
     * @param model The model.
     * @param par2 No idea...
     */
    public RenderPoisonousLibelle(ExtendedConfig<MobConfig> config, ModelBase model, float par2) {
        super(config, model, par2);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float f) {
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glTranslatef(0, 0.5F, 0);
    }

    private void renderLibelle(PoisonousLibelle libelle, double x, double y, double z, float yaw, float partialTickTime) {
        super.doRender(libelle, x, y, z, yaw, partialTickTime);
    }

    @Override
    public void doRender(EntityLiving par1EntityLiving, double x, double y, double z, float yaw, float partialTickTime) {
        this.renderLibelle((PoisonousLibelle)par1EntityLiving, x, y, z, yaw, partialTickTime);
    }
}