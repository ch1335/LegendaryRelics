package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EquipmentEffectLevelArg;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.List;

public class HealPerSecondEffect extends LRCurioEffectBase {
    public HealPerSecondEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public HealPerSecondEffect(int level) {
        this(LREquipmentEffectTypes.HEAL_PER_SECOND_EFFECT.value(), level);
    }

    @Calculator
    public static final FinalCalculator HEAL_PER_5S = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    EquipmentEffectLevelArg.of(
                            LevelBasedValue.perLevel(1F)
                    )
            ),
            2
    );

    @Override
    public void curioTick(ItemStack itemStack, LivingEntity wearer) {
        CalculatorArg calculatorArg = CalculatorArg.simpleArg(wearer, itemStack, this);
        if (wearer.level().getGameTime() % 10 == 0) {
            wearer.heal(HEAL_PER_5S.getValue(calculatorArg) / 10);
        }
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.heal_per_second_effect", HEAL_PER_5S.toComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }
}
