package com.chen1335.legendaryRelics.common;

import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.common.InfoHolder;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import org.apache.logging.log4j.util.Cast;

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
        return CODEC.encodeStart(registryOps, this).getOrThrow();
    }

    public void deserializeNBT(RegistryOps<Tag> registryOps, Tag nbt) {
        EffectInfoHolder first = CODEC.decode(registryOps, nbt).getOrThrow().getFirst();
        effectMap.clear();
        effectMap.putAll(first.effectMap());
    }

    public <T extends BaseEffect> InfoHolder<T> get(EffectType<T> effectType) {
        return Cast.cast(effectMap.get(effectType));
    }
}
