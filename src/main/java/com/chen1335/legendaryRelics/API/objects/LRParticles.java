package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.particles.WaveIceParticleOptions;
import com.chen1335.legendaryRelics.common.particles.WaveIceParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRParticles {
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, LegendaryRelics.MODID);

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }

    public static final DeferredHolder<ParticleType<?>, WaveIceParticleType> WAVE_ICE =
            PARTICLE_TYPES.register("wave_ice", () -> new WaveIceParticleType(false));

    /**
     * 在指定位置生成一个冰波粒子；服务端会自动同步给周围客户端。
     *
     * @param rollDegrees 生成时的朝向（度）
     * @param startAlpha  初始透明度（0 全透明 → 1 不透明）
     */
    public static void spawnWaveIce(Level level, double x, double y, double z, float rollDegrees, float startAlpha) {
        WaveIceParticleOptions options = WaveIceParticleOptions.of(rollDegrees, startAlpha);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(options, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        } else {
            level.addParticle(options, x, y, z, 0.0, 0.0, 0.0);
        }
    }
}
