package com.chen1335.legendaryRelics.client.renderers.entityRenderers;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.ThrownFrostShard;
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

/**
 * 霜锥投掷物的渲染：直接把物品模型画出来，不需要额外的实体模型与纹理。
 *
 * <p>朝向沿用原版三叉戟投掷物（Y 轴对齐飞行偏航、Z 轴对齐俯仰），
 * 使用 {@link ItemDisplayContext#NONE} 以避免模型自带 display 变换带来的位移。
 */
public class ThrownFrostShardRender extends EntityRenderer<ThrownFrostShard> {

    private final ItemRenderer itemRenderer;

    public ThrownFrostShardRender(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ThrownFrostShard entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        ItemStack renderItem = entity.getRenderItem();
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())-45));
        poseStack.scale(2.5F,2.5F,1);
        itemRenderer.renderStatic(renderItem, ItemDisplayContext.NONE, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), entity.getId());
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ThrownFrostShard entity) {
        return MissingTextureAtlasSprite.getLocation();
    }
}
