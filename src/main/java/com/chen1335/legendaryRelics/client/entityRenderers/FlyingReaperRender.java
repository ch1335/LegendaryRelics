package com.chen1335.legendaryRelics.client.entityRenderers;

import com.chen1335.legendaryRelics.registers.entities.projectiles.misc.FlyingReaper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

public class FlyingReaperRender extends EntityRenderer<FlyingReaper> {
    private final ItemRenderer itemRenderer;

    public FlyingReaperRender(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }



    @Override
    public void render(@NotNull FlyingReaper entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.6, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() + 90.0F));
        if (entity.inGround()) {
            entity.currentRot = 200;
        } else {
            entity.currentRot += Math.max((float) (partialTicks * entity.getDeltaMovement().length() * 45), 10);
        }
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.currentRot));
        poseStack.scale(2,2,2);
        itemRenderer.renderStatic(entity.getWeaponItem() == null ? entity.getRenderStack() : entity.getWeaponItem(), ItemDisplayContext.NONE, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, null, 0);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FlyingReaper entity) {
        return MissingTextureAtlasSprite.getLocation();
    }
}
