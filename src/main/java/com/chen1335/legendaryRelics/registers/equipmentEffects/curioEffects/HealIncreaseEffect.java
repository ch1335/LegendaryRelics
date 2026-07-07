package com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.common.InfoHolder;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EquipmentEffectLevelArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

import java.util.List;

public class HealIncreaseEffect extends LRCurioEffect {


    @Calculator
    public static final FinalCalculator HEAL_INCREASE = FinalCalculator.of(DarkGoldUpdateArg.of(
            EquipmentEffectLevelArg.of(LevelBasedValue.perLevel(0.05f))
    ));

    public HealIncreaseEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.heal_increase_effect", HEAL_INCREASE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }


    public static void LivingHealEvent(LivingHealEvent event) {
        LivingEntity livingEntity = event.getEntity();
        EquipmentEffectAPI.findStackableEffect(livingEntity, LREquipmentEffectTypes.HEAL_INCREASE_EFFECT.value()).ifPresent(list -> {
            float i = 0;
            for (InfoHolder<HealIncreaseEffect> pair : list) {
                CalculatorArg args = CalculatorArg.simpleArg(livingEntity, pair.itemStack(), pair.effect());
                i += HEAL_INCREASE.getValue(args);
            }

            event.setAmount(event.getAmount() * (1 + i));

        });
    }
}
