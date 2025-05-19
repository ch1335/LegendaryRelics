package com.chen1335.legendaryRelics.mixins.main;

import com.chen1335.legendaryRelics.items.curios.AgglomerationMalice;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootItemRandomChanceWithEnchantedBonusCondition.class)
public class LootItemRandomChanceWithEnchantedBonusConditionMixin {
    @WrapOperation(method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int test(Holder<Enchantment> holder, LivingEntity livingEntity, Operation<Integer> original, @Local(argsOnly = true) LootContext context) {
        int lootingLevel = original.call(holder, livingEntity);
        if (holder.is(Enchantments.LOOTING)) {
            Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
            Entity attacker = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
            if (entity instanceof Mob mob && attacker instanceof LivingEntity livingAttacker) {
                lootingLevel = AgglomerationMalice.modifyLoot(mob, livingAttacker, lootingLevel);
            }
        }
        return lootingLevel;
    }
}
