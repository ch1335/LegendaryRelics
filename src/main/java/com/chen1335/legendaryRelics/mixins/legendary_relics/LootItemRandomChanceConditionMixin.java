package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.common.LootChanceBoost;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** random_chance：稀有概率按魔法发现提升，其余不变。 */
@Mixin(LootItemRandomChanceCondition.class)
public class LootItemRandomChanceConditionMixin {
    @ModifyVariable(method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", at = @At("STORE"), ordinal = 0)
    private float lr$applyMagicFind(float chance, @Local(argsOnly = true) LootContext context) {
        return LootChanceBoost.boost(chance, context);
    }
}
