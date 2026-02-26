package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.mixinsAPI.IAbstractArrowExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin implements IAbstractArrowExtension {
    @Unique
    private boolean lr$isHittingEntity = false;

    @WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;getPierceLevel()B"))
    private byte getPierceLevel(AbstractArrow instance, Operation<Byte> original) {
        return 0;
    }

    public boolean lr$isHittingEntity() {
        return lr$isHittingEntity;
    }

    public void lr$setIsHittingEntity(boolean lr$isHittingEntity) {
        this.lr$isHittingEntity = lr$isHittingEntity;
    }
}
