package com.chen1335.legendaryRelics.mixins.legendary_relics.client;

import com.chen1335.legendaryRelics.API.ILRItemExtension;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    public int rightClickDelay;

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private void onUseItem(CallbackInfo ci, @Local ItemStack itemStack) {
        if (itemStack.getItem() instanceof ILRItemExtension extension && extension.isNoRightClickCooldown()) {
            rightClickDelay = 0;
        }
    }
}
