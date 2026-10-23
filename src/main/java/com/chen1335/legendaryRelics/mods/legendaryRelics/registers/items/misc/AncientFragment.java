package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.misc;

import com.chen1335.legendaryRelics.common.calculator.api.FormatedOperation.Format;

import com.chen1335.legendaryRelics.API.ILRItemExtension;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.utils.LRUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AncientFragment extends Item implements ILRItemExtension {
    public AncientFragment() {
        super(new Properties().rarity(Rarity.EPIC));
    }

    public static FinalCalculator ANCIENT_FRAGMENT_BOOST = FinalCalculator.of(Constant.of(0.1F)).withFormat(Format.PERCENTAGE);

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("item.legendary_relics.ancient_fragment.desc", ANCIENT_FRAGMENT_BOOST.toComponent(CalculatorArg.emptyArg().withTooltipFlag(tooltipFlag))).withColor(0xaeaeae), maxToolTipWidth(stack));
    }
}