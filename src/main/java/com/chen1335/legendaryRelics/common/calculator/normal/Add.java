package com.chen1335.legendaryRelics.common.calculator.normal;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.api.IBracketsNeedUnit;
import com.chen1335.legendaryRelics.common.calculator.api.Unit;
import com.google.common.base.CaseFormat;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;


public class Add implements IBracketsNeedUnit {
    private final Unit a;
    private final Unit b;

    public static final StreamCodec<RegistryFriendlyByteBuf, Add> STREAM_CODEC = StreamCodec.composite(
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.a,
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.b,
            Add::new
    );

    public Add(Unit a, Unit b) {
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
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Unit> getStreamCodec() {
        return STREAM_CODEC;
    }


    public static Add of(Unit a, Unit b) {
        return new Add(a, b);
    }
}
