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

public class MultiAdd implements OperationUnit {

    public static final MapCodec<MultiAdd> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CalculatorRegister.DISPATCH_CODEC.listOf().fieldOf("units").forGetter(multiAdd -> multiAdd.units)
    ).apply(instance, MultiAdd::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MultiAdd> STREAM_CODEC = StreamCodec.composite(
            CalculatorRegister.DISPATCH_STREAM_CODEC.apply(ByteBufCodecs.list()),
            value -> value.units,
            MultiAdd::new
    );

    private final List<OperationUnit> units;

    public MultiAdd(List<OperationUnit> units) {
        this.units = units;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        float i = 0;
        for (OperationUnit unit : units) {
            i += unit.getValue(calculatorArg);
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
                component.append("+");
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
        return CalculatorRegister.MULTI_ADD.get();
    }

    public static MultiAdd of(OperationUnit... units) {
        return new MultiAdd(List.of(units));
    }
}
