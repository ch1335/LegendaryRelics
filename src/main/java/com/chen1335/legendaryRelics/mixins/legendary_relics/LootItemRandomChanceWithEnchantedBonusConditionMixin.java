package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.OreCollectorEffect;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
            if (entity instanceof LivingEntity mob && attacker instanceof LivingEntity livingAttacker) {
                for (EntityEquipmentEffectData.InfoHolder<?> collectAllEffect : livingAttacker.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA.get()).collectAllCurioEffects()) {
                    lootingLevel = collectAllEffect.effect().modifyLoot(collectAllEffect.itemStack(), mob, livingAttacker, lootingLevel);
                }
            }
        }
        return lootingLevel;
    }
}
