package com.chen1335.legendaryRelics.common.calculator;

import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class SingleCustomArg<T> implements Unit {

    private final CalculatorArg.ArgType<T> argType;
    private final NumberGetter<T> function;
    private final Function<T, Component> componentFunction;

    public SingleCustomArg(CalculatorArg.ArgType<T> argType, NumberGetter<T> function, Function<T, Component> componentFunction) {
        this.argType = argType;
        this.function = function;
        this.componentFunction = componentFunction;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        T arg = argType.getArg(calculatorArg);
        return function.get(arg).floatValue();
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        T arg = argType.getArg(calculatorArg);
        return componentFunction.apply(arg);
    }

    //Caused by: dev.latvian.mods.rhino.EvaluatorException: Cannot convert 1.0 to R
    public interface NumberGetter<T> {
        Number get(T arg);
    }

    public static <T> SingleCustomArg<T> of(CalculatorArg.ArgType<T> argType, NumberGetter<T> function, Function<T, Component> componentFunction) {
        return new SingleCustomArg<>(argType, function, componentFunction);
    }
}
