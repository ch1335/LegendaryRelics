package com.chen1335.legendaryRelics.registers.equipmentEffects.weaponEffects;

import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.registers.specialMobEffects.Erosion;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ErosionEffect extends LRWeaponEffect {

    @Calculator
    public static final FinalCalculator DAMAGE_PER_LAYER = FinalCalculator.of(
            Mul.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(0.1F),
                            Constant.of(0.15F)
                    ),
                    EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
            )
    );

    @Calculator
    public static final FinalCalculator ARMOR_REDUCE_PER_LAYER = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.03F),
                    Constant.of(0.05F)
            )
    );

    @Calculator
    public static final FinalCalculator DAMAGE_PER_LAYER_FULL = FinalCalculator.of(
            Mul.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(0.4F),
                            Constant.of(0.6F)
                    ),
                    EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
            )
    );

    public ErosionEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg arg = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.erosion.1").withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.erosion.2", DAMAGE_PER_LAYER.toComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.erosion.3", ARMOR_REDUCE_PER_LAYER.toPercentageComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        CalculatorArg arg = CalculatorArg.simpleArg(attacker, stack,this);
        Erosion erosion = new Erosion(DAMAGE_PER_LAYER.getValue(arg),ARMOR_REDUCE_PER_LAYER.getValue(arg));
        erosion.setSourceEntity(attacker);
        erosion.initTime(100);
        SpecialEffectAPI.addEffectToEntity(target, erosion, Erosion::getFinal);
    }
}
