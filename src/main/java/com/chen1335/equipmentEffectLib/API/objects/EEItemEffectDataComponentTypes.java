package com.chen1335.equipmentEffectLib.API.objects;

import com.chen1335.legendaryRelics.API.objects.LRDataComponentTypes;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EEItemEffectDataComponentTypes {
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> EFFECT_LEVEL = LRDataComponentTypes.DATA_COMPONENTS.register("effect_level", () -> DataComponentType.<Integer>builder().networkSynchronized(ByteBufCodecs.INT).persistent(Codec.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> WEARER_ARMOR = LRDataComponentTypes.DATA_COMPONENTS.register("wearer_armor", () -> DataComponentType.<Double>builder().networkSynchronized(ByteBufCodecs.DOUBLE).persistent(Codec.DOUBLE).build());

    public static void init() {

    }
}
