package com.chen1335.legendaryRelics.registers.armorSetEffect;

import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.common.calculator.special.TieredBonus;
import com.chen1335.legendaryRelics.effectInstances.BlackDragonEffectInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class BlackDragonArmorSetEffect extends SetEffect {
    @Calculator
    public static final FinalCalculator ATTRIBUTE_MULTIPLIER = FinalCalculator.of(
            TieredBonus.of(List.of(0.04F, 0.06F, 0.08F, 0.1F))
    );
    @Calculator
    public static final FinalCalculator HEALTH_REGAIN = FinalCalculator.of(
            Add.of(
                    Constant.of(1),
                    Mul.of(
                            EntityAttributeValue.of(Attributes.MAX_HEALTH),
                            TieredBonus.of(List.of(0.02F, 0.03F, 0.04F, 0.05F))
                    )
            )
    );

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = buildArg(player);
        tooltipComponents.add(Component.translatable("set_effect_type.legendary_relics.tiered_bonus", Component.translatable("set_effect.legendary_relics.excellence.name").append("(%s/4)".formatted(getPiece(player))).withColor(16733695)).withColor(16755200));
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon.desc.1",
                ATTRIBUTE_MULTIPLIER.toPercentageComponent(tooltipFlag.hasShiftDown(), args)
        ).withColor(16733695));
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon.desc.2",
                HEALTH_REGAIN.toComponent(tooltipFlag.hasShiftDown(), args)
        ).withColor(16733695));
    }

    @Override
    public EffectInstance createInstance(int piece) {
        return new BlackDragonEffectInstance(piece);
    }
}
