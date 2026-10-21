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

/**
 * 无敌效果的最高优先级拦截。
 *
 * <p>在事件构造完成、发布到事件总线之前就把它标记为已取消，
 * 因此优先级高于任何 {@code @SubscribeEvent} 监听器，
 * 其它免伤 / 增伤逻辑不会看到这次伤害。
 */
@Mixin(LivingIncomingDamageEvent.class)
public class LivingIncomingDamageEventMixin {

    @Inject(method = "<init>(Lnet/minecraft/world/entity/LivingEntity;Lnet/neoforged/neoforge/common/damagesource/DamageContainer;)V", at = @At("TAIL"))
    private void lr$cancelIfInvincible(LivingEntity entity, DamageContainer container, CallbackInfo ci) {
        if (!entity.level().isClientSide && Invincibility.isInvincible(entity)) {
            LivingIncomingDamageEvent event = (LivingIncomingDamageEvent) (Object) this;
            if (entity instanceof Player player) {
                LRPlayerData data = LRPlayerData.getData(player);
                if (data.isInvincible()) {
                    event.setCanceled(true);
                }
            }
            event.setCanceled(true);
        }
    }
}
