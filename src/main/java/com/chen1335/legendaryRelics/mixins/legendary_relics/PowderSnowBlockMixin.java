package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects.TreadingSnowEffect;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 踏雪：佩戴雪羽斗篷时可以直接站在细雪上，不再陷入。
 *
 * <p>原版这个判断只看 {@code EquipmentSlot.FEET} 上的靴子（见 {@code canWalkOnPowderedSnow}），
 * Curios 槽的物品吃不到，所以这里补一条。
 */
@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @ModifyReturnValue(method = "canEntityWalkOnPowderSnow", at = @At("RETURN"))
    private static boolean legendary_relics$walkOnPowderSnow(boolean original, Entity entity) {
        return original || TreadingSnowEffect.preventsPowderSnowSinking(entity);
    }
}
