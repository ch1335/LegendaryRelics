package com.chen1335.legendaryRelics.common;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.network.EffectCooldownPack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EquipmentEffectCooldownManager {
    private final Map<EffectType<?>, CooldownHolder> cooldownHolders = new HashMap<>();

    public void tick(LivingEntity living) {
        Iterator<Map.Entry<EffectType<?>, CooldownHolder>> iterator = cooldownHolders.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<EffectType<?>, CooldownHolder> entry = iterator.next();
            entry.getValue().tick();
            if (entry.getValue().finished()) {
                iterator.remove();
            }
        }

    }

    public void addCooldown(EffectType<?> effectType, int time) {
        cooldownHolders.put(effectType, new CooldownHolder(time));
    }

    public boolean isNotInCooldown(EffectType<?> effectType) {
        return !cooldownHolders.containsKey(effectType);
    }

    public static void addCooldown(LivingEntity living, EffectType<?> effectType, int time) {
        living.getData(LRAttachmentTypes.ENTITY_DATA).getEquipmentEffectCooldownManager().addCooldown(effectType, time);
        if (living instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new EffectCooldownPack(effectType, time));
        } else if (living.level().isClientSide) {
            LRClient.getClientCooldownManager().addCooldown(effectType, time);
        }
    }

    public static boolean isNotInCooldown(LivingEntity living, EffectType<?> effectType) {
        return living.getData(LRAttachmentTypes.ENTITY_DATA).getEquipmentEffectCooldownManager().isNotInCooldown(effectType);
    }

    public Map<EffectType<?>, CooldownHolder> getCooldownHolders() {
        return cooldownHolders;
    }

    public static class CooldownHolder {
        private int timeLeft;

        private int totalTime;

        public CooldownHolder(int timeLeft) {
            this.timeLeft = timeLeft;
            this.totalTime = timeLeft;
        }

        public void tick() {
            timeLeft--;
        }

        public boolean finished() {
            return timeLeft <= 0;
        }

        public int getTotalTime() {
            return totalTime;
        }

        public int getTimeLeft() {
            return timeLeft;
        }
    }
}
