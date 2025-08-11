package com.chen1335.legendaryRelics.common.calculator;

import net.minecraft.network.chat.Component;

public class Add implements IBracketsNeedUnit {
    private final Unit a;
    private final Unit b;


    public Add(Unit a, Unit b) {
        this.a = a;
        this.b = b;
    }


    @Override
    public float getValue(CalculatorArg calculatorArg) {
        return a.getValue(calculatorArg) + b.getValue(calculatorArg);
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        return Component.empty().append(a.toComponent(calculatorArg)).append("+").append(b.toComponent(calculatorArg));
    }


    public static Add of(Unit a, Unit b) {
        return new Add(a, b);
    }
}
