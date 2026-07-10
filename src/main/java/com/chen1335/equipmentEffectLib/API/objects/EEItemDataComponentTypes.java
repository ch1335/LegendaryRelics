package com.chen1335.equipmentEffectLib.API.objects;

import com.chen1335.equipmentEffectLib.EquipmentEffectLib;
import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.dataComponentTypes.SetEffectData;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EEItemDataComponentTypes {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.DataComponents.createDataComponents(Registries.DATA_COMPONENT_TYPE, EquipmentEffectLib.MODID);


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemEffectsData>> ITEM_EFFECT = DATA_COMPONENTS.register("item_effect", () -> DataComponentType.<ItemEffectsData>builder().networkSynchronized(ItemEffectsData.STREAM_CODEC).persistent(ItemEffectsData.CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemEffectsData>> ITEM_EFFECT_ADDITION = DATA_COMPONENTS.register("item_effect_addition", () -> DataComponentType.<ItemEffectsData>builder().networkSynchronized(ItemEffectsData.STREAM_CODEC).persistent(ItemEffectsData.CODEC).build());


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SetEffectData>> SET_EFFECT = DATA_COMPONENTS.register("set_effect", () -> DataComponentType.<SetEffectData>builder().networkSynchronized(SetEffectData.STREAM_CODEC).persistent(SetEffectData.CODEC).build());

}
