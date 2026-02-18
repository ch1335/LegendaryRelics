package com.chen1335.legendaryRelics.mixins.legendary_relics.client;

import com.chen1335.legendaryRelics.mixinsHooks.legendary_relics.client.ItemRendererHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
    private void onRenderItem(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model, CallbackInfo ci) {
        ItemRendererHooks.onRenderItem(itemStack, displayContext, leftHand, poseStack, bufferSource, combinedLight, combinedOverlay, p_model);
    }
}
