package com.chen1335.legendaryRelics.common.calculator.normal;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.api.IBracketsNeedUnit;
import com.chen1335.legendaryRelics.common.calculator.api.Unit;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.logging.log4j.util.Cast;

import java.util.List;

public class MultiMul implements Unit {
    public static final StreamCodec<RegistryFriendlyByteBuf, MultiMul> STREAM_CODEC = StreamCodec.composite(
            CalculatorRegister.DISPATCH_STREAM_CODEC.apply(ByteBufCodecs.list()),
            value -> value.units,
            MultiMul::new
    );

    private final List<Unit> units;

    public MultiMul(List<Unit> units) {
        this.units = units;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        float i = 1;
        for (Unit unit : units) {
            i = i * unit.getValue(calculatorArg);
        }

        return i;
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        MutableComponent component = Component.empty();
        for (Unit unit : units) {
            if (unit instanceof IBracketsNeedUnit) {
                component.append("(").append(unit.toComponent(calculatorArg)).append(")");
            } else {
                component.append(unit.toComponent(calculatorArg));
            }
            if (units.getLast() != unit) {
                component.append("x");
            }
        }
        return component;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Unit> getStreamCodec() {
        return Cast.cast(STREAM_CODEC);
    }

    public static MultiMul of(Unit... units) {
        return new MultiMul(List.of(units));
    }
}
