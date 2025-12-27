package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EquipmentEffectLevelArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

import java.util.List;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class HealIncreaseEffect extends LRCurioEffectBase {
    public HealIncreaseEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public HealIncreaseEffect(int level) {
        this(LREquipmentEffectTypes.HEAL_INCREASE_EFFECT.value(), level);
    }

    @Calculator
    public static final FinalCalculator HEAL_INCREASE = FinalCalculator.of(DarkGoldUpdateArg.of(
            EquipmentEffectLevelArg.of(LevelBasedValue.perLevel(0.05f))
    ));


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.legendary_relics.healing_talisman.desc.1", HEAL_INCREASE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }


    @SubscribeEvent
    public static void HandleHealEvent(LivingHealEvent event) {
        LivingEntity livingEntity = event.getEntity();
        EquipmentEffectAPI.findStackableEffect(livingEntity, LREquipmentEffectTypes.HEAL_INCREASE_EFFECT.value()).ifPresent(list -> {
            float i = 0;
            for (Pair<ItemStack, BaseEffect> pair : list) {
                CalculatorArg args = CalculatorArg.simpleArg(livingEntity, pair.getFirst(), pair.getSecond());
                i += HEAL_INCREASE.getValue(args);
            }

            event.setAmount(event.getAmount() * (1 + i));

        });
    }
}
