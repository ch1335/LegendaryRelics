package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects.TreadingSnowEffect;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 踏雪：佩戴雪羽斗篷时不再因细雪而冻结。
 *
 * <p>原版只认装备槽上的 {@code minecraft:freeze_immune_wearables} 标签（皮革盔甲），
 * Curios 槽的物品不在装备槽里，所以拦在这里。同时覆盖了细雪的寒冷积累与完全冻结后的伤害，
 * 因为那两处都以 {@code canFreeze()} 为前提。
 */
@Mixin(LivingEntity.class)
public class LivingEntityCanFreezeMixin {

    @ModifyReturnValue(method = "canFreeze", at = @At("RETURN"))
    private boolean legendary_relics$preventFreezing(boolean original) {
        return original && !TreadingSnowEffect.preventsPowderSnowSinking((LivingEntity) (Object) this);
    }
}
