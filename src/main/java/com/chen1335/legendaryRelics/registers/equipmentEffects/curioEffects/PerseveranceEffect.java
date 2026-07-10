package com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.utils.SimpleSchedule;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class PerseveranceEffect extends LRCurioEffect {

    @Calculator
    public static final FinalCalculator TIME = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(10F),
            Constant.of(8F)
    ));

    @Calculator
    public static final FinalCalculator DAMAGE_PERCENTAGE = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(0.15F),
            Constant.of(0.20F)
    ));

    public PerseveranceEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.perseverance_effect", TIME.toComponent(tooltipFlag.hasShiftDown(), args), DAMAGE_PERCENTAGE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }

    public static void LivingDamageEvent(LivingDamageEvent.Post event) {
        LivingEntity livingEntity = event.getEntity();
        EquipmentEffectAPI.findBestEffect(livingEntity, LREquipmentEffectTypes.PERSEVERANCE_EFFECT.value()).ifPresent(pair -> {
            CalculatorArg calculatorArg = CalculatorArg.simpleArg(livingEntity, pair.infoHolder().itemStack(), pair.infoHolder().effect());
            int totalTime = TIME.getInt(calculatorArg) * 20;
            float totalHeal = DAMAGE_PERCENTAGE.getValue(calculatorArg) * Math.min(livingEntity.getMaxHealth(), event.getNewDamage());
            int runCount = totalTime / 10;
            float perHeal = totalHeal / runCount;
            SimpleSchedule.addSchedule(event.getEntity().level(), new SimpleSchedule.RepeatSchedule(runCount, 10, () -> {
                if (!event.getEntity().isRemoved()) {
                    event.getEntity().heal(perHeal);
                }
            }));
        });

    }
}
