package com.chen1335.legendaryRelics.network;

import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.LRClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record SetsInfoPack(Map<SetEffect, Integer> map) implements CustomPacketPayload {
    public static final Type<SetsInfoPack> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LegendaryRelics.MODID, "sets_info"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetsInfoPack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    SetEffect.STREAM_CODEC,
                    ByteBufCodecs.INT,
                    8
            ),
            SetsInfoPack::map,
            SetsInfoPack::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handler(IPayloadContext context) {
        context.enqueueWork(()->{
            LRClient.ENTITY_SETS_EFFECT_DATA = map;
        });
    }
}
