package com.chen1335.legendaryRelics.common.calculator;

import net.minecraft.network.chat.Component;

public class Mul implements Unit {
    private final Unit a;
    private final Unit b;

    public Mul(Unit a, Unit b) {
        this.a = a;
        this.b = b;
    }


    @Override
    public float getValue(CalculatorArg calculatorArg) {
        return a.getValue(calculatorArg) * b.getValue(calculatorArg);
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        Component componentA = a.toComponent(calculatorArg);
        if (a instanceof IBracketsNeedUnit) {
            componentA = Component.empty().append("(").append(componentA).append(")");
        }

        Component componentB = b.toComponent(calculatorArg);
        if (b instanceof IBracketsNeedUnit) {
            componentB = Component.empty().append("(").append(componentB).append(")");
        }


        return Component.empty().append(componentA).append("x").append(componentB);
    }

    public static Mul of(Unit a, Unit b) {
        return new Mul(a, b);
    }
}
