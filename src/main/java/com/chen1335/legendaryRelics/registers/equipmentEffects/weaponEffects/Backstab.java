package com.chen1335.legendaryRelics.registers.equipmentEffects.weaponEffects;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.utils.LRUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

import java.util.List;

public class Backstab extends LRWeaponEffect {
    @Calculator
    public static final FinalCalculator DAMAGE_MUL = FinalCalculator.of(
            Constant.of(1.5F)
    );

    public Backstab(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("equipment_effect.legendary_relics.backstab", DAMAGE_MUL.toPercentageComponent(tooltipFlag.hasShiftDown(), CalculatorArg.simpleArg(player, itemStack, this))).withColor(0xaeaeae), getMaxToolTipWidth(itemStack));
    }

    public static void CriticalHitEvent(CriticalHitEvent event) {
        Player attacker = event.getEntity();
        if (event.getTarget() instanceof LivingEntity target) {
            double dot = attacker.getLookAngle().dot(target.calculateViewVector(target.getXRot(), target.yBodyRot));
            if (dot >= 0) {
                Backstab effect = EquipmentEffectAPI.getEffect(attacker.getWeaponItem(), LREquipmentEffectTypes.BACKSTAB.value());
                if (effect != null) {
                    event.setCriticalHit(true);
                    CalculatorArg args = CalculatorArg.simpleArg(attacker, attacker.getWeaponItem(), effect);
                    event.setDamageMultiplier(event.getDamageMultiplier() + DAMAGE_MUL.getValue(args));
                }
            }
        }
    }
}
