package com.chen1335.legendaryRelics.common.calculator;

import net.minecraft.network.chat.Component;

public class Constant implements Unit{
    private final float amount;

    public Constant(float amount) {
        this.amount = amount;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        return amount;
    }

    public Component toComponent(CalculatorArg calculatorArg) {
        return Component.literal(String.format("%.2f", amount));
    }

    public static Constant of(float amount){
        return new Constant(amount);
    }
}
