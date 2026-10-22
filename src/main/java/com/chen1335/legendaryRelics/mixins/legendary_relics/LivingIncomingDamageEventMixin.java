package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas.LRPlayerData;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.Invincibility;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LivingIncomingDamageEvent.class)
public class LivingIncomingDamageEventMixin {

    @Inject(method = "<init>(Lnet/minecraft/world/entity/LivingEntity;Lnet/neoforged/neoforge/common/damagesource/DamageContainer;)V", at = @At("TAIL"))
    private void lr$cancelIfInvincible(LivingEntity entity, DamageContainer container, CallbackInfo ci) {
        LivingIncomingDamageEvent event = (LivingIncomingDamageEvent) (Object) this;
        if (!entity.level().isClientSide && Invincibility.isInvincible(entity)) {
            event.setCanceled(true);
        }
        if (entity instanceof Player player) {
            LRPlayerData data = LRPlayerData.getData(player);
            if (data.isInvincible()) {
                event.setCanceled(true);
            }
        }
    }
}
