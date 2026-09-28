package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.ITypeWithEntityData;
import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EntityEffectDates implements INBTSerializable<CompoundTag> {
    private final Map<EffectType<?>, ITypeWithEntityData<?>> effectDates = new HashMap<>();


    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        effectDates.forEach((effectType, iTypeWithEntityData) -> {
            if (iTypeWithEntityData instanceof INBTSerializable<?> serializable) {
                EERegisterTypes.EQUIPMENT_EFFECT_TYPE.getResourceKey(effectType).ifPresent(key->{
                    tag.put(key.toString(),serializable.serializeNBT(provider));
                });
            }
        });
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        for (String allKey : nbt.getAllKeys()) {
            Tag tag = nbt.get(allKey);
            EERegisterTypes.EQUIPMENT_EFFECT_TYPE.getHolder(ResourceLocation.parse(allKey)).ifPresent(holder->{

            });
        }
    }
}
