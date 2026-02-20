package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.attachmentDatas.LRProjectileData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow
    private Level level;

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void onRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        if (reason == Entity.RemovalReason.DISCARDED && level != null) {
            if (Entity.class.cast(this) instanceof AbstractArrow arrow && !level.isClientSide) {
                LRProjectileData data = arrow.getData(LRAttachmentTypes.PROJECTILE_DATA.get());
                if (data.pierceLevel > 0) {
                    data.pierceLevel--;
                    ci.cancel();
                }
            }
        }
    }
}
