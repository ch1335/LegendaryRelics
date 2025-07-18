package com.chen1335.legendaryRelics.items.misc;

import com.chen1335.legendaryRelics.API.objects.LRRarities;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.Constant;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DarkGoldForgingTool extends Item {
    public DarkGoldForgingTool() {
        super(new Properties().rarity(LRRarities.DARK_GOLD.getValue()).stacksTo(1));
    }

    public static FinalCalculator DARK_GOLD_BOOST = FinalCalculator.of(Constant.of(0.2F));

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {

        tooltipComponents.add(Component.translatable("item.legendary_relics.dark_gold_forging_tool.desc.1").withColor(0xff8c00));
        tooltipComponents.add(Component.translatable("item.legendary_relics.dark_gold_forging_tool.desc.2", DARK_GOLD_BOOST.toPercentageComponent(tooltipFlag.hasShiftDown(), CalculatorArg.emptyArg())).withColor(0xff8c00));
        tooltipComponents.add(Component.translatable("item.legendary_relics.dark_gold_forging_tool.desc.3").withColor(0xff8c00));
        tooltipComponents.add(Component.translatable("item.legendary_relics.dark_gold_forging_tool.desc.4").withColor(0xff8c00));
    }
}
