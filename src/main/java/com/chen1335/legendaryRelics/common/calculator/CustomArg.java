package com.chen1335.legendaryRelics.common.calculator;

import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class CustomArg<T> implements Unit {

    private final CalculatorArg.ArgType<T> argType;
    private final Function<T, Float> function;
    private final Component name;

    public CustomArg(CalculatorArg.ArgType<T> argType, Function<T, Float> function, Component name) {
        this.argType = argType;
        this.function = function;
        this.name = name;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        T arg = argType.getArg(calculatorArg);
        return function.apply(arg);
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        return name;
    }

    public static <T> CustomArg<T> of(CalculatorArg.ArgType<T> argType, Function<T, Float> function, Component name) {
        return new CustomArg<>(argType, function, name);
    }
}
