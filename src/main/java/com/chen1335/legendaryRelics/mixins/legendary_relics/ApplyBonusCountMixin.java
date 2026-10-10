package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.events.EnchantedLootEvent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 第四个"用附魔等级改战利品"的原版函数（apply_bonus，时运/抢夺加掉落数量）。
 * 和 BonusLevelTableCondition 一样用物品版 getItemEnchantmentLevel 且只看 TOOL。
 */
@Mixin(ApplyBonusCount.class)
public class ApplyBonusCountMixin {

    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/item/ItemStack;)I"))
    private int legendaryRelics$fireEnchantedLoot(Holder<Enchantment> holder, ItemStack tool,
                                                  Operation<Integer> original,
                                                  @Local(argsOnly = true) LootContext context) {
        return EnchantedLootEvent.post(holder, context, original.call(holder, tool));
    }
}