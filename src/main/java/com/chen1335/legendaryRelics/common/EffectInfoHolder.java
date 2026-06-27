package com.chen1335.legendaryRelics.common;

import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.common.InfoHolder;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.utils.Cast;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;

import java.util.HashMap;
import java.util.Map;

public record EffectInfoHolder(
        Map<EffectType<?>, InfoHolder<? extends BaseEffect>> effectMap) {
    public static final Codec<EffectInfoHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(EERegisterTypes.EQUIPMENT_EFFECT_TYPE.byNameCodec(), InfoHolder.CODEC).fieldOf("effects").forGetter(EffectInfoHolder::effectMap)
    ).apply(instance, EffectInfoHolder::new));

    public EffectInfoHolder(Map<EffectType<?>, InfoHolder<? extends BaseEffect>> effectMap) {
        this.effectMap = new HashMap<>(effectMap);
    }

    public Tag serializeNBT(RegistryOps<Tag> registryOps) {
        DataResult<Tag> result = CODEC.encodeStart(registryOps, this);
        if (result.isSuccess()) {
            return result.getOrThrow();
        }
        return new CompoundTag();
    }

    public void deserializeNBT(RegistryOps<Tag> registryOps, Tag nbt) {
        DataResult<Pair<EffectInfoHolder, Tag>> decode = CODEC.decode(registryOps, nbt);
        decode.ifSuccess(pair -> {
            EffectInfoHolder first = pair.getFirst();
            effectMap.clear();
            effectMap.putAll(first.effectMap());
        });
    }

    public <T extends BaseEffect> InfoHolder<T> get(EffectType<T> effectType) {
        return Cast.cast(effectMap.get(effectType));
    }
}
