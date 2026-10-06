package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.common.LootChanceBoost;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** table_bonus：稀有概率按魔法发现提升，其余不变。 */
@Mixin(BonusLevelTableCondition.class)
public class BonusLevelTableConditionMixin {
    @ModifyVariable(method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", at = @At("STORE"), ordinal = 0)
    private float lr$applyMagicFind(float chance, @Local(argsOnly = true) LootContext context) {
        return LootChanceBoost.boost(chance, context);
    }
}
