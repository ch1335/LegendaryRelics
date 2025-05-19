package com.chen1335.legendaryRelics.common.calculator;

import net.minecraft.network.chat.Component;

public class FinalCalculator implements Unit {
    private final Unit unit;

    public FinalCalculator(Unit unit) {
        this.unit = unit;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        return unit.getValue(calculatorArg);
    }

    public int getInt(CalculatorArg calculatorArg){
        return (int) getValue(calculatorArg);
    }
    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        return Component.empty().append(Component.literal(String.format("%.1f", getValue(calculatorArg))).withColor(16777215)).append("=(").append(unit.toComponent(calculatorArg)).append(")").withColor(5592405);
    }

    public Component toRawComponent() {
        return Component.empty().append("(").append(unit.toComponent(CalculatorArg.emptyArg())).append(")").withColor(5592405);
    }

    public Component toComponent(boolean hasShiftDown, CalculatorArg calculatorArg) {
        if (hasShiftDown) {
            return toComponent(calculatorArg);
        } else {
            return Component.literal(String.format("%.1f", getValue(calculatorArg)));
        }
    }

    public Component toPercentageComponent(boolean hasShiftDown, CalculatorArg calculatorArg) {
        if (hasShiftDown) {
            return Component.empty().append(Component.literal(String.format("%.1f%%", getValue(calculatorArg) * 100)).withColor(16777215)).append("=(").append(unit.toComponent(calculatorArg)).append(")").withColor(5592405);
        } else {
            return Component.literal(String.format("%.1f%%", getValue(calculatorArg) * 100));
        }
    }

    public static FinalCalculator of(Unit unit) {
        return new FinalCalculator(unit);
    }
}
