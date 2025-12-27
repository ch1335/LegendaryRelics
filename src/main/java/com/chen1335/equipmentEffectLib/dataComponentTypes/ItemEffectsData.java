package com.chen1335.equipmentEffectLib.dataComponentTypes;

import com.chen1335.equipmentEffectLib.API.objects.RegisterTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record ItemEffectsData(Map<EffectType<?>, BaseEffect> effects) {


    public static ItemEffectsData EMPTY = new ItemEffectsData(Map.of());

    private static final Codec<Map<EffectType<?>, BaseEffect>> MAP_CODEC = Codec.unboundedMap(RegisterTypes.EQUIPMENT_EFFECT_TYPE.byNameCodec(), BaseEffect.CODEC);

    public static final Codec<ItemEffectsData> CODEC = RecordCodecBuilder.create(i -> i.group(
            MAP_CODEC.fieldOf("effects").forGetter(ItemEffectsData::effects)
    ).apply(i, ItemEffectsData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemEffectsData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.registry(RegisterTypes.EQUIPMENT_EFFECT_TYPE_KEY),
                    BaseEffect.STREAM_CODEC,
                    64
            ),
            ItemEffectsData::effects,
            ItemEffectsData::new
    );


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEffectsData that = (ItemEffectsData) o;
        return Objects.equals(effects, that.effects);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(effects);
    }
}
