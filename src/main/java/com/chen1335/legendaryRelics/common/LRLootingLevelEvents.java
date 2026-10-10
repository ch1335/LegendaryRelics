package com.chen1335.legendaryRelics.common;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.common.InfoHolder;
import com.chen1335.equipmentEffectLib.common.SlotEffectHolder;
import com.chen1335.legendaryRelics.API.objects.LRDataComponentTypes;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.events.EnchantedLootEvent;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.dataComponentTypes.CollectedMinerals;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects.OreCollectorEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Optional;

/**
 * 装备效果改附魔等级：
 * <ul>
 *     <li>抢夺：让装备效果加掉落数量/概率</li>
 *     <li>时运：矿石收集者戒指按收集量额外加等级</li>
 * </ul>
 *
 * <p>方块掉落时 THIS_ENTITY 是挖矿的玩家，实体掉落时是被击杀的生物。
 */
@EventBusSubscriber(modid = LegendaryRelics.MODID)
public final class LRLootingLevelEvents {

    @SubscribeEvent
    public static void onEnchantedLoot(EnchantedLootEvent event) {
        Holder<Enchantment> enchantment = event.getEnchantment();
        LootContext context = event.getContext();

        if (enchantment.is(Enchantments.LOOTING)) {
            modifyLootingLevel(event, context);
        } else if (enchantment.is(Enchantments.FORTUNE)) {
            modifyFortuneLevel(event, context);
        }
    }

    private static void modifyLootingLevel(EnchantedLootEvent event, LootContext context) {
        Entity killed = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        Entity attacker = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
        if (!(killed instanceof LivingEntity mob) || !(attacker instanceof LivingEntity livingAttacker)) {
            return;
        }

        int level = event.getLevel();
        for (InfoHolder<?> holder : livingAttacker.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA.get()).collectAllEffects()) {
            level = holder.effect().modifyLootingLevel(holder.itemStack(), mob, livingAttacker, level);
        }

        event.setLevel(level);
    }

    /** 接管原来写在 CurioInventoryCapabilityMixin 里的逻辑：收集到的矿物够数就额外 +1。 */
    private static void modifyFortuneLevel(EnchantedLootEvent event, LootContext context) {
        if (!(context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof LivingEntity living)) {
            return;
        }

        Optional<SlotEffectHolder<OreCollectorEffect>> pairOptional =
                EquipmentEffectAPI.findBestEffect(living, LREquipmentEffectTypes.ORE_COLLECTOR_EFFECT.value());
        if (pairOptional.isEmpty()) {
            return;
        }

        SlotEffectHolder<OreCollectorEffect> pair = pairOptional.get();
        CalculatorArg args = CalculatorArg.simpleArg(living, pair.infoHolder().itemStack(), pair.infoHolder().effect());
        int addition = pair.infoHolder().itemStack()
                .getOrDefault(LRDataComponentTypes.COLLECTED_MINERALS, CollectedMinerals.empty())
                .getCollectedOresAmount() >= OreCollectorEffect.COLLECTED_MINERALS_REQUIRE.getInt(args) ? 1 : 0;

        event.setLevel(event.getLevel() + (int) (OreCollectorEffect.DEFAULT.getValue(args) + addition));
    }

    private LRLootingLevelEvents() {
    }
}