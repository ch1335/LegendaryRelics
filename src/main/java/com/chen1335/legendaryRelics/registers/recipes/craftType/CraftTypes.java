package com.chen1335.legendaryRelics.registers.recipes.craftType;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class CraftTypes {
    public static final BiMap<ResourceLocation, MapCodec<? extends ICraftType>> CRAFT_CODECS = HashBiMap.create();
    public static final BiMap<ResourceLocation, StreamCodec<RegistryFriendlyByteBuf, ? extends ICraftType>> CRAFT_STREAM_CODECS = HashBiMap.create();
    public static final Codec<MapCodec<? extends ICraftType>> CODEC = ResourceLocation.CODEC.xmap(CRAFT_CODECS::get, CRAFT_CODECS.inverse()::get);

    public static final StreamCodec<RegistryFriendlyByteBuf, StreamCodec<RegistryFriendlyByteBuf, ? extends ICraftType>> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            codec -> CRAFT_STREAM_CODECS.inverse().get(codec),
            CRAFT_STREAM_CODECS::get
    );
    public static final Codec<ICraftType> DISPATCH_CODEC = CODEC.dispatch(ICraftType::codec, Function.identity());

    public static final StreamCodec<RegistryFriendlyByteBuf, ICraftType> DISPATCH_STREAM_CODEC = STREAM_CODEC.dispatch(ICraftType::streamCodec, Function.identity());

    public static <T extends ICraftType> void register(ResourceLocation resourceLocation, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        CRAFT_CODECS.put(resourceLocation, codec);
        CRAFT_STREAM_CODECS.put(resourceLocation, streamCodec);
    }
}
