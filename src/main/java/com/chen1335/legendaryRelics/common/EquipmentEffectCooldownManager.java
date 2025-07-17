package com.chen1335.legendaryRelics.common;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import net.minecraft.world.entity.LivingEntity;

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

    public boolean isCooldown(EffectType<?> effectType) {
        return cooldownHolders.containsKey(effectType);
    }

    public static void addCooldown(LivingEntity living, EffectType<?> effectType, int time) {
        living.getData(LRAttachmentTypes.ENTITY_DATA).getEquipmentEffectCooldownManager().addCooldown(effectType, time);
    }

    public static boolean isCooldown(LivingEntity living, EffectType<?> effectType) {
        return living.getData(LRAttachmentTypes.ENTITY_DATA).getEquipmentEffectCooldownManager().isCooldown(effectType);
    }

    public static class CooldownHolder {
        private int timeLeft;

        public CooldownHolder(int timeLeft) {
            this.timeLeft = timeLeft;
        }

        public void tick() {
            timeLeft--;
        }

        public boolean finished() {
            return timeLeft <= 0;
        }
    }
}
