package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.mixinsAPI.IAbstractArrowExtension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


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
}
