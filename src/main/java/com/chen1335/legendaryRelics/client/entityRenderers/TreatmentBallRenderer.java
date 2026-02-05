package com.chen1335.legendaryRelics.client.entityRenderers;

import com.chen1335.legendaryRelics.entities.projectiles.misc.TreatmentBall;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
        double xOld = treatmentBall.xOld;
        double yOld = treatmentBall.yOld;
        double zOld = treatmentBall.zOld;

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.solid());

        PoseStack.Pose last = poseStack.last();
        buffer.addVertex(last, 0F, (float) yOld, 0F).setUv(0, 1).setUv2(0, 1).setNormal(0, 1, 0).setColor(255, 255, 255, 255);
        buffer.addVertex(last, 0F, (float) yOld + 1, 0F).setUv(0, 0).setUv2(0, 0).setNormal(0, 1, 0).setColor(255, 255, 255, 255);
        buffer.addVertex(last, 0F, (float) treatmentBall.getY() + 1, 0F).setUv(1, 0).setUv2(1, 0).setNormal(0, 1, 0).setColor(255, 255, 255, 255);
        buffer.addVertex(last, 0F, (float) treatmentBall.getY(), 0F).setUv(1, 1).setUv2(1, 1).setNormal(0, 1, 0).setColor(255, 255, 255, 255);

    }


    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TreatmentBall entity) {
        return MissingTextureAtlasSprite.getLocation();
    }
}
