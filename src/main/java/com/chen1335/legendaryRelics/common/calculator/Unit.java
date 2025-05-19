package com.chen1335.legendaryRelics.common.calculator;

import net.minecraft.network.chat.Component;

public interface Unit {
    float getValue(CalculatorArg calculatorArg);

    Component toComponent(CalculatorArg calculatorArg);
}
