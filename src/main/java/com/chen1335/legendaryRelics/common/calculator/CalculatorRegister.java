package com.chen1335.legendaryRelics.common.calculator;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.api.Unit;
import com.chen1335.legendaryRelics.common.calculator.normal.*;
import com.chen1335.legendaryRelics.common.calculator.special.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CalculatorRegister {

    private static final Map<ResourceLocation, StreamCodec<RegistryFriendlyByteBuf, ? extends Unit>> REGISTERED_CALCULATOR_STREAM_CODEC = new HashMap<>();

    public static final StreamCodec<RegistryFriendlyByteBuf, StreamCodec<RegistryFriendlyByteBuf, ? extends Unit>> STREAM_CODEC = StreamCodec.of(
            (buffer, value) -> {
                for (Map.Entry<ResourceLocation, StreamCodec<RegistryFriendlyByteBuf, ? extends Unit>> entry : REGISTERED_CALCULATOR_STREAM_CODEC.entrySet()) {
                    if (entry.getValue() == value) {
                        ResourceLocation.STREAM_CODEC.encode(buffer, entry.getKey());
                        return;
                    }
                }
            },
            buffer -> REGISTERED_CALCULATOR_STREAM_CODEC.get(ResourceLocation.STREAM_CODEC.decode(buffer))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Unit> DISPATCH_STREAM_CODEC = STREAM_CODEC.dispatch(Unit::getStreamCodec, Function.identity());

    public static void init() {
        register("add", Add.STREAM_CODEC);
        register("constant", Constant.STREAM_CODEC);
        register("mul", Mul.STREAM_CODEC);
        register("multi_add", MultiAdd.STREAM_CODEC);
        register("multi_mul", MultiMul.STREAM_CODEC);

        register("dark_gold_update", DarkGoldUpdateArg.STREAM_CODEC);
        register("entity_attribute", EntityAttributeValue.STREAM_CODEC);
        register("equipment_effect_level", EquipmentEffectLevelArg.STREAM_CODEC);
        register("single_custom", SingleCustomArg.STREAM_CODEC);
        register("tiered_bonus", TieredBonus.STREAM_CODEC);
    }

    private static void register(String name, StreamCodec<RegistryFriendlyByteBuf, ? extends Unit> streamCodec) {
        REGISTERED_CALCULATOR_STREAM_CODEC.put(LegendaryRelics.id(name), streamCodec);
    }


}
