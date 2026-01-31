package com.chen1335.equipmentEffectLib.dataComponentTypes;

import com.chen1335.equipmentEffectLib.API.objects.EEItemDataComponentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Cast;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record ItemEffectsData(Map<EffectType<?>, BaseEffect> effects) {

    public static ItemEffectsData buildFromLinkedMap(Map<EffectType<?>, BaseEffect> map) {
        return new ItemEffectsData(ImmutableMap.copyOf(map));
    }

    public static ItemEffectsData EMPTY = new ItemEffectsData(Map.of());

    private static final Codec<Map<EffectType<?>, BaseEffect>> MAP_CODEC = Codec.unboundedMap(EERegisterTypes.EQUIPMENT_EFFECT_TYPE.byNameCodec(), BaseEffect.CODEC);

    public static final Codec<ItemEffectsData> CODEC = RecordCodecBuilder.create(i -> i.group(
            MAP_CODEC.fieldOf("effects").forGetter(ItemEffectsData::effects)
    ).apply(i, ItemEffectsData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemEffectsData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    LinkedHashMap::new,
                    ByteBufCodecs.registry(EERegisterTypes.EQUIPMENT_EFFECT_TYPE_KEY),
                    BaseEffect.STREAM_CODEC,
                    64
            ),
            ItemEffectsData::effects,
            ItemEffectsData::buildFromLinkedMap
    );

    public <T extends BaseEffect> T getEffect(EffectType<T> effectType) {
        return Cast.cast(effects.get(effectType));
    }


    public static void addEffect(ItemStack itemStack, BaseEffect effect) {
        @Nullable ItemEffectsData effectsData = itemStack.get(EEItemDataComponentTypes.ITEM_EFFECT_DATA);
        if (effectsData != null) {
            ImmutableMap.Builder<EffectType<?>, BaseEffect> builder = ImmutableMap.builder();
            builder.putAll(effectsData.effects);
            builder.put(effect.getType(), effect);
            itemStack.set(EEItemDataComponentTypes.ITEM_EFFECT_DATA, new ItemEffectsData(builder.build()));
        }
    }

    public static void removeEffect(ItemStack itemStack, EffectType<?> effectType) {
        @Nullable ItemEffectsData effectsData = itemStack.get(EEItemDataComponentTypes.ITEM_EFFECT_DATA);
        if (effectsData != null) {
            ImmutableMap.Builder<EffectType<?>, BaseEffect> builder = ImmutableMap.builder();
            effectsData.effects.forEach((effectType1, baseEffect) -> {
                if (!effectType1.equals(effectType)) {
                    builder.put(effectType1, baseEffect);
                }
            });

            itemStack.set(EEItemDataComponentTypes.ITEM_EFFECT_DATA, new ItemEffectsData(builder.build()));
        }
    }
}
