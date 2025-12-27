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

public class MultiAdd implements IBracketsNeedUnit {

    public static final StreamCodec<RegistryFriendlyByteBuf, MultiAdd> STREAM_CODEC = StreamCodec.composite(
            CalculatorRegister.DISPATCH_STREAM_CODEC.apply(ByteBufCodecs.list()),
            value -> value.units,
            MultiAdd::new
    );

    private final List<Unit> units;

    public MultiAdd(List<Unit> units) {
        this.units = units;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        float i = 0;
        for (Unit unit : units) {
            i += unit.getValue(calculatorArg);
        }
        return i;
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        MutableComponent component = Component.empty();
        for (Unit unit : units) {
            component.append(unit.toComponent(calculatorArg));
            if (units.getLast() != unit) {
                component.append("+");
            }
        }
        return component;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Unit> getStreamCodec() {
        return Cast.cast(STREAM_CODEC);
    }

    public static MultiAdd of(Unit... units) {
        return new MultiAdd(List.of(units));
    }
}
