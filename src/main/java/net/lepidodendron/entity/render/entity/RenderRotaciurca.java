package net.lepidodendron.entity.render.entity;

import net.lepidodendron.LepidodendronMod;
import net.lepidodendron.entity.EntityPrehistoricFloraEldonia;
import net.lepidodendron.entity.EntityPrehistoricFloraRotaciurca;
import net.lepidodendron.entity.model.entity.ModelRotaciurca;
import net.lepidodendron.entity.render.RenderLivingBaseWithBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderRotaciurca extends RenderLivingBaseWithBook<EntityPrehistoricFloraRotaciurca> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(LepidodendronMod.MODID + ":textures/entities/rotaciurca.png");

    public static float getScaler() {return 0.35F;}

    public RenderRotaciurca(RenderManager mgr) {
        super(mgr, new ModelRotaciurca(), 0.0f);
        this.addLayer(new LayerRotaciurcaTentacle(this));
    }

    @Override
    public ResourceLocation getEntityTexture(EntityPrehistoricFloraRotaciurca entity) {
        return RenderRotaciurca.TEXTURE;
    }

    @Override
    protected void applyRotations(EntityPrehistoricFloraRotaciurca entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    protected void preRenderCallback(EntityPrehistoricFloraRotaciurca entity, float f) {
        float scale = getScaler();
        GlStateManager.scale(scale, scale, scale);
        this.shadowSize = 0F;
    }

    @Override
    public void doRender(EntityPrehistoricFloraRotaciurca entity, double x, double y, double z, float entityYaw, float partialTicks) {
        try {
            StackTraceElement[] elements = new Throwable().getStackTrace();
            String  callerClass = elements[4].getClassName();
            if (callerClass.equalsIgnoreCase("vazkii.patchouli.client.book.page.PageEntity")) {
                GlStateManager.pushMatrix();
                GlStateManager.disableCull();
                GlStateManager.enableAlpha();
                boolean flag = this.setDoRenderBrightness(entity, partialTicks);
                //Start of renders
                float scale = this.prepareScale(entity, partialTicks);
                //Main model
                this.bindTexture(TEXTURE);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.bookModel.renderStaticBook(scale);
                GlStateManager.disableBlend();
                //Layer1:
                this.bindTexture(LayerRotaciurcaTentacle.TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
                GlStateManager.enableNormalize();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                this.bookModel.renderStaticBook(scale);
                GlStateManager.disableBlend();
                GlStateManager.disableNormalize();
                GlStateManager.popMatrix();
                //End of renders
                if (flag)
                {
                    this.unsetBrightness();
                }
                GlStateManager.depthMask(true);
                GlStateManager.disableRescaleNormal();
                GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GlStateManager.enableTexture2D();
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                GlStateManager.enableCull();
                GlStateManager.popMatrix();
            }
            else {
                super.doRender(entity, x, y, z, entityYaw, partialTicks);
            }
        }
        catch (Exception e)
        {
            //Do nothing
        }
    }

}