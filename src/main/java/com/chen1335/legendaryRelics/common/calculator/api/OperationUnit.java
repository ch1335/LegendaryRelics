package com.chen1335.legendaryRelics.common.calculator.api;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.OperationType;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

public interface OperationUnit {
    float getValue(CalculatorArg calculatorArg);

    Component toComponent(CalculatorArg calculatorArg);

    StreamCodec<RegistryFriendlyByteBuf, ? extends OperationUnit> getStreamCodec();

    MapCodec<? extends OperationUnit> getCodec();

    OperationType getType();
}
