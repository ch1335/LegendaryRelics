package com.chen1335.legendaryRelics.common.particles;

import com.chen1335.legendaryRelics.API.objects.LRParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * 冰波粒子参数。
 *
 * @param rollDegrees 生成时的朝向（绕视线轴旋转的角度，单位度）
 * @param startAlpha  生成时的初始透明度（0 全透明，1 不透明），之后会逐渐升高
 */
public record WaveIceParticleOptions(float rollDegrees, float startAlpha) implements ParticleOptions {
    public static final MapCodec<WaveIceParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("roll", 0.0F).forGetter(WaveIceParticleOptions::rollDegrees),
            Codec.FLOAT.optionalFieldOf("alpha", 0.0F).forGetter(WaveIceParticleOptions::startAlpha)
    ).apply(instance, WaveIceParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WaveIceParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, WaveIceParticleOptions::rollDegrees,
            ByteBufCodecs.FLOAT, WaveIceParticleOptions::startAlpha,
            WaveIceParticleOptions::new);

    public static WaveIceParticleOptions of(float rollDegrees, float startAlpha) {
        return new WaveIceParticleOptions(rollDegrees, startAlpha);
    }

    @Override
    public ParticleType<?> getType() {
        return LRParticles.WAVE_ICE.get();
    }
}
