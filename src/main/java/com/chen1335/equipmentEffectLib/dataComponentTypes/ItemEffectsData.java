package com.chen1335.equipmentEffectLib.dataComponentTypes;

import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.logging.log4j.util.Cast;

import java.util.LinkedHashMap;
import java.util.Map;

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


    public ItemEffectsData withEffectAdded(BaseEffect effect) {
        ImmutableMap.Builder<EffectType<?>, BaseEffect> builder = ImmutableMap.builder();
        builder.putAll(effects);
        builder.put(effect.getType(), effect);
        return new ItemEffectsData(builder.build());
    }
}
