package com.chen1335.legendaryRelics.common.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/** 带参数的冰波粒子类型。 */
public class WaveIceParticleType extends ParticleType<WaveIceParticleOptions> {
    public WaveIceParticleType(boolean overrideLimiter) {
        super(overrideLimiter);
    }

    @Override
    public MapCodec<WaveIceParticleOptions> codec() {
        return WaveIceParticleOptions.CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, WaveIceParticleOptions> streamCodec() {
        return WaveIceParticleOptions.STREAM_CODEC;
    }
}
