package com.chen1335.legendaryRelics.common.calculator.normal;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.OperationType;
import com.chen1335.legendaryRelics.common.calculator.api.FormatedOperation;
import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class Constant extends FormatedOperation {

    public static final MapCodec<Constant> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("value").forGetter(constant -> constant.amount),
            Format.CODEC.optionalFieldOf("format", Format.NORMAL).forGetter(constant -> constant.format)
    ).apply(instance, Constant::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Constant> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            constant -> constant.amount,
            Format.STREAM_CODEC,
            constant -> constant.format,
            Constant::new
    );

    private final float amount;

    public Constant(float amount) {
        this(amount, Format.NORMAL);
    }

    public Constant(float amount, Format format) {
        this.amount = amount;
        this.format = format;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        return amount;
    }

    public Component expand(CalculatorArg calculatorArg) {
        return formatValue(amount);
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
        return CalculatorRegister.CONSTANT.get();
    }

    public static Constant of(float amount) {
        return new Constant(amount);
    }

    public static Constant of(float amount, Format format) {
        return new Constant(amount, format);
    }
}
