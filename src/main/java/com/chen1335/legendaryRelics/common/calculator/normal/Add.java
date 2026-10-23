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


public class Add implements OperationUnit {
    private final OperationUnit a;
    private final OperationUnit b;

    public static final MapCodec<Add> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CalculatorRegister.DISPATCH_CODEC.fieldOf("a").forGetter(add -> add.a),
            CalculatorRegister.DISPATCH_CODEC.fieldOf("b").forGetter(add -> add.b)
    ).apply(instance, Add::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Add> STREAM_CODEC = StreamCodec.composite(
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.a,
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.b,
            Add::new
    );

    public Add(OperationUnit a, OperationUnit b) {
        this.a = a;
        this.b = b;
//        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "aaa");
    }


    @Override
    public float getValue(CalculatorArg calculatorArg) {
        return a.getValue(calculatorArg) + b.getValue(calculatorArg);
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        return Component.empty().append(a.toComponent(calculatorArg)).append("+").append(b.toComponent(calculatorArg));
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
        return CalculatorRegister.ADD.get();
    }


    public static Add of(OperationUnit a, OperationUnit b) {
        return new Add(a, b);
    }
}
