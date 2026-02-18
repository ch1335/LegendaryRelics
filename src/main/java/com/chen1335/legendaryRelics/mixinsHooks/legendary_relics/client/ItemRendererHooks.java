package com.chen1335.legendaryRelics.mixinsHooks.legendary_relics.client;

import com.chen1335.legendaryRelics.client.ArrowInBowRender;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemRendererHooks {

    public static void onRenderItem(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel pModel) {
        ArrowInBowRender.onRenderItem(itemStack, displayContext, leftHand, poseStack, bufferSource, combinedLight, combinedOverlay, pModel);
    }
}
