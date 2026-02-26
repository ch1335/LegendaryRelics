package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.registers.attachmentDatas.LRProjectileData;
import com.chen1335.legendaryRelics.utils.LRUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageSource.class)
public class DamageSourceMixin {
    @Unique
    LRProjectileData lr$projectileData = null;

    @Inject(method = "<init>(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;)V", at = @At("RETURN"))
    private void init(Holder<?> type, Entity directEntity, Entity causingEntity, Vec3 damageSourcePosition, CallbackInfo ci) {
        if (directEntity instanceof Projectile projectile) {
            lr$projectileData = LRUtil.getProjectileData(projectile);
        }
    }

    @ModifyReturnValue(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("RETURN"))
    private boolean is(boolean original, @Local(argsOnly = true) TagKey<DamageType> damageTypeKey) {
        if (lr$projectileData != null) {
            if (damageTypeKey == DamageTypeTags.BYPASSES_COOLDOWN) {
                return original || lr$projectileData.ignoreHitCooldown;
            }
        }
        return original;
    }
}
