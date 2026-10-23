package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.weaponEffects;

import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.EnderSuppression;
import com.chen1335.legendaryRelics.utils.LRUtil;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 末影抑制：直接攻击命中时给目标挂上末影抑制，使其无法传送。
 */
public class EnderSuppressionEffect extends LRWeaponEffect {

    /** 持续时间（秒）。 */
    @Calculator
    public static final FinalCalculator DURATION = FinalCalculator.of(
            Constant.of(3F)
    );

    public EnderSuppressionEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg arg = CalculatorArg.simpleArg(player, itemStack, this);
        CalculatorArg.ArgType.TOOLTIP_FLAG.putArg(arg, tooltipFlag);
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("equipment_effect.legendary_relics.ender_suppression.desc",
                DURATION.toComponent(arg)
        ).withColor(0xaeaeae), getMaxToolTipWidth(itemStack));
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        CalculatorArg arg = CalculatorArg.simpleArg(attacker, stack, this);
        EnderSuppression suppression = new EnderSuppression(LRSpecialMobEffects.ENDER_SUPPRESSION.value());
        suppression.setSourceEntity(attacker);
        suppression.initTime((int) (DURATION.getValue(arg) * 20));
        SpecialEffectAPI.addEffectToEntity(target, suppression, EnderSuppression::getFinal);
    }
}
