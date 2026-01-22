package com.chen1335.legendaryRelics.common.calculator.special;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.api.Unit;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.util.Cast;

import java.util.HashMap;
import java.util.Map;

public class SingleCustomArg<T> implements Unit {

    public static final Map<ResourceLocation, SingleCustomArg<?>> REGISTERED_TYPE = new HashMap<>();

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
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Unit> getStreamCodec() {
        return Cast.cast(STREAM_CODEC);
    }

    //Caused by: dev.latvian.mods.rhino.EvaluatorException: Cannot convert 1.0 to R
    public interface NumberGetter<T> {
        Number get(T arg);
    }

    public static <T> SingleCustomArg<T> register(ResourceLocation id, CalculatorArg.ArgType<T> argType, NumberGetter<T> function, Component component) {
        return new SingleCustomArg<>(id, argType, function, component);
    }
}
