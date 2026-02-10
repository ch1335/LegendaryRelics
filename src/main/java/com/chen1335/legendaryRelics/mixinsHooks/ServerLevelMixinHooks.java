package com.chen1335.legendaryRelics.mixinsHooks;

import com.chen1335.legendaryRelics.armorSetEffect.TwistedArmorSetEffect;
import com.chen1335.legendaryRelics.effectInstances.TwistedEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class ServerLevelMixinHooks {

    public static void modifyNewEntity(Entity entity) {
        if (!entity.level().isClientSide) {
            if (entity instanceof AbstractArrow arrow && arrow.getOwner() instanceof LivingEntity owner) {
                TwistedEffectInstance effectInstance = TwistedArmorSetEffect.getEffectInstance(owner);
                if (effectInstance != null) {
                    effectInstance.modifyArrow(arrow,owner);
                }
            }
        }
    }
}
