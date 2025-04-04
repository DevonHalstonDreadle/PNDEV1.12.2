package net.lepidodendron.entity.render.entity;

import net.lepidodendron.LepidodendronMod;
import net.lepidodendron.entity.EntityPrehistoricFloraMawsonia;
import net.lepidodendron.entity.model.entity.ModelMawsonia;
import net.lepidodendron.entity.render.RenderLivingBaseWithBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderMawsonia extends RenderLivingBaseWithBook<EntityPrehistoricFloraMawsonia> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(LepidodendronMod.MODID + ":textures/entities/mawsonia.png");

    public static float getScaler() {
        return 1.0625F;
    }
    public RenderMawsonia(RenderManager mgr) {
        super(mgr, new ModelMawsonia(), 0.0f);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityPrehistoricFloraMawsonia entity) {
        return RenderMawsonia.TEXTURE;
    }

    @Override
    protected void applyRotations(EntityPrehistoricFloraMawsonia entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
    @Override
    protected void preRenderCallback(EntityPrehistoricFloraMawsonia entity, float f) {
        float scale = entity.getAgeScale() * this.getScaler() ;
        if (scale < 0.1f) {
            scale = 0.1f;
        }
        GlStateManager.scale(scale, scale, scale);
        this.shadowSize = 0;
    }

}