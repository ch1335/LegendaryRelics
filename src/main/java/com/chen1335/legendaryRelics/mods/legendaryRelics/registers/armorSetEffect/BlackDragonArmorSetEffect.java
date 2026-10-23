package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.armorSetEffect;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.arcanumlib.util.ComponentUtil;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

import static com.chen1335.legendaryRelics.effectInstances.BlackDragonEffectInstance.BLACK_DRAGON_ATTRIBUTE_MULTIPLIER;

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
        CalculatorArg.ArgType.TOOLTIP_FLAG.putArg(args, tooltipFlag);
        tooltipComponents.add(Component.translatable("set_effect_type.legendary_relics.tiered_bonus", Component.translatable("set_effect.legendary_relics.excellence.name").append("(%s/4)".formatted(getPiece(player))).withColor(16733695)).withColor(16755200));
        Component append = ComponentUtil.Attributes.attribute(ArcanumAttributes.ALL_ATTRIBUTE_MULTIPLIER,ATTRIBUTE_MULTIPLIER.toComponent(args, Style.EMPTY, 100, "%s%%"), "+");
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon.desc.1", append).withColor(16733695));
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon.desc.2",
                HEALTH_REGAIN.toComponent(args, Style.EMPTY)
        ).withColor(16733695));
    }

    @Override
    public EffectInstance createInstance(int piece) {
        return new BlackDragonEffectInstance(piece);
    }
}
