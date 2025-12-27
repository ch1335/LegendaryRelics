package com.chen1335.legendaryRelics.network;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorsHolder;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UpdateCalculatorPack(CalculatorsHolder.LocateInfo locateInfo,
                                   FinalCalculator unit) implements CustomPacketPayload {
    public static final Type<UpdateCalculatorPack> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LegendaryRelics.MODID, "update_calculator"));


    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateCalculatorPack> STREAM_CODEC = StreamCodec.composite(
            CalculatorsHolder.LocateInfo.STREAM_CODEC,
            UpdateCalculatorPack::locateInfo,
            FinalCalculator.STREAM_CODEC,
            UpdateCalculatorPack::unit,
            UpdateCalculatorPack::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handler(IPayloadContext context) {
        FinalCalculator finalCalculator = CalculatorsHolder.getCalculators().get(locateInfo);
        if (finalCalculator != null) {
            finalCalculator.define(unit);
        }
    }
}
