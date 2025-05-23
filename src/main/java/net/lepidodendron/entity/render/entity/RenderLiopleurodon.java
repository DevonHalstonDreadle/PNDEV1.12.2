package net.lepidodendron.entity.render.entity;

import net.lepidodendron.LepidodendronMod;
import net.lepidodendron.entity.EntityPrehistoricFloraLiopleurodon;
import net.lepidodendron.entity.model.entity.ModelLiopleurodon;
import net.lepidodendron.entity.render.RenderLivingBaseWithBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderLiopleurodon extends RenderLivingBaseWithBook<EntityPrehistoricFloraLiopleurodon> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(LepidodendronMod.MODID + ":textures/entities/liopleurodon.png");

    public RenderLiopleurodon(RenderManager mgr) {
        super(mgr, new ModelLiopleurodon(), 0.3f);
    }

    public static float getScaler() {
        return 0.77F;
    }

    @Override
    public ResourceLocation getEntityTexture(EntityPrehistoricFloraLiopleurodon entity) {
        return RenderLiopleurodon.TEXTURE;
    }

    @Override
    protected void applyRotations(EntityPrehistoricFloraLiopleurodon entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    protected void preRenderCallback(EntityPrehistoricFloraLiopleurodon entity, float f) {
        float scale = entity.getAgeScale() * this.getScaler();
        GlStateManager.scale(scale, scale, scale);
        this.shadowSize = entity.width * scale * 0.6F;
    }

}