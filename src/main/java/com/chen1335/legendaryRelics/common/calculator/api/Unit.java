package com.chen1335.legendaryRelics.common.calculator.api;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

public interface Unit {
    float getValue(CalculatorArg calculatorArg);

    Component toComponent(CalculatorArg calculatorArg);

    StreamCodec<RegistryFriendlyByteBuf, ? extends Unit> getStreamCodec();
}
