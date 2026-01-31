package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantedCountIncreaseFunction.class)
public class EnchantedCountIncreaseFunctionMixin {

    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int run(Holder<Enchantment> holder, LivingEntity livingEntity, Operation<Integer> original, @Local(argsOnly = true) LootContext context) {
        int lootingLevel = original.call(holder, livingEntity);
        if (holder.is(Enchantments.LOOTING)) {
            Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
            Entity attacker = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
            if (entity instanceof LivingEntity mob && attacker instanceof LivingEntity livingAttacker) {
                for (EntityEquipmentEffectData.InfoHolder<?> collectAllEffect : livingAttacker.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA.get()).collectAllEffects()) {
                    lootingLevel = collectAllEffect.effect().modifyLoot(collectAllEffect.itemStack(), mob, livingAttacker, lootingLevel);
                }
            }
        }
        return lootingLevel;
    }
}
