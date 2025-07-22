package com.chen1335.equipmentEffectLib.dataComponentTypes;

import com.chen1335.equipmentEffectLib.API.objects.RegisterTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.Holder;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record ItemEffectsData(Map<EffectType<?>, BaseEffect> effects) {

    public static ItemEffectsData EMPTY = new ItemEffectsData(Map.of());

    public static final Codec<ItemEffectsData> CODEC = Codec.of(ItemEffectsData::encode, ItemEffectsData::decode);

    private static <T> DataResult<Pair<ItemEffectsData, T>> decode(DynamicOps<T> tDynamicOps, T t) {
        ListTag listTag = (ListTag) tDynamicOps.convertTo(NbtOps.INSTANCE, t);
        ImmutableMap.Builder<EffectType<?>, BaseEffect> builder = ImmutableMap.builder();
        if (listTag != null) {
            for (Tag tag : listTag) {
                CompoundTag compoundTag = (CompoundTag) tag;
                DataResult<Pair<BaseEffect, Tag>> baseEffect = BaseEffect.CODEC.decode(NbtOps.INSTANCE, compoundTag);
                RegisterTypes.EQUIPMENT_EFFECT_TYPE.getHolder(ResourceLocation.parse(compoundTag.getString("EffectType"))).ifPresent(effectType -> builder.put(effectType.value(), baseEffect.getOrThrow().getFirst()));

            }
        }
        return DataResult.success(Pair.of(new ItemEffectsData(builder.build()), t));
    }

    private <T> DataResult<T> encode(DynamicOps<T> tDynamicOps, T t) {
        ListTag listTag = new ListTag();

        this.effects.forEach((effectType, effect) -> {
            DataResult<Tag> result = BaseEffect.CODEC.encodeStart(NbtOps.INSTANCE, effect);
            Tag tag = result.getOrThrow();
            listTag.add(tag);
        });


        return DataResult.success(NbtOps.INSTANCE.convertTo(tDynamicOps, listTag));
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemEffectsData> STREAM_CODEC = StreamCodec.of((buffer, value) -> {
        ListTag listTag = new ListTag();


        for (Map.Entry<EffectType<?>, BaseEffect> entry : value.effects.entrySet()) {
            DataResult<Tag> result = BaseEffect.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue());
            CompoundTag compoundTag = (CompoundTag) result.getOrThrow();
            listTag.add(compoundTag);
        }

        buffer.writeNbt(listTag);
    }, byteBuf -> {
        ListTag listTag = (ListTag) byteBuf.readNbt(NbtAccounter.create(2097152L));

        ImmutableMap.Builder<EffectType<?>, BaseEffect> builder = ImmutableMap.builder();
        if (listTag != null) {
            for (Tag tag : listTag) {
                CompoundTag compoundTag = (CompoundTag) tag;
                DataResult<Pair<BaseEffect, Tag>> baseEffect = BaseEffect.CODEC.decode(NbtOps.INSTANCE, compoundTag);
                Holder<EffectType<?>> effectTypeHolder = RegisterTypes.EQUIPMENT_EFFECT_TYPE.getHolder(ResourceLocation.parse(compoundTag.getString("EffectType"))).orElseThrow();
                builder.put(effectTypeHolder.value(), baseEffect.getOrThrow().getFirst());
            }
        }
        return new ItemEffectsData(builder.build());
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
