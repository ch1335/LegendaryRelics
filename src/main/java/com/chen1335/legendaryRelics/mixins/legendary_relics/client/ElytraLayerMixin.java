package com.chen1335.legendaryRelics.mixins.legendary_relics.client;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.registers.items.armor.blackDragonSet.BlackDragonChestPlate;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ElytraLayer.class)
public class ElytraLayerMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void shouldRender(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() == LRItems.BLACK_DRAGON_CHEST_PLATE.get()) {
            cir.setReturnValue(true);
        }
    }

    @ModifyArg(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    private ResourceLocation modifyResourcelocation(ResourceLocation location, @Local ItemStack itemStack) {
        if (itemStack.getItem() == LRItems.BLACK_DRAGON_CHEST_PLATE.get()) {
            return BlackDragonChestPlate.getElytraTexture();
        }
        return location;
    }
}
