package com.chen1335.legendaryRelics.client.entityRenderers;

import com.chen1335.legendaryRelics.entities.projectiles.misc.TreatmentBall;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.*;

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
