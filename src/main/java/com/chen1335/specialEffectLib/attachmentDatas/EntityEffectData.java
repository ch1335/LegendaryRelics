package com.chen1335.specialEffectLib.attachmentDatas;

import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class EntityEffectData implements INBTSerializable<CompoundTag> {
    public static final UUID NO_SOURCE_UUID = UUID.fromString("766e2f40-715f-4ca1-a9e2-a391a73b4ceb");
    public final Map<UUID, Map<MobEffectType<?>, SpecialMobEffect>> uuidMapMap = new HashMap<>();


    public void tick(LivingEntity livingEntity) {
        Iterator<Map<MobEffectType<?>, SpecialMobEffect>> i1 = uuidMapMap.values().iterator();
        while (i1.hasNext()) {
            Map<MobEffectType<?>, SpecialMobEffect> effectMap = i1.next();
            if (effectMap.isEmpty()) {
                i1.remove();
                break;
            }
            Iterator<Map.Entry<MobEffectType<?>, SpecialMobEffect>> iterator = effectMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<MobEffectType<?>, SpecialMobEffect> entry = iterator.next();
                SpecialMobEffect effect = entry.getValue();
                effect.tick(livingEntity);
                if (effect.isExpired()) {
                    iterator.remove();
                    effect.onRemove(livingEntity);
                }
            }
        }
    }

    public Map<MobEffectType<?>, SpecialMobEffect> getSourceEffects(UUID uuid) {
        return uuidMapMap.computeIfAbsent(uuid, uuid1 -> new HashMap<>());
    }

    public void putEffect(MobEffectType<?> mobEffectType, SpecialMobEffect effect) {
        uuidMapMap.computeIfAbsent(effect.getSourceEntityUUID(), uuid -> new HashMap<>()).put(mobEffectType, effect);
    }

    public boolean hasEffect(LivingEntity livingEntity, MobEffectType<?> mobEffectType) {
        for (Map<MobEffectType<?>, SpecialMobEffect> value : uuidMapMap.values()) {
            if (value.containsKey(mobEffectType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        ListTag listTag = new ListTag();
        for (Map<MobEffectType<?>, SpecialMobEffect> effectMap : uuidMapMap.values()) {
            for (SpecialMobEffect value : effectMap.values()) {
                listTag.add(value.save());
            }
        }
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Data",listTag);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag data) {
        for (Tag tag : data.getList("Data", Tag.TAG_COMPOUND)) {
            CompoundTag compoundTag = (CompoundTag) tag;
            MobEffectType<?> effectType = RegisterTypes.SPECIAL_EFFECT_TYPE.get(ResourceLocation.parse(compoundTag.getString("EffectType")));
            if (effectType != null) {
                SpecialMobEffect effect = effectType.create();
                effect.load(compoundTag);
                putEffect(effectType, effect);
            }
        }
    }


}
