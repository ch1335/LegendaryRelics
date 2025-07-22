package com.chen1335.specialEffectLib.attachmentDatas;

import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EntityEffectData implements INBTSerializable<CompoundTag> {
    public final Map<MobEffectType<?>, SpecialMobEffect> effectMap = new HashMap<>();


    public void tick(LivingEntity livingEntity) {
        Iterator<Map.Entry<MobEffectType<?>, SpecialMobEffect>> iterator = effectMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<MobEffectType<?>, SpecialMobEffect> entry = iterator.next();
            SpecialMobEffect effect = entry.getValue();
            effect.tick(livingEntity);
            if (effect.isExpired()) {
                iterator.remove();
            }
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        effectMap.forEach((effectType, specialMobEffect) -> {
            ResourceLocation resourceLocation = RegisterTypes.SPECIAL_EFFECT_TYPE.getKey(effectType);
            if (resourceLocation != null) {
                compoundTag.put(resourceLocation.toString(), specialMobEffect.save());
            }
        });

        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        Set<String> keys = nbt.getAllKeys();
        for (String key : keys) {
            MobEffectType<?> mobEffectType = RegisterTypes.SPECIAL_EFFECT_TYPE.get(ResourceLocation.parse(key));
            if (mobEffectType != null) {
                CompoundTag compoundTag = (CompoundTag) nbt.get(key);
                SpecialMobEffect effect = mobEffectType.create();
                if (compoundTag != null) {
                    effect.load(compoundTag);
                    effectMap.put(mobEffectType, effect);
                }
            }
        }
    }
}
