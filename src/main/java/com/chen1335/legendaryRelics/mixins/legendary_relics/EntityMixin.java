package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.mixinsAPI.IAbstractArrowExtension;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.EnderSuppression;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;


@Mixin(Entity.class)
public class EntityMixin {


    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void remove(Entity.RemovalReason reason, CallbackInfo ci) {
        if (Entity.class.cast(this) instanceof AbstractArrow arrow) {
            IAbstractArrowExtension arrowExtension = (IAbstractArrowExtension) arrow;
            byte pierceLevel = arrow.getPierceLevel();
            if (arrow.piercingIgnoreEntityIds != null && reason == Entity.RemovalReason.DISCARDED && arrow.piercingIgnoreEntityIds.size() < pierceLevel + 1 && arrowExtension.lr$isHittingEntity()) {
                ci.cancel();
            }
        }
    }

    /**
     * 末影抑制：阻止同维度传送。
     */
    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"), cancellable = true)
    private void lr$blockTeleport(double x, double y, double z, CallbackInfo ci) {
        if (EnderSuppression.isSuppressed(Entity.class.cast(this))) {
            ci.cancel();
        }
    }

    /**
     * 带维度的重载：只在目标维度与当前维度相同时阻止，跨维度交给 EntityTravelToDimensionEvent。
     */
    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z", at = @At("HEAD"), cancellable = true)
    private void lr$blockTeleportSameDimension(ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeMovements, float yRot, float xRot, CallbackInfoReturnable<Boolean> cir) {
        Entity self = Entity.class.cast(this);
        if (level == self.level() && EnderSuppression.isSuppressed(self)) {
            cir.setReturnValue(false);
        }
    }
}
