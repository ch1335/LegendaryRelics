package com.chen1335.legendaryRelics.network;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.particlePlayer.ParticlePlayersHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PlayClientParticlePack(String name, CompoundTag arg) implements CustomPacketPayload {
    public static final Type<PlayClientParticlePack> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LegendaryRelics.MODID, "play_particle"));


    public static final StreamCodec<RegistryFriendlyByteBuf, PlayClientParticlePack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PlayClientParticlePack::name,
            ByteBufCodecs.COMPOUND_TAG,
            PlayClientParticlePack::arg,
            PlayClientParticlePack::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handler(IPayloadContext context) {
        if (context.player().level().isClientSide) {
            ParticlePlayersHolder.playParticles(context.player().level(),name, arg);
        }
    }
}
