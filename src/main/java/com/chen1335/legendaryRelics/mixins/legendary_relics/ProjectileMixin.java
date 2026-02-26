package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.mixinsAPI.IAbstractArrowExtension;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Projectile.class)
public class ProjectileMixin {
    @Inject(method = "hitTargetOrDeflectSelf", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;onHit(Lnet/minecraft/world/phys/HitResult;)V"))
    private void onHitEntityBefore(HitResult hitResult, CallbackInfoReturnable<ProjectileDeflection> cir) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult result1 = (EntityHitResult) hitResult;
            if (Projectile.class.cast(this) instanceof AbstractArrow arrow) {
                if (arrow.piercingIgnoreEntityIds == null) {
                    arrow.piercingIgnoreEntityIds = new IntOpenHashSet(5);
                }

                if (arrow.piercedAndKilledEntities == null) {
                    arrow.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
                }

                arrow.piercingIgnoreEntityIds.add(result1.getEntity().getId());
                ((IAbstractArrowExtension) arrow).lr$setIsHittingEntity(true);
            }
        }
    }

    @Inject(method = "hitTargetOrDeflectSelf", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;onHit(Lnet/minecraft/world/phys/HitResult;)V", shift = At.Shift.AFTER))
    private void onHitEntityAfter(HitResult hitResult, CallbackInfoReturnable<ProjectileDeflection> cir) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            if (Projectile.class.cast(this) instanceof AbstractArrow arrow) {
                ((IAbstractArrowExtension) arrow).lr$setIsHittingEntity(false);
            }
        }
    }
}
