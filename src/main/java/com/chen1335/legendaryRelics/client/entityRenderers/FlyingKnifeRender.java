package com.chen1335.legendaryRelics.client.entityRenderers;

import com.chen1335.legendaryRelics.registers.entities.projectiles.misc.FlyingKnife;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FlyingKnifeRender extends EntityRenderer<FlyingKnife> {
    private final ItemRenderer itemRenderer;

    public FlyingKnifeRender(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(FlyingKnife flyingKnife, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        ItemStack weaponItem = flyingKnife.getRenderItem();
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, flyingKnife.yRotO, flyingKnife.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, flyingKnife.xRotO, flyingKnife.getXRot())-45));
        itemRenderer.renderStatic(weaponItem, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, flyingKnife.level(), 0);
        poseStack.popPose();
        super.render(flyingKnife, entityYaw, partialTick, poseStack, bufferSource, packedLight);

    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FlyingKnife entity) {
        return MissingTextureAtlasSprite.getLocation();
    }
}
