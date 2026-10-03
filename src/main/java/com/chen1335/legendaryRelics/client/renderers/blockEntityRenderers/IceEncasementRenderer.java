package com.chen1335.legendaryRelics.client.renderers.blockEntityRenderers;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.blocks.blockEntities.IceEncasementBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 把方块实体里保存的物品画在方块中心。
 *
 * <p>方块本身是半透明的冰，所以物品在外部可见 —— 前提是冰封方块注册了
 * {@code RenderType.translucent()} 渲染层（见客户端初始化）。
 */
public class IceEncasementRenderer implements BlockEntityRenderer<IceEncasementBlockEntity> {

    private final ItemRenderer itemRenderer;

    public IceEncasementRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(@NotNull IceEncasementBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemStack stack = blockEntity.getItem();
        if (stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        // 每块一个随机朝向，避免排成一片时看起来完全一样
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getRotation()));
        poseStack.scale(0.6F, 0.6F, 0.6F);
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer, blockEntity.getLevel(), 0);
        poseStack.popPose();
    }
}
