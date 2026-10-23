package com.chen1335.legendaryRelics.common.calculator.special;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.OperationType;
import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import com.chen1335.equipmentEffectLib.utils.Cast;

import java.util.HashMap;
import java.util.Map;

public class SingleCustomArg<T> implements OperationUnit {

    public static final Map<ResourceLocation, SingleCustomArg<?>> REGISTERED_TYPE = new HashMap<>();

    public static final MapCodec<SingleCustomArg<?>> CODEC = RecordCodecBuilder.<SingleCustomArg<?>>mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(arg -> arg.id)
    ).apply(instance, REGISTERED_TYPE::get)).flatXmap(
            arg -> arg == null ? DataResult.error(() -> "Unknown calculator argument") : DataResult.success(arg),
            DataResult::success
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SingleCustomArg<?>> STREAM_CODEC = StreamCodec.of(
            (buffer, value) -> {
                ByteBufCodecs.STRING_UTF8.encode(buffer, value.id.toString());
            },
            buffer -> REGISTERED_TYPE.get(ResourceLocation.parse(ByteBufCodecs.STRING_UTF8.decode(buffer)))
    );


    private final ResourceLocation id;
    private final CalculatorArg.ArgType<T> argType;
    private final NumberGetter<T> function;
    private final Component component;

    private SingleCustomArg(ResourceLocation id, CalculatorArg.ArgType<T> argType, NumberGetter<T> function, Component component) {
        this.id = id;
        this.argType = argType;
        this.function = function;
        this.component = component;
        REGISTERED_TYPE.put(id, this);
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        T arg = argType.getArg(calculatorArg);
        return function.get(arg).floatValue();
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
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
        return CalculatorRegister.SINGLE_CUSTOM.get();
    }

    //Caused by: dev.latvian.mods.rhino.EvaluatorException: Cannot convert 1.0 to R
    public interface NumberGetter<T> {
        Number get(T arg);
    }

    public static <T> SingleCustomArg<T> register(ResourceLocation id, CalculatorArg.ArgType<T> argType, NumberGetter<T> function, Component component) {
        return new SingleCustomArg<>(id, argType, function, component);
    }
}
