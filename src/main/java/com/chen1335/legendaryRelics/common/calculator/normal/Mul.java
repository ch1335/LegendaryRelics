package com.chen1335.legendaryRelics.common.calculator.normal;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.OperationType;
import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;


public class Mul implements OperationUnit {

    public static final MapCodec<Mul> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CalculatorRegister.DISPATCH_CODEC.fieldOf("a").forGetter(mul -> mul.a),
            CalculatorRegister.DISPATCH_CODEC.fieldOf("b").forGetter(mul -> mul.b)
    ).apply(instance, Mul::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Mul> STREAM_CODEC = StreamCodec.composite(
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.a,
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.b,
            Mul::new
    );

    private final OperationUnit a;
    private final OperationUnit b;

    public Mul(OperationUnit a, OperationUnit b) {
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
        Component componentB = b.toComponent(calculatorArg);
        return Component.empty().append(componentA).append("x").append(componentB);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends OperationUnit> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public MapCodec<? extends OperationUnit> getCodec() {
        return CODEC;
    }

    @Override
    public OperationType getType() {
        return CalculatorRegister.MUL.get();
    }

    public static Mul of(OperationUnit a, OperationUnit b) {
        return new Mul(a, b);
    }
}
