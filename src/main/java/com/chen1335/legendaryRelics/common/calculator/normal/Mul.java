package com.chen1335.legendaryRelics.common.calculator.normal;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.api.IBracketsNeedUnit;
import com.chen1335.legendaryRelics.common.calculator.api.Unit;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;


public class Mul implements Unit {

    public static final StreamCodec<RegistryFriendlyByteBuf, Mul> STREAM_CODEC = StreamCodec.composite(
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.a,
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.b,
            Mul::new
    );

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

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Unit> getStreamCodec() {
        return STREAM_CODEC;
    }

    public static Mul of(Unit a, Unit b) {
        return new Mul(a, b);
    }
}
