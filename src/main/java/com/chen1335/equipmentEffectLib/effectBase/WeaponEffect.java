package com.chen1335.equipmentEffectLib.effectBase;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WeaponEffect extends BaseEffect {
    public WeaponEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
    }
}
