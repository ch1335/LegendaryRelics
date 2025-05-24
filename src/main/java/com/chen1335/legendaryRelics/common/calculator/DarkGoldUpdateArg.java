package com.chen1335.legendaryRelics.common.calculator;

import com.chen1335.legendaryRelics.API.objects.LRRarities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DarkGoldUpdateArg implements Unit {

    private final Unit a;
    private final Unit b;

    public DarkGoldUpdateArg(Unit a, Unit b) {
        this.a = a;
        this.b = b;
    }


    @Override
    public float getValue(CalculatorArg calculatorArg) {
        @Nullable ItemStack itemStack = CalculatorArg.ArgType.THIS_ITEMS_STACK.getArg(calculatorArg);
        if (itemStack != null && itemStack.getRarity() == LRRarities.DARK_GOLD.getValue()) {
            return b.getValue(calculatorArg);
        } else {
            return a.getValue(calculatorArg);
        }
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        @Nullable ItemStack itemStack = CalculatorArg.ArgType.THIS_ITEMS_STACK.getArg(calculatorArg);
        if (itemStack != null && itemStack.getRarity() == LRRarities.DARK_GOLD.getValue()) {
            return b.toComponent(calculatorArg).copy().withColor(0xff8c00);
        } else {
            return a.toComponent(calculatorArg);
        }
    }

    public static DarkGoldUpdateArg of(Unit a, Unit b) {
        return new DarkGoldUpdateArg(a, b);
    }
}
