package com.chen1335.legendaryRelics.common.calculator;

import net.minecraft.network.chat.Component;

public class Constant implements Unit {
    private final float amount;
    private final int i;

    public Constant(float amount) {
        this(amount, 2);
    }

    public Constant(float amount, int i) {
        this.amount = amount;
        this.i = i;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        return amount;
    }

    public Component toComponent(CalculatorArg calculatorArg) {
        return Component.literal(String.format("%." + i + "f", amount));
    }

    public static Constant of(float amount) {
        return new Constant(amount);
    }

    public static Constant of(float amount, int i) {
        return new Constant(amount, i);
    }
}
