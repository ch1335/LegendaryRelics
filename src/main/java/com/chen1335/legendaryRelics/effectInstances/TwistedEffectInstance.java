package com.chen1335.legendaryRelics.effectInstances;

import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.registers.armorSetEffect.TwistedArmorSetEffect;
import com.chen1335.legendaryRelics.registers.attachmentDatas.LRProjectileData;
import com.chen1335.legendaryRelics.utils.LRUtil;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class TwistedEffectInstance extends EffectInstance {
    public int coolDown = 0;

    public boolean isDoingAdditionShoot = false;

    public TwistedEffectInstance(int piece) {
        super(piece);
    }

    @Override
    public void tick(LivingEntity living) {
        coolDown = Math.max(coolDown - 1, 0);
    }


    public void modifyArrow(AbstractArrow arrow, LivingEntity owner) {
        LRProjectileData projectileData = LRUtil.getProjectileData(arrow);
        SpecialEffectAPI.getEffect(owner, owner.getUUID(), LRSpecialMobEffects.PHASE_SHOOTING.value()).ifPresent(phaseShooting -> {
            if (phaseShooting.getStack() >= 10) {
                projectileData.ignoreHitCooldown = true;
            }
        });

        if (isDoingAdditionShoot) {
            projectileData.damageMul = projectileData.damageMul * TwistedArmorSetEffect.ADDITION_ARROW_BASE_DAMAGE.getValue(buildArgs(owner));
        }
    }
}
