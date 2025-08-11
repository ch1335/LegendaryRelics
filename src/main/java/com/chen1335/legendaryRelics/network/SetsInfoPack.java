package com.chen1335.legendaryRelics.network;

import com.chen1335.equipmentEffectLib.API.objects.RegisterTypes;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetsEffectBase;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.LRClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record SetsInfoPack(Map<SetsEffectBase, Integer> map) implements CustomPacketPayload {
    public static final Type<SetsInfoPack> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LegendaryRelics.MODID, "sets_info"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetsInfoPack> STREAM_CODEC = StreamCodec.of((buffer, value) -> {
        buffer.writeInt(value.map.size());
        value.map.forEach((setsEffectBase, integer) -> {
            buffer.writeResourceLocation(Objects.requireNonNull(RegisterTypes.SETS_EFFECT_TYPE.getKey(setsEffectBase)));
            buffer.writeInt(integer);
        });
    }, buffer -> {
        int size = buffer.readInt();
        Map<SetsEffectBase, Integer> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            SetsEffectBase setsEffectBase = RegisterTypes.SETS_EFFECT_TYPE.get(buffer.readResourceLocation());
            int count = buffer.readInt();
            map.put(setsEffectBase, count);
        }
        return new SetsInfoPack(map);
    });

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handler(IPayloadContext context) {
        LRClient.ENTITY_SETS_EFFECT_DATA = map;
    }
}
