package com.chen1335.equipmentEffectLib.dataComponentTypes;

import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.common.SetEffectHolder;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record SetEffectData(Holder<SetEffect> setEffect, Holder<EquipmentType> equipmentType) {

    public static final Codec<SetEffectData> CODEC = RecordCodecBuilder.create(i -> i.group(
            EERegisterTypes.SETS_EFFECT_TYPE.holderByNameCodec().fieldOf("setEffect").forGetter(SetEffectData::setEffect),
            EERegisterTypes.EQUIPMENT_TYPE.holderByNameCodec().fieldOf("equipmentType").forGetter(SetEffectData::equipmentType)
    ).apply(i, SetEffectData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetEffectData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(EERegisterTypes.SETS_EFFECT_TYPE_KEY),
            SetEffectData::setEffect,
            ByteBufCodecs.holderRegistry(EERegisterTypes.EQUIPMENT_TYPE_KEY),
            SetEffectData::equipmentType,
            SetEffectData::new
    );

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SetEffectData that = (SetEffectData) o;
        return Objects.equals(setEffect, that.setEffect) && Objects.equals(equipmentType, that.equipmentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(setEffect, equipmentType);
    }

}
