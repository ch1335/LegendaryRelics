package com.chen1335.legendaryRelics.common.calculator;

import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record OperationType(MapCodec<? extends OperationUnit> codec, StreamCodec<RegistryFriendlyByteBuf, ? extends OperationUnit> streamCodec) {
}
