package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.AttributeAdjustment;
import com.chen1335.arcanumlib.modules.attribute.events.LivingGetAttributeAdjustmentEvent;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EquipmentEffectLevelArg;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.List;

public class InFireTargetDamageIncrease extends LRCurioEffect {


    @Calculator
    public static final FinalCalculator DAMAGE_INCREASE = FinalCalculator.of(DarkGoldUpdateArg.of(
            EquipmentEffectLevelArg.of(LevelBasedValue.perLevel(0.1F, 0.05F), 3)
    ));

    public InFireTargetDamageIncrease(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.in_fire_target_damage_increase", DAMAGE_INCREASE.toComponent(tooltipFlag.hasShiftDown(), args, Style.EMPTY, 100, "%s%%")).withColor(0xaeaeae));
    }

    public static void getAttributeAdjustment(LivingGetAttributeAdjustmentEvent event, DamageSource source, LivingEntity target, LivingEntity attacker) {
        if (target.isOnFire()) {
            LREquipmentEffectTypes.IN_FIRE_TARGET_DAMAGE_INCREASE.value().findBestEffect(attacker).ifPresent(slotHolder -> {
                CalculatorArg calculatorArg = CalculatorArg.simpleArg(attacker, slotHolder.infoHolder().itemStack(), slotHolder.infoHolder().effect());
                event.getAdjustment(attacker, ArcanumAttributes.DAMAGE_MULTIPLIER)
                        .add(AttributeAdjustment.Operation.ADD_MULTIPLIED_TOTAL, DAMAGE_INCREASE.getValue(calculatorArg));
            });
        }
    }

}
