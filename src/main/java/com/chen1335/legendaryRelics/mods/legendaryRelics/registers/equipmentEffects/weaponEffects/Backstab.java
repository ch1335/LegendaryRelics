package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.weaponEffects;

import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.AttributeAdjustment;
import com.chen1335.arcanumlib.modules.attribute.events.LivingGetAttributeAdjustmentEvent;
import com.chen1335.arcanumlib.util.ComponentUtil;
import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.utils.CombatUtil;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.utils.LRUtil;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class Backstab extends LRWeaponEffect {
    /**
     * 暴击率加成 1.0 = 100%，即必定暴击。
     */
    @Calculator
    public static final FinalCalculator CRIT_CHANCE_BONUS = FinalCalculator.of(
            Constant.of(1.0F)
    );
    @Calculator
    public static final FinalCalculator CRIT_DAMAGE_BONUS = FinalCalculator.of(
            Constant.of(1.5F)
    );

    public Backstab(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        Component critChance = ComponentUtil.Attributes.attribute(ALObjects.Attributes.CRIT_CHANCE,
                CRIT_CHANCE_BONUS.toComponent(tooltipFlag.hasShiftDown(), args, Style.EMPTY, 100, "%s%%"),
                "+");
        Component critDamage = ComponentUtil.Attributes.attribute(ALObjects.Attributes.CRIT_DAMAGE,
                CRIT_DAMAGE_BONUS.toComponent(tooltipFlag.hasShiftDown(), args, Style.EMPTY, 100, "%s%%"),
                "+");
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("equipment_effect.legendary_relics.backstab",
                critChance, critDamage).withColor(0xaeaeae), getMaxToolTipWidth(itemStack));
    }

    public static void getAttributeAdjustment(LivingGetAttributeAdjustmentEvent event, DamageSource source, LivingEntity target, LivingEntity attacker) {
        if (CombatUtil.isLooseAttack(source)) {
            double dot = attacker.getLookAngle().dot(target.calculateViewVector(target.getXRot(), target.yBodyRot));
            if (dot >= 0) {
                Backstab effect = EquipmentEffectAPI.getEffect(attacker.getWeaponItem(), LREquipmentEffectTypes.BACKSTAB.value());
                if (effect != null) {

                    CalculatorArg args = CalculatorArg.simpleArg(attacker, attacker.getWeaponItem(), effect);
                    event.getAdjustment(attacker, ALObjects.Attributes.CRIT_CHANCE).add(AttributeAdjustment.Operation.ADD_VALUE, CRIT_CHANCE_BONUS.getValue(args));
                    event.getAdjustment(attacker, ALObjects.Attributes.CRIT_DAMAGE).add(AttributeAdjustment.Operation.ADD_VALUE, CRIT_DAMAGE_BONUS.getValue(args));
                }
            }
        }
    }
}
