package com.chen1335.legendaryRelics.common.calculator.normal;

import com.chen1335.equipmentEffectLib.utils.Cast;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.OperationType;
import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class MultiMul implements OperationUnit {
    public static final MapCodec<MultiMul> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CalculatorRegister.DISPATCH_CODEC.listOf().fieldOf("units").forGetter(multiMul -> multiMul.units)
    ).apply(instance, MultiMul::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MultiMul> STREAM_CODEC = StreamCodec.composite(
            CalculatorRegister.DISPATCH_STREAM_CODEC.apply(ByteBufCodecs.list()),
            value -> value.units,
            MultiMul::new
    );

    private final List<OperationUnit> units;

    public MultiMul(List<OperationUnit> units) {
        this.units = units;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        float i = 1;
        for (OperationUnit unit : units) {
            i = i * unit.getValue(calculatorArg);
        }

        return i;
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        MutableComponent component = Component.empty();
        component.append("(");
        for (OperationUnit unit : units) {
            component.append(unit.toComponent(calculatorArg));
            if (units.getLast() != unit) {
                component.append("x");
            }
        }
        component.append(")");
        return component;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends OperationUnit> getStreamCodec() {
        return Cast.cast(STREAM_CODEC);
    }

    @Override
    public MapCodec<? extends OperationUnit> getCodec() {
        return CODEC;
    }

    @Override
    public OperationType getType() {
        return CalculatorRegister.MULTI_MUL.get();
    }

    public static MultiMul of(OperationUnit... units) {
        return new MultiMul(List.of(units));
    }
}
