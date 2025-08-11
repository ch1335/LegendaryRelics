package com.chen1335.legendaryRelics.client.entityRenderers;

import com.chen1335.legendaryRelics.entities.TreatmentBall;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TreatmentBallRenderer extends EntityRenderer<TreatmentBall> {
    public TreatmentBallRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public static final List<Fragment> FRAGMENTS = new ArrayList<>();

    @Override
    public void render(@NotNull TreatmentBall treatmentBall, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {

    }


    public static class Fragment {

        public void render(PoseStack poseStack, VertexConsumer vertexConsumer) {

        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TreatmentBall entity) {
        return MissingTextureAtlasSprite.getLocation();
    }
}
