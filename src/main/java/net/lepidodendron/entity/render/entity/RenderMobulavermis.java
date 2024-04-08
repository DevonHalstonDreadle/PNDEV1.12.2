package net.lepidodendron.entity.render.entity;

import net.lepidodendron.LepidodendronMod;
import net.lepidodendron.entity.EntityPrehistoricFloraMobulavermis;
import net.lepidodendron.entity.model.entity.ModelMobulavermis;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderMobulavermis extends RenderLiving<EntityPrehistoricFloraMobulavermis> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(LepidodendronMod.MODID + ":textures/entities/mobulavermis.png");

    public static float getScaler() {
        return 0.2F;
    }

    public RenderMobulavermis(RenderManager mgr) {
        super(mgr, new ModelMobulavermis(), 0.2f);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityPrehistoricFloraMobulavermis entity) {
        return RenderMobulavermis.TEXTURE;
    }

    @Override
    protected void applyRotations(EntityPrehistoricFloraMobulavermis entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    protected void preRenderCallback(EntityPrehistoricFloraMobulavermis entity, float f) {
        float scale = this.getScaler();
        GlStateManager.scale(scale, scale, scale);
        this.shadowSize = entity.width * scale * 0.38F;
    }

}