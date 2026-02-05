package com.chen1335.legendaryRelics.client.particlePlayer;

import com.chen1335.legendaryRelics.armorSetEffect.InfernoArmorSetEffect;
import com.chen1335.legendaryRelics.network.PlayClientParticlePack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

public class ParticlePlayersHolder {

    public interface ParticlePlayer {
        void accept(Level level, CompoundTag arg);
    }

    private static final Map<String, ParticlePlayer> PARTICLE_PLAYER_MAP = new HashMap<>();

    public static void init() {
        PARTICLE_PLAYER_MAP.put("inferno_explosion", InfernoArmorSetEffect::playClientParticle);
    }

    public static void playParticles(Level level, String name, CompoundTag arg) {
        ParticlePlayer particlePlayer = PARTICLE_PLAYER_MAP.get(name);
        if (particlePlayer != null) {
            particlePlayer.accept(level, arg);
        }
    }

    public static void sendToPlayersTrackingEntity(LivingEntity target,String name,CompoundTag compoundTag){
        PacketDistributor.sendToPlayersTrackingEntity(target, new PlayClientParticlePack(name, compoundTag));
    }
}
