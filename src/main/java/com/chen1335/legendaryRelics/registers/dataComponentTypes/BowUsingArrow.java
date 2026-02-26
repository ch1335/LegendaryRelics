package com.chen1335.legendaryRelics.registers.dataComponentTypes;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

public record BowUsingArrow(Item item) {

    public static final Codec<BowUsingArrow> CODEC = BuiltInRegistries.ITEM.byNameCodec().xmap(BowUsingArrow::new, BowUsingArrow::item);

    public static final StreamCodec<? super RegistryFriendlyByteBuf, BowUsingArrow> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ITEM),
            BowUsingArrow::item,
            BowUsingArrow::new
    );
}
