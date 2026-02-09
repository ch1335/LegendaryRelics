package com.chen1335.legendaryRelics.mixins.legendary_relics.client;

import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
    @Shadow
    @Final
    private HumanoidModel<LivingEntity> outerModel;

    @Shadow
    @Final
    private HumanoidModel<LivingEntity> innerModel;

    @Inject(method = "renderTrim(Lnet/minecraft/core/Holder;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V", at = @At("HEAD"), cancellable = true)
    private void renderTrim(Holder<ArmorMaterial> p_323506_, PoseStack p_289687_, MultiBufferSource p_289643_, int p_289683_, ArmorTrim p_289692_, Model p_289663_, boolean p_289651_, CallbackInfo ci) {
        if (p_323506_.value() == LRArmorMaterials.BLACK_DRAGON.value()) {
            ci.cancel();
        }
    }

    @WrapOperation(method = "renderTrim(Lnet/minecraft/core/Holder;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void trimFix(Model instance, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, Operation<Void> original, @Local(argsOnly = true) boolean usesInnerModel) {
        if (usesInnerModel) {
            innerModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);
        } else {
            outerModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);
        }
    }
}
