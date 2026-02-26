package com.chen1335.legendaryRelics.client.entityRenderers;

import com.chen1335.legendaryRelics.registers.entities.projectiles.misc.TreatmentBall;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TreatmentBallRenderer extends EntityRenderer<TreatmentBall> {
    public TreatmentBallRenderer(EntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    public void render(@NotNull TreatmentBall treatmentBall, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {

    }


    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TreatmentBall entity) {
        return MissingTextureAtlasSprite.getLocation();
    }


}
