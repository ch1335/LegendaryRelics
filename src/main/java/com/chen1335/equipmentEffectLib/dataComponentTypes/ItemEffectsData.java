package com.chen1335.equipmentEffectLib.dataComponentTypes;

import com.chen1335.equipmentEffectLib.API.objects.RegisterTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record ItemEffectsData(Map<EffectType<?>, BaseEffect> effects) {


    public static final Codec<ItemEffectsData> CODEC = Codec.of(ItemEffectsData::encode, ItemEffectsData::decode);

    private static <T> DataResult<Pair<ItemEffectsData, T>> decode(DynamicOps<T> tDynamicOps, T t) {
        CompoundTag nbt = (CompoundTag) tDynamicOps.convertTo(NbtOps.INSTANCE, t);
        Map<EffectType<?>, BaseEffect> effects = new HashMap<>();
        if (nbt != null) {
            for (String key : nbt.getAllKeys()) {
                CompoundTag data = nbt.getCompound(key);
                DataResult<Pair<BaseEffect, Tag>> baseEffect = BaseEffect.CODEC.decode(NbtOps.INSTANCE, data);

                RegisterTypes.EQUIPMENT_EFFECT_TYPE.getHolder(ResourceLocation.parse(key)).ifPresent(effectType -> effects.put(effectType.value(), baseEffect.getOrThrow().getFirst()));
            }
        }
        return DataResult.success(Pair.of(new ItemEffectsData(effects), t));
    }

    private <T> DataResult<T> encode(DynamicOps<T> tDynamicOps, T t) {
        CompoundTag compoundTag = new CompoundTag();

        this.effects.forEach((effectType, effect) -> {
            DataResult<Tag> result = BaseEffect.CODEC.encodeStart(NbtOps.INSTANCE, effect);
            Tag tag = result.getOrThrow();
            compoundTag.put(RegisterTypes.EQUIPMENT_EFFECT_TYPE.getKey(effectType).toString(), tag);
        });


        return DataResult.success(NbtOps.INSTANCE.convertTo(tDynamicOps, compoundTag));
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemEffectsData> STREAM_CODEC = StreamCodec.of((buffer, value) -> {
        CompoundTag compoundTag = new CompoundTag();

        value.effects.forEach((effectType, effect) -> {
            DataResult<Tag> result = BaseEffect.CODEC.encodeStart(NbtOps.INSTANCE, effect);
            Tag tag = result.getOrThrow();
            compoundTag.put(RegisterTypes.EQUIPMENT_EFFECT_TYPE.getKey(effectType).toString(), tag);
        });
        buffer.writeNbt(compoundTag);
    }, byteBuf -> {
        CompoundTag nbt = byteBuf.readNbt();
        Map<EffectType<?>, BaseEffect> effects = new HashMap<>();
        if (nbt != null) {
            for (String key : nbt.getAllKeys()) {
                CompoundTag data = nbt.getCompound(key);
                DataResult<Pair<BaseEffect, Tag>> baseEffect = BaseEffect.CODEC.decode(NbtOps.INSTANCE, data);
                Holder<EffectType<?>> effectTypeHolder = RegisterTypes.EQUIPMENT_EFFECT_TYPE.getHolder(ResourceLocation.parse(key)).orElseThrow();
                effects.put(effectTypeHolder.value(), baseEffect.getOrThrow().getFirst());
            }
        }
        return new ItemEffectsData(effects);
    });


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ItemEffectsData other) {
            if (this.effects.size() != other.effects.size()) {
                return false;
            }

            for (Map.Entry<EffectType<?>, BaseEffect> entry : this.effects.entrySet()) {
                if (!other.effects.get(entry.getKey()).equals(entry.getValue())) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
