package com.chen1335.legendaryRelics.common.calculator;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class MultiAdd implements IBracketsNeedUnit {
    private final List<Unit> units;

    public MultiAdd(List<Unit> units) {
        this.units = units;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        float i = 0;
        for (Unit unit : units) {
            i += unit.getValue(calculatorArg);
        }
        return i;
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        MutableComponent component = Component.empty();
        for (Unit unit : units) {
            component.append(unit.toComponent(calculatorArg));
            if (units.getLast() != unit) {
                component.append("+");
            }
        }
        return component;
    }

    public static MultiAdd of(Unit... units) {
        return new MultiAdd(List.of(units));
    }
}
