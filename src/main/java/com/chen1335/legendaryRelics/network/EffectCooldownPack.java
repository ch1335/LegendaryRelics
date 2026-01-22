package com.chen1335.legendaryRelics.network;

import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record EffectCooldownPack(EffectType<?> effectType, int cooldownTick) implements CustomPacketPayload {
    public static final Type<EffectCooldownPack> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LegendaryRelics.MODID, "effect_cooldown"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectCooldownPack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(EERegisterTypes.EQUIPMENT_EFFECT_TYPE_KEY), EffectCooldownPack::effectType,
            ByteBufCodecs.INT, EffectCooldownPack::cooldownTick,
            EffectCooldownPack::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handler(IPayloadContext context) {
        EquipmentEffectCooldownManager.addCooldown(context.player(), effectType, cooldownTick);
    }
}
