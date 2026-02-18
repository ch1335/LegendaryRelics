package com.chen1335.legendaryRelics.dataComponentTypes;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record BowUsingArrow(ItemStack itemStack) {

    public static final Codec<BowUsingArrow> CODEC = ItemStack.CODEC.xmap(BowUsingArrow::new, BowUsingArrow::itemStack);

    public static final StreamCodec<? super RegistryFriendlyByteBuf, BowUsingArrow> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            BowUsingArrow::itemStack,
            BowUsingArrow::new
    );
}
