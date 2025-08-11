package com.chen1335.legendaryRelics.common.calculator;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class MultiMul implements Unit {
    private final List<Unit> units;

    public MultiMul(List<Unit> units) {
        this.units = units;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        float i = 1;
        for (Unit unit : units) {
            i = i * unit.getValue(calculatorArg);
        }

        return i;
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        MutableComponent component = Component.empty();
        for (Unit unit : units) {
            if (unit instanceof IBracketsNeedUnit) {
                component.append("(").append(unit.toComponent(calculatorArg)).append(")");
            } else {
                component.append(unit.toComponent(calculatorArg));
            }
            if (units.getLast() != unit) {
                component.append("x");
            }
        }
        return component;
    }

    public static MultiMul of(Unit... units) {
        return new MultiMul(List.of(units));
    }
}
