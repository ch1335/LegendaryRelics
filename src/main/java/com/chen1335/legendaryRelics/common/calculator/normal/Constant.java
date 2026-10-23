package com.chen1335.legendaryRelics.common.calculator.normal;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.OperationType;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class Constant implements OperationUnit {

    public static final MapCodec<Constant> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("value").forGetter(constant -> constant.amount),
            Codec.INT.optionalFieldOf("precision", 2).forGetter(constant -> constant.i)
    ).apply(instance, Constant::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Constant> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            constant -> constant.amount,
            ByteBufCodecs.INT,
            constant -> constant.i,
            Constant::new
    );

    private final float amount;
    private final int i;

    public Constant(float amount) {
        this(amount, 2);
    }

    public Constant(float amount, int i) {
        this.amount = amount;
        this.i = i;
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        return amount;
    }

    public Component toComponent(CalculatorArg calculatorArg) {
        return Component.literal(FinalCalculator.format(amount, i));
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

    public static Constant of(float amount, int i) {
        return new Constant(amount, i);
    }
}
