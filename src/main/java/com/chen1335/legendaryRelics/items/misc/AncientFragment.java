package com.chen1335.legendaryRelics.items.misc;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.Constant;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AncientFragment extends Item {
    public AncientFragment() {
        super(new Properties().rarity(Rarity.EPIC));
    }

    public static FinalCalculator ANCIENT_FRAGMENT_BOOST = FinalCalculator.of(Constant.of(0.1F));

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.legendary_relics.ancient_fragment.desc", ANCIENT_FRAGMENT_BOOST.toPercentageComponent(tooltipFlag.hasShiftDown(), CalculatorArg.emptyArg())).withColor(0xaeaeae));
    }
}
