package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.attachmentDatas.LRProjectileData;
import com.chen1335.legendaryRelics.mixinsAPI.IAbstractArrowExtension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile implements IAbstractArrowExtension {
    protected AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "canHitEntity", at = @At("RETURN"), cancellable = true)
    private void canHitEntity(Entity target, CallbackInfoReturnable<Boolean> cir) {
        LRProjectileData data = this.getData(LRAttachmentTypes.PROJECTILE_DATA.get());
        if (data.lastHitEntity == target) {
            cir.setReturnValue(false);
        }
    }
}
