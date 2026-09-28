package com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.equipmentEffects;

import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.common.ComponentHolders;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.specialMobEffects.MagicSuppression;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.weaponEffects.LRWeaponEffect;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 法术抑制：直接攻击命中时给目标挂上法术抑制，降低其造成的法术伤害。
 */
public class MagicSuppressionEffect extends LRWeaponEffect {

    /** 法术伤害降低比例。 */
    @Calculator
    public static final FinalCalculator DAMAGE_REDUCTION = FinalCalculator.of(
            Constant.of(0.3F)
    );

    /** 持续时间（秒）。 */
    @Calculator
    public static final FinalCalculator DURATION = FinalCalculator.of(
            Constant.of(5F)
    );

    public MagicSuppressionEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg arg = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.magic_suppression.desc",
                DURATION.toComponent(tooltipFlag.hasShiftDown(), arg),
                ComponentHolders.Combat.magicDamage(),
                DAMAGE_REDUCTION.toComponent(tooltipFlag.hasShiftDown(), arg, Style.EMPTY, 100, "%s%%")
        ).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("legendary_relics.special.effect_source_no_stack").withColor(5592405));
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        CalculatorArg arg = CalculatorArg.simpleArg(attacker, stack, this);
        MagicSuppression suppression = new MagicSuppression(DAMAGE_REDUCTION.getValue(arg));
        suppression.setSourceEntity(attacker);
        suppression.initTime((int) (DURATION.getValue(arg) * 20));
        SpecialEffectAPI.addEffectToEntity(target, suppression, MagicSuppression::getFinal);
    }
}
